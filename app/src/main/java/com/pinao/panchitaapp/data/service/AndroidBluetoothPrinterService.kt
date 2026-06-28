package com.pinao.panchitaapp.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

/**
 * Implementación de [BluetoothPrinterService] para la plataforma Android.
 * Se conecta a impresoras térmicas Bluetooth de 80mm (48 columnas) mediante SPP (Serial Port Profile).
 */
class AndroidBluetoothPrinterService(
    private val context: Context,
    private val sessionManager: SessionManager
) : BluetoothPrinterService {

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    // SPP UUID estándar para impresoras Bluetooth térmicas
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    @SuppressLint("MissingPermission")
    override fun getPairedPrinters(): List<BluetoothDeviceModel> {
        return try {
            val bondedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
            bondedDevices.map { device ->
                BluetoothDeviceModel(
                    name = device.name ?: "Dispositivo Desconocido",
                    address = device.address
                )
            }
        } catch (e: SecurityException) {
            // Si no se tienen permisos en runtime, se propaga o retorna vacío
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun printTicket(
        deviceAddress: String,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val adapter = bluetoothAdapter ?: return@withContext Result.failure(
            Exception("Bluetooth no está disponible en este dispositivo.")
        )

        if (!adapter.isEnabled) {
            return@withContext Result.failure(
                Exception("El Bluetooth está apagado. Por favor, enciéndalo para imprimir.")
            )
        }

        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null

        try {
            val device = adapter.getRemoteDevice(deviceAddress)
            // Conexión RFCOMM SPP estándar
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            
            // Cancelar el descubrimiento para acelerar la conexión
            adapter.cancelDiscovery()
            
            socket.connect()
            outputStream = socket.outputStream

            // Formatear e imprimir el ticket
            val printData = generateEscPosBytes(ticket, products, clientName, clientDoc)
            outputStream.write(printData)
            outputStream.flush()

            Result.success(Unit)
        } catch (se: SecurityException) {
            Result.failure(Exception("Permiso de Bluetooth denegado. Conceda permisos de Bluetooth en la configuración.", se))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión con la impresora. Asegúrese de que esté encendida y al alcance.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado en la impresión: ${e.message}", e))
        } finally {
            try {
                outputStream?.close()
                socket?.close()
            } catch (e: Exception) {
                // Ignorar error al cerrar socket
            }
        }
    }

    /**
     * Genera la secuencia de bytes ESC/POS formateada para impresoras de 80mm (48 columnas).
     */
    private fun generateEscPosBytes(
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): ByteArray {
        val list = mutableListOf<Byte>()

        // Comandos ESC/POS básicos
        val INIT = byteArrayOf(0x1B, 0x40)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
        val DOUBLE_SIZE_ON = byteArrayOf(0x1D, 0x21, 0x11)
        val DOUBLE_SIZE_OFF = byteArrayOf(0x1D, 0x21, 0x00)
        val NEW_LINE = byteArrayOf(0x0A)

        fun writeBytes(bytes: ByteArray) {
            list.addAll(bytes.toList())
        }

        fun writeText(text: String, alignment: ByteArray = ALIGN_LEFT, bold: Boolean = false, doubleSize: Boolean = false) {
            writeBytes(alignment)
            writeBytes(if (bold) BOLD_ON else BOLD_OFF)
            writeBytes(if (doubleSize) DOUBLE_SIZE_ON else DOUBLE_SIZE_OFF)
            
            // Reemplazar caracteres con acentos para evitar símbolos extraños en las impresoras
            val clean = cleanText(text)
            writeBytes(clean.toByteArray(charset("CP1252")))
            writeBytes(NEW_LINE)
        }

        val storeName = sessionManager.getStoreName()?.trim()?.takeIf { it.isNotEmpty() } ?: "BODEGA PANCHITA"
        val storeRuc = sessionManager.getStoreRuc()?.trim()?.takeIf { it.isNotEmpty() } ?: "10452391032"
        val storeAddress = sessionManager.getStoreAddress()?.trim()?.takeIf { it.isNotEmpty() } ?: "Av. El Sol 456 - Cusco"
        val storePhone = sessionManager.getStorePhone()?.trim()?.takeIf { it.isNotEmpty() } ?: "984523190"

        // 1. Inicializar
        writeBytes(INIT)

        // 2. Cabecera del Negocio (Centrado)
        writeText(storeName, ALIGN_CENTER, bold = true, doubleSize = true)
        writeText("R.U.C. $storeRuc", ALIGN_CENTER)
        writeText(storeAddress, ALIGN_CENTER)
        if (storePhone.isNotEmpty()) {
            writeText("Telf: $storePhone", ALIGN_CENTER)
        }
        
        // Separador
        writeText("================================================", ALIGN_CENTER)

        // 3. Información del Ticket e Identificación de Venta
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
        
        // Separador de cabecera de columnas
        writeText("================================================", ALIGN_CENTER)
        
        // Cabecera de Columnas: Cant (6), Producto (22), P.Unit (10), Total (10) = 48 columnas
        val header = String.format(
            java.util.Locale.getDefault(),
            "%-6s%-22s%10s%10s",
            "Cant", "Producto", "P.Unit", "Total"
        )
        writeText(header, ALIGN_LEFT, bold = true)
        writeText("------------------------------------------------", ALIGN_CENTER)

        // 4. Lista de Productos
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

        // Separador final
        writeText("------------------------------------------------", ALIGN_CENTER)

        // 5. Total a Pagar (Alineado a la derecha)
        val totalText = "S/. %.2f".format(ticket.totalAmount)
        val totalLine = String.format(
            java.util.Locale.getDefault(),
            "%-30s%18s",
            "TOTAL A PAGAR:", totalText
        )
        writeText(totalLine, ALIGN_LEFT, bold = true, doubleSize = true)

        writeText("================================================", ALIGN_CENTER)
        
        // 6. Mensaje de Agradecimiento
        writeText("Gracias por su preferencia!", ALIGN_CENTER, bold = true)
        writeText("Vuelva pronto!", ALIGN_CENTER)
        
        // Espacio para corte manual
        writeBytes(NEW_LINE)
        writeBytes(NEW_LINE)
        writeBytes(NEW_LINE)
        writeBytes(NEW_LINE)

        // 7. Precorte obligatorio parcial (GS V B 0)
        val PARTIAL_CUT = byteArrayOf(0x1D, 0x56, 0x42, 0x00)
        writeBytes(PARTIAL_CUT)

        return list.toByteArray()
    }

    /**
     * Limpia el texto reemplazando acentos y caracteres especiales para asegurar compatibilidad
     * con cualquier impresora térmica ESC/POS.
     */
    private fun cleanText(text: String): String {
        return text
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
            .replace("ñ", "n").replace("Ñ", "N")
            .replace("¡", "").replace("¿", "")
    }
}
