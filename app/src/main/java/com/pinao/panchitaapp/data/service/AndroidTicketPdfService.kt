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

class AndroidTicketPdfService(
    private val context: Context
) : TicketPdfService {

    override suspend fun generateAndSaveTicket(
        ticket: SaleModel,
        products: List<ProductModel>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            drawTicketContent(canvas, ticket, products)

            pdfDocument.finishPage(page)

            val fileName = "Ticket_${ticket.saleId}_${System.currentTimeMillis()}.pdf"
            savePdfToFile(pdfDocument, fileName)

            pdfDocument.close()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
            val line2 = String.format("x%-3.0f S/.%6.2f S/.%6.2f",
                product.stockQuantity, product.priceSell, product.priceSell * product.stockQuantity)
            canvas.drawText( line2, 40f, y, textPaint)
            y += 20f
        }

        y += 10f
        canvas.drawText("--------------------------------", 40f, y, textPaint)
        y += 30f
        canvas.drawText("TOTAL A PAGAR: S/. ${ticket.totalAmount}", 40f, y, titlePaint)
    }

    private fun savePdfToFile(pdfDocument: PdfDocument, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { context.contentResolver.openOutputStream(it)?.use { pdfDocument.writeTo(it) } }
        } else {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            pdfDocument.writeTo(FileOutputStream(file))
        }
    }
}