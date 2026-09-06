package com.pinao.panchitaapp.data.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.model.UsbDeviceModel
import com.pinao.panchitaapp.domain.service.UsbPrinterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación de [UsbPrinterService] para la plataforma Android usando [UsbManager].
 * Transmite datos directamente a impresoras térmicas conectadas por cable USB OTG mediante Bulk Transfer.
 */
class AndroidUsbPrinterService(
    private val context: Context,
    private val sessionManager: SessionManager
) : UsbPrinterService {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.pinao.panchitaapp.USB_PERMISSION"
        private val PRINTER_CHARSET = charset("Windows-1252")
    }

    private val usbManager: UsbManager? by lazy {
        context.getSystemService(Context.USB_SERVICE) as? UsbManager
    }

    override fun getConnectedPrinters(): List<UsbDeviceModel> {
        val manager = usbManager ?: return emptyList()
        val deviceList = manager.deviceList
        val printerDevices = mutableListOf<UsbDeviceModel>()

        for (device in deviceList.values) {
            if (isPrinterDevice(device)) {
                val name = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    "${device.productName ?: "Impresora USB"} (${device.vendorId}:${device.productId})"
                } else {
                    "Impresora USB (${device.vendorId}:${device.productId})"
                }
                printerDevices.add(
                    UsbDeviceModel(
                        name = name,
                        deviceId = device.deviceId,
                        vendorId = device.vendorId,
                        productId = device.productId
                    )
                )
            }
        }
        return printerDevices
    }

    /**
     * Evalúa si un dispositivo USB es una impresora o posee una interfaz con endpoint Bulk OUT.
     */
    private fun isPrinterDevice(device: UsbDevice): Boolean {
        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_PRINTER) {
                return true
            }
            // Fallback para impresoras genéricas chinas que usan USB_CLASS_PER_INTERFACE o CDC/Vendor
            for (j in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(j)
                if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    endpoint.direction == UsbConstants.USB_DIR_OUT
                ) {
                    return true
                }
            }
        }
        return false
    }

    override fun hasPermission(deviceId: Int): Boolean {
        val manager = usbManager ?: return false
        val device = manager.deviceList.values.firstOrNull { it.deviceId == deviceId } ?: return false
        return manager.hasPermission(device)
    }

    override fun requestPermission(deviceId: Int) {
        val manager = usbManager ?: return
        val device = manager.deviceList.values.firstOrNull { it.deviceId == deviceId } ?: return
        if (!manager.hasPermission(device)) {
            val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val intent = Intent(ACTION_USB_PERMISSION).apply {
                `package` = context.packageName
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
            manager.requestPermission(device, pendingIntent)
        }
    }

    override suspend fun printTicket(
        deviceId: Int,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val manager = usbManager ?: return@withContext Result.failure(
            Exception("USB Manager no está disponible en este dispositivo.")
        )

        val device = manager.deviceList.values.firstOrNull { it.deviceId == deviceId }
            ?: return@withContext Result.failure(
                Exception("No se encontró el dispositivo USB seleccionado. Asegúrese de que el cable OTG esté bien conectado.")
            )

        if (!manager.hasPermission(device)) {
            return@withContext Result.failure(
                Exception("No se cuenta con permiso para acceder a la impresora USB. Selecciónela de nuevo en Ajustes para conceder permiso.")
            )
        }

        var targetInterface: UsbInterface? = null
        var targetEndpoint: UsbEndpoint? = null

        // Buscar la interfaz y endpoint Bulk OUT adecuados
        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            for (j in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(j)
                if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    endpoint.direction == UsbConstants.USB_DIR_OUT
                ) {
                    targetInterface = usbInterface
                    targetEndpoint = endpoint
                    break
                }
            }
            if (targetEndpoint != null) break
        }

        if (targetInterface == null || targetEndpoint == null) {
            return@withContext Result.failure(
                Exception("No se encontró un canal de comunicación de salida (Bulk OUT) en el dispositivo USB.")
            )
        }

        val connection = manager.openDevice(device)
            ?: return@withContext Result.failure(
                Exception("No se pudo abrir la conexión con la impresora USB.")
            )

        try {
            if (!connection.claimInterface(targetInterface, true)) {
                return@withContext Result.failure(
                    Exception("No se pudo reclamar el control exclusivo de la interfaz USB de la impresora.")
                )
            }

            val printData = generateEscPosBytes(ticket, products, clientName, clientDoc)

            // Transferir los datos en bloques para evitar desbordamiento del buffer USB
            val chunkSize = 4096
            var offset = 0
            while (offset < printData.size) {
                val length = minOf(chunkSize, printData.size - offset)
                val buffer = printData.copyOfRange(offset, offset + length)
                val bytesTransferred = connection.bulkTransfer(targetEndpoint, buffer, buffer.size, 5000)
                if (bytesTransferred <= 0) {
                    return@withContext Result.failure(
                        Exception("La transferencia USB no avanzó; revise la conexión con la impresora.")
                    )
                }
                offset += bytesTransferred
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado en la impresión USB: ${e.message}", e))
        } finally {
            try {
                connection.releaseInterface(targetInterface)
                connection.close()
            } catch (_: Exception) {}
        }
    }

    /**
     * Genera la secuencia completa de bytes ESC/POS formateada para la impresora de 80mm.
     */
    private fun generateEscPosBytes(
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): ByteArray {
        val list = mutableListOf<Byte>()

        val INIT = byteArrayOf(0x1B, 0x40)
        val SET_CODEPAGE_WPC1252 = byteArrayOf(0x1B, 0x74, 0x10)

        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)

        val MODE_NORMAL = byteArrayOf(0x1B, 0x21, 0x00)
        val MODE_BOLD = byteArrayOf(0x1B, 0x21, 0x08)
        val MODE_LARGE_BOLD = byteArrayOf(0x1B, 0x21, 0x38)

        val NEW_LINE = byteArrayOf(0x0A)

        fun writeBytes(bytes: ByteArray) {
            list.addAll(bytes.toList())
        }

        fun writeText(text: String, alignment: ByteArray = ALIGN_LEFT, bold: Boolean = false, doubleSize: Boolean = false) {
            val mode = when {
                doubleSize -> MODE_LARGE_BOLD
                bold -> MODE_BOLD
                else -> MODE_NORMAL
            }
            writeBytes(alignment)
            writeBytes(mode)
            writeBytes(text.toByteArray(PRINTER_CHARSET))
            writeBytes(NEW_LINE)
        }

        val storeName = sessionManager.getStoreName()?.trim()?.takeIf { it.isNotEmpty() } ?: "BODEGA PANCHITA"
        val storeRuc = sessionManager.getStoreRuc()?.trim()?.takeIf { it.isNotEmpty() } ?: "10452391032"
        val storeAddress = sessionManager.getStoreAddress()?.trim()?.takeIf { it.isNotEmpty() } ?: "Av. El Sol 456 - Cusco"
        val storePhone = sessionManager.getStorePhone()?.trim()?.takeIf { it.isNotEmpty() } ?: "984523190"

        // 1. Inicializar y CodePage WPC1252 (español)
        writeBytes(INIT)
        writeBytes(SET_CODEPAGE_WPC1252)

        // 2. Cabecera del Negocio
        writeText(storeName, ALIGN_CENTER, bold = true, doubleSize = true)
        writeText("R.U.C. $storeRuc", ALIGN_CENTER)
        writeText(storeAddress, ALIGN_CENTER)
        if (storePhone.isNotEmpty()) {
            writeText("Telf: $storePhone", ALIGN_CENTER)
        }

        writeText("================================================", ALIGN_CENTER)

        // 3. Información del Ticket
        writeText("TICKET DE VENTA: #${ticket.saleId}", ALIGN_LEFT, bold = true)
        writeText("FECHA: ${ticket.saleDate}", ALIGN_LEFT)

        val normalizedClientName = clientName.trim()
        val normalizedClientDoc = clientDoc.trim()
        if (normalizedClientName.isNotEmpty()) {
            writeText("CLIENTE: $normalizedClientName", ALIGN_LEFT)
        }
        if (normalizedClientDoc.isNotEmpty()) {
            writeText("DOCUMENTO: $normalizedClientDoc", ALIGN_LEFT)
        }

        writeText("================================================", ALIGN_CENTER)

        // 4. Cabecera de Columnas
        val header = String.format(
            java.util.Locale.getDefault(),
            "%-6s%-22s%10s%10s",
            "Cant", "Producto", "P.Unit", "Total"
        )
        writeText(header, ALIGN_LEFT, bold = true)
        writeText("------------------------------------------------", ALIGN_CENTER)

        // 5. Productos
        products.forEach { product ->
            val qtyStr = "x${product.stockQuantity.toInt()}"
            val nameStr = if (product.name.length > 22) product.name.substring(0, 20) + ".." else product.name
            val unitPriceStr = "S/.%.2f".format(product.priceSell)
            val totalPriceStr = "S/.%.2f".format(product.priceSell * product.stockQuantity)

            val line = String.format(
                java.util.Locale.getDefault(),
                "%-6s%-22s%10s%10s",
                qtyStr, nameStr, unitPriceStr, totalPriceStr
            )
            writeText(line, ALIGN_LEFT)
        }

        writeText("------------------------------------------------", ALIGN_CENTER)

        // 6. Total
        val totalText = "S/. %.2f".format(ticket.totalAmount)
        val totalLine = String.format(
            java.util.Locale.getDefault(),
            "%-30s%18s",
            "TOTAL A PAGAR:", totalText
        )
        writeText(totalLine, ALIGN_LEFT, bold = true, doubleSize = true)

        writeText("================================================", ALIGN_CENTER)

        // 7. Agradecimiento
        writeText("Gracias por su preferencia!", ALIGN_CENTER, bold = true)
        writeText("Vuelva pronto!", ALIGN_CENTER)

        // 8. Avance de papel y Corte
        val FEED_LINES = byteArrayOf(0x1B, 0x64, 0x04)
        val FULL_CUT = byteArrayOf(0x1D, 0x56, 0x00)
        val PARTIAL_CUT = byteArrayOf(0x1D, 0x56, 0x01)

        writeBytes(FEED_LINES)
        writeBytes(FULL_CUT)
        writeBytes(PARTIAL_CUT)

        return list.toByteArray()
    }
}
