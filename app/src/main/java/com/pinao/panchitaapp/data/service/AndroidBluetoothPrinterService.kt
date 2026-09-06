package com.pinao.panchitaapp.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val RFCOMM_CHANNEL = 1
    }

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

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
            emptyList()
        }
    }

    /**
     * Intenta conectar al dispositivo Bluetooth usando tres métodos en orden:
     * 1. RFCOMM estándar seguro (SDP lookup)
     * 2. RFCOMM inseguro (sin cifrado)
     * 3. Reflexión a canal RFCOMM fijo (canal 1) — fix común para impresoras chinas
     */
    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice): BluetoothSocket {
        // Método 1: Conexión segura estándar
        try {
            val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()
            return socket
        } catch (_: IOException) {}

        // Método 2: Conexión insegura
        try {
            val socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()
            return socket
        } catch (_: IOException) {}

        // Método 3: Reflexión — canal RFCOMM fijo (canal 1)
        // Solución conocida para módulos Bluetooth genéricos chinos
        val method = device.javaClass.getMethod(
            "createRfcommSocket",
            Int::class.javaPrimitiveType
        )
        val socket = method.invoke(device, RFCOMM_CHANNEL) as BluetoothSocket
        socket.connect()
        return socket
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

            // Cancelar descubrimiento antes de conectar
            adapter.cancelDiscovery()

            // Conectar usando el método más compatible
            socket = connectToDevice(device)

            // Esperar a que el enlace SPP se estabilice
            delay(1000L)

            outputStream = socket.outputStream

            // Formatear e imprimir el ticket con comandos ESC/POS.
            val printData = generateEscPosBytes(ticket, products, clientName, clientDoc)
            outputStream.write(printData)

            // Esperar a que termine la transmisión
            outputStream.flush()
            delay(2000L)

            Result.success(Unit)
        } catch (se: SecurityException) {
            Result.failure(Exception("Permiso de Bluetooth denegado. Conceda permisos de Bluetooth en la configuración.", se))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión con la impresora. Asegúrese de que esté encendida y al alcance.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado en la impresión: ${e.message}", e))
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { socket?.close() } catch (_: Exception) {}
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

        val init = byteArrayOf(0x1B, 0x40)
        val alignCenter = byteArrayOf(0x1B, 0x61, 0x01)
        val alignLeft = byteArrayOf(0x1B, 0x61, 0x00)
        val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
        val boldOff = byteArrayOf(0x1B, 0x45, 0x00)
        val doubleSizeOn = byteArrayOf(0x1D, 0x21, 0x11)
        val doubleSizeOff = byteArrayOf(0x1D, 0x21, 0x00)
        val newLine = byteArrayOf(0x0A)

        fun writeBytes(bytes: ByteArray) {
            list.addAll(bytes.toList())
        }

        fun writeText(
            text: String,
            alignment: ByteArray = alignLeft,
            bold: Boolean = false,
            doubleSize: Boolean = false
        ) {
            writeBytes(alignment)
            writeBytes(if (bold) boldOn else boldOff)
            writeBytes(if (doubleSize) doubleSizeOn else doubleSizeOff)
            writeBytes(cleanText(text).toByteArray(charset("CP1252")))
            writeBytes(newLine)
        }

        val storeName = sessionManager.getStoreName()?.trim()?.takeIf { it.isNotEmpty() } ?: "BODEGA PANCHITA"
        val storeRuc = sessionManager.getStoreRuc()?.trim()?.takeIf { it.isNotEmpty() } ?: "10452391032"
        val storeAddress = sessionManager.getStoreAddress()?.trim()?.takeIf { it.isNotEmpty() } ?: "Av. El Sol 456 - Cusco"
        val storePhone = sessionManager.getStorePhone()?.trim()?.takeIf { it.isNotEmpty() } ?: "984523190"

        writeBytes(init)

        writeText(storeName, alignCenter, bold = true, doubleSize = true)
        writeText("R.U.C. $storeRuc", alignCenter)
        writeText(storeAddress, alignCenter)
        if (storePhone.isNotEmpty()) {
            writeText("Telf: $storePhone", alignCenter)
        }

        writeText("================================================", alignCenter)
        writeText("TICKET DE VENTA: #${ticket.saleId}", alignLeft, bold = true)
        writeText("FECHA: ${ticket.saleDate}", alignLeft)

        val normalizedClientName = clientName.trim()
        val normalizedClientDoc = clientDoc.trim()
        if (normalizedClientName.isNotEmpty()) {
            writeText("CLIENTE: $normalizedClientName", alignLeft)
        }
        if (normalizedClientDoc.isNotEmpty()) {
            writeText("DOCUMENTO: $normalizedClientDoc", alignLeft)
        }

        writeText("================================================", alignCenter)

        val header = String.format(
            java.util.Locale.getDefault(),
            "%-6s%-22s%10s%10s",
            "Cant", "Producto", "P.Unit", "Total"
        )
        writeText(header, alignLeft, bold = true)
        writeText("------------------------------------------------", alignCenter)

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
            writeText(line, alignLeft)
        }

        writeText("------------------------------------------------", alignCenter)

        val totalText = "S/. %.2f".format(ticket.totalAmount)
        val totalLine = String.format(
            java.util.Locale.getDefault(),
            "%-30s%18s",
            "TOTAL A PAGAR:", totalText
        )
        writeText(totalLine, alignLeft, bold = true, doubleSize = true)

        writeText("================================================", alignCenter)
        writeText("Gracias por su preferencia!", alignCenter, bold = true)
        writeText("Vuelva pronto!", alignCenter)

        repeat(4) { writeBytes(newLine) }
        writeBytes(byteArrayOf(0x1D, 0x56, 0x42, 0x00))

        return list.toByteArray()
    }

    /**
     * Limpia los caracteres que no son compatibles con la tabla de caracteres de la impresora.
     */
    private fun cleanText(text: String): String {
        return text
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
            .replace("ñ", "n").replace("Ñ", "N")
            .replace("¡", "").replace("¿", "")
    }
}
