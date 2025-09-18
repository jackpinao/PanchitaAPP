package com.pinao.panchitaapp.presentation.ui.guiaremision

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class GuiaRemisionViewModel(
    private val applicationContext: Context,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val findCodeProductUseCase: FindCodeProductUseCase,
    private val saveProductsUseCase: SaveProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val saveClientUseCase: SaveClientUseCase,
) : ViewModel() {

    private val _guiaRemisionUiState =
        MutableStateFlow<GuiaRemisionUiState>(GuiaRemisionUiState.Loading)
    val guiaRemisionUiState: StateFlow<GuiaRemisionUiState> = _guiaRemisionUiState.asStateFlow()
    private val _productsUiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState.asStateFlow()

    // Datos de los productos
    val code = System.currentTimeMillis().toString()
    private val _codeProduct = MutableStateFlow<String>(code)
    val codeProduct: StateFlow<String> = _codeProduct.asStateFlow()
    private val _nameProduct = MutableStateFlow<String>("")
    val nameProduct: StateFlow<String> = _nameProduct.asStateFlow()
    private val _priceProduct = MutableStateFlow<String>("")
    val priceProduct: StateFlow<String> = _priceProduct.asStateFlow()
    private val _quantityProduct = MutableStateFlow<String>("")
    val quantityProduct: StateFlow<String> = _quantityProduct.asStateFlow()

    //Datos de los clientes
    private val _nameClient = MutableStateFlow<String>("")
    val nameClient: StateFlow<String> = _nameClient.asStateFlow()
    private val _numDocClient = MutableStateFlow<String>("")
    val numDocClient: StateFlow<String> = _numDocClient.asStateFlow()

    //IMPRESION
    private val _printingStatus = MutableStateFlow<String?>(null)
    val printingStatus: StateFlow<String?> = _printingStatus.asStateFlow()

    // DOWNLOAD TICKET
    private val _downloadStatus = MutableStateFlow<String?>(null)
    val downloadStatus: StateFlow<String?> = _downloadStatus.asStateFlow()

    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    init {
        //downLoadGuiaRemision()
        downLoadProducts()
    }

    private fun downLoadProducts() {
        viewModelScope.launch {
            getAllProductsUseCase()
                .onStart { _productsUiState.value = ProductsUiState.Loading }
                .catch { exception -> _productsUiState.value = ProductsUiState.Error(exception) }
                .collect { products ->
                    _productsUiState.value = ProductsUiState.Success(products)
                    Log.d("GuiaRemisionViewModel", "Productos descargados: $products")
                }
        }
    }

//    private fun downLoadGuiaRemision() {
//        TODO("Not yet implemented")
//    }

    fun onCodeProductChange(newValue: String) {
        Log.d("GuiaRemisionViewModel", "onCodeProductChange: $newValue")
        _codeProduct.value = newValue
    }

    fun onNameClientChange(newValue: String) {
        _nameClient.value = newValue
    }

    fun onNumDocClientChange(newValue: String) {
        _numDocClient.value = newValue
    }

    fun onNameProductChange(newValue: String) {
        _nameProduct.value = newValue
    }

    fun onPriceProductChange(newValue: String) {
        _priceProduct.value = newValue
    }

    fun onQuantityProductChange(newValue: String) {
        _quantityProduct.value = newValue
    }

    fun checkCodeProduct(codeProduct: String): Boolean {
        Log.d("GuiaRemisionViewModel", "checkCodeProduct: $codeProduct")
        var isProduct = false
        viewModelScope.launch {
            findCodeProductUseCase(codeProduct)
                .onStart { _productsUiState.value = ProductsUiState.Loading }
                .catch { exception -> _productsUiState.value = ProductsUiState.Error(exception) }
                .collect { product ->
                    if (product != null) {
                        _productsUiState.value = ProductsUiState.Success(listOf(product))
                        Log.d("GuiaRemisionViewModel", "Producto encontrado: $product")
                        isProduct = true
                    } else {
                        Log.d("GuiaRemisionViewModel", "Producto no encontrado")
                        isProduct = false
                    }
                }
        }
        return isProduct
    }

    fun updateProduct(productModel: ProductModel) {
        viewModelScope.launch {
            try {
                _productsUiState.value = ProductsUiState.Loading
                saveProductsUseCase(productModel)
                _productsUiState.value = ProductsUiState.Success(listOf(productModel))
                //downLoadProducts()
            } catch (e: Exception) {
                Log.e("GuiaRemisionViewModel", "Error al actualizar el producto", e)
                _productsUiState.value = ProductsUiState.Error(e)
            }

        }
    }

    fun saveClient(clientModel: ClientModel) {
        viewModelScope.launch {
            try {
                saveClientUseCase(clientModel)
            } catch (e: Exception) {
                Log.e("GuiaRemisionViewModel", "Error al guardar el cliente", e)
            }
        }
    }

    fun onItemRemove(productModel: ProductModel) {
        viewModelScope.launch {
            deleteProductUseCase(productModel)
            downLoadProducts()
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    /*
    Impresion de ticket
     */
    fun initiatePrintTicket() {
        viewModelScope.launch {
            _printingStatus.value = "Ticket printing initiated"
            val ticketDataString = formatTicketData()
            //kotlin.coroutines.delay(2000) // Simulating printing delay
            _printingStatus.value = "Ticket printed successfully"
        }
    }

    fun formatTicketData(): String {
        val currentProducts =
            (productsUiState.value as? ProductsUiState.Success)?.productsModelList ?: emptyList()
        val builder = StringBuilder()
        builder.append(string1).append("\n")
        builder.append(string2).append("\n")
        builder.append(string2_1).append("\n")
        builder.append(string3).append("\n")
        builder.append(string4).append("\n")
        builder.append(string5).append(code).append("\n")
        builder.append(string6).append(GetCurrentDateTime().getCurrentDateTime()).append("\n")
        builder.append(string7).append(nameClient.value).append("\n")
        builder.append(string8).append(numDocClient.value).append("\n")
        builder.append(string9).append("\n")
        builder.append(string10).append("\n")
        builder.append(string9).append("\n")
        var granTotal = 0.0
        currentProducts.forEach { product ->
            val totalProduct = product.price * product.stock
            granTotal += totalProduct
            builder.append(
                String.format(
                    Locale.US,
                    "%-5s %-15s %6.2f %6.2f\n",
                    product.stock.toString(),
                    product.name.take(15),
                    product.price,
                    totalProduct
                )
            )
        }
        builder.append(string9).append("\n")
        builder.append(string11).append("\n")
        builder.append(
            String.format(
                Locale.US, "%.2f", granTotal
            )
        ).append("\n")
        builder.append(string12).append("\n")
        return builder.toString()
    }

    /*
    DOWNLOAD TICKET
     */
    fun clearDownloadStatus() {
        _downloadStatus.value = null
    }

    fun downloadTicketAsPdf() {
        viewModelScope.launch {
            _downloadStatus.value = "Generating PDF..."
            val ticketText = formatTicketData()
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 12f
                color = Color.BLACK
            }
            val textPaint = Paint().apply {
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textSize = 10f
                color = Color.BLACK
            }
            var yPosition = 40f
            var xPosition = 40f
            val lineSpacing = 12f

            ticketText.split("\n").forEach { line ->
                canvas.drawText(
                    line,
                    xPosition,
                    yPosition,
                    if (line.contains("Bodega") ||
                        line.contains("Total a Pagar:") ||
                        line.contains("GRACIAS")
                    ) titlePaint else textPaint
                )
                yPosition += lineSpacing
            }
            pdfDocument.finishPage(page)

            val fileName = "Ticket_${System.currentTimeMillis()}.pdf"
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = applicationContext.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri =
                        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            pdfDocument.writeTo(outputStream)
                            _downloadStatus.value = "Ticket downloaded successfully"
                        }
                    } ?: run {
                        _downloadStatus.value = "Failed to save ticket(MediaStore)"
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val downloadDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs()
                    }
                    val file = File(downloadDir, fileName)
                    FileOutputStream(file).use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        _downloadStatus.value = "Ticket downloaded successfully(Legacy)"
                    }
                }
            } catch (e: IOException) {
                _downloadStatus.value = "Failed to save ticket: ${e.message}"
                e.printStackTrace()
            } finally {
                pdfDocument.close()
            }
        }
    }


    /*
    FORMAT TICKET STRING
     */
    var string1 = "Bodega 'El Chasqui'"
    var string2 = "Av. Antigua Panamericana Nª451,"
    var string2_1 = "Mala, Cañete, Lima"
    var string3 = "Telefono: 12345678"
    var string4 = "--------------------------------"
    var string5 = "Ticket #"
    var string6 = "Date: "
    var string7 = "Cliente: "
    var string8 = "Documento: "
    var string9 = "--------------------------------"
    var string10 = "Cant. Producto     Precio Total"
    var string11 = "Total a Pagar:"
    var string12 = "¡¡GRACIAS POR SU COMPRA!!"
}