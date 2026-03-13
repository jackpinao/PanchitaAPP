package com.pinao.panchitaapp.data.service

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.TicketPdfService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Implementación de [TicketPdfService] para Android.
 * Genera un ticket de venta en formato PDF y lo guarda en la carpeta de Descargas.
 */
class AndroidTicketPdfService(
    private val context: Context
) : TicketPdfService {

    override suspend fun generateAndSaveTicket(
        ticket: SaleModel,
        products: List<ProductModel>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        var pdfDocument: PdfDocument? = null
        try {
            pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            
            drawTicketContent(page.canvas, ticket, products)
            pdfDocument.finishPage(page)

            val fileName = "Ticket_${ticket.saleId}_${System.currentTimeMillis()}.pdf"

            savePdf(pdfDocument, fileName)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            pdfDocument?.close()
        }
    }

    /**
     * Dibuja el contenido del ticket en el canvas del PDF.
     */
    private fun drawTicketContent(canvas: Canvas, ticket: SaleModel, products: List<ProductModel>) {
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
            color = Color.BLACK
        }
        val textPaint = Paint().apply {
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textSize = 12f
            color = Color.BLACK
        }

        var y = 50f
        canvas.drawText("Bodega 'El Chasqui'", 40f, y, titlePaint)
        y += 30f
        canvas.drawText("Ticket ID: ${ticket.saleId}", 40f, y, textPaint)
        y += 20f
        canvas.drawText("Fecha: ${ticket.saleDate}", 40f, y, textPaint)
        y += 30f
        canvas.drawText("------------------------------------", 40f, y, textPaint)
        y += 20f
        canvas.drawText("Producto   Cant.  P.unit   P.total", 40f, y, textPaint)
        y += 10f
        canvas.drawText("------------------------------------", 40f, y, textPaint)
        y += 20f

        products.forEach { product ->
            val line = String.format("%-15s", product.name.take(15))
            canvas.drawText(line, 40f, y, textPaint)
            y += 20f
            val line2 = String.format(
                locale = java.util.Locale.getDefault(),
                "x%-3.0f S/.%6.2f S/.%6.2f",
                product.stockQuantity, product.priceSell, product.priceSell * product.stockQuantity
            )
            canvas.drawText(line2, 40f, y, textPaint)
            y += 20f
        }

        y += 10f
        canvas.drawText("--------------------------------", 40f, y, textPaint)
        y += 30f
        canvas.drawText("TOTAL A PAGAR: S/. ${ticket.totalAmount}", 40f, y, titlePaint)
    }

    /**
     * Guarda el [PdfDocument] en el almacenamiento externo.
     * Usa MediaStore para Android 10+ (API 29+) para cumplir con Scoped Storage.
     */
    private fun savePdf(pdfDocument: PdfDocument, fileName: String) {
        val outputStream: OutputStream?
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Lógica para Android 10 o superior (Scoped Storage)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("No se pudo crear el archivo en Descargas")

            outputStream = resolver.openOutputStream(uri)
        } else {
            // Lógica para Android 9 o inferior (Legacy Storage)
            @Suppress("DEPRECATION")
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!directory.exists()) directory.mkdirs()
            
            val file = File(directory, fileName)
            outputStream = FileOutputStream(file)
        }

        outputStream?.use { 
            pdfDocument.writeTo(it) 
        } ?: throw Exception("No se pudo abrir el flujo de escritura")
    }
}
