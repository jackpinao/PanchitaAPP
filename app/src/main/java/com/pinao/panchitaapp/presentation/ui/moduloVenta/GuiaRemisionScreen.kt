package com.pinao.panchitaapp.presentation.ui.moduloVenta

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.common.toCurrency
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla principal del Módulo de Ventas refactorizada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaRemisionScreen(
    viewModel: GuiaRemisionViewModel = koinViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    // Manejo de retorno de búsqueda manual
    val selectedProductCode = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("selected_product_code")

    LaunchedEffect(selectedProductCode) {
        selectedProductCode?.let { code ->
            viewModel.handleProductByCode(code)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_product_code")
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message, actionLabel = "OK")
            viewModel.clearErrorMessage()
        }
    }

    if (uiState.showSelectionSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeDialogs,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp)
            ) {
                Text(
                    text = "Añadir Producto",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                ListItem(
                    modifier = Modifier.clickable { viewModel.startScanningProduct() },
                    headlineContent = { Text("Escanear Código") },
                    leadingContent = {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
                ListItem(
                    modifier = Modifier.clickable {
                        viewModel.closeDialogs()
                        navController.navigate(AppScreens.ProductSearch.route)
                    },
                    headlineContent = { Text("Búsqueda Manual") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }
        }
    }

    if (uiState.showNotFoundError) {
        NotFoundErrorDialog(
            code = uiState.lastScannedCode,
            onConfirm = {
                viewModel.closeDialogs()
                navController.navigate("${AppScreens.AddProduct.route}?barcode=${uiState.lastScannedCode}")
            },
            onDismiss = viewModel::closeDialogs
        )
    }

    if (uiState.showAddDialog) {
        AddProductQuantityDialog(
            product = uiState.scannedProduct,
            quantity = uiState.quantity,
            isEditing = uiState.isEditing,
            onQuantityChange = viewModel::onQuantityChange,
            onConfirm = viewModel::onConfirmQuantity,
            onDismiss = viewModel::closeDialogs
        )
    }

    GuiaRemisionContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onClientNameChange = viewModel::onClientNameChange,
        onClientDocChange = viewModel::onClientDocChange,
        onScanClick = viewModel::onScanClick,
        onRemoveProduct = viewModel::removeItem,
        onProductLongClick = viewModel::onProductLongClick,
        onNavigateToPreview = {
            navController.navigate(AppScreens.PreviewTicket.route)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GuiaRemisionContent(
    uiState: GuiaRemisionUiState,
    snackbarHostState: SnackbarHostState,
    onClientNameChange: (String) -> Unit,
    onClientDocChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onRemoveProduct: (ProductModel) -> Unit,
    onProductLongClick: (ProductModel) -> Unit,
    onNavigateToPreview: () -> Unit
) {

    val listState = rememberLazyListState()

    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(onClick = onScanClick) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Añadir Producto")
                }
            },
            bottomBar = {
                Button(
                    onClick = onNavigateToPreview,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = uiState.products.isNotEmpty() && !uiState.isLoading
                ) {
                    Text(if (uiState.isLoading) "Procesando..." else "Finalizar Venta")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Venta Minimarket", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = uiState.clientName,
                    onValueChange = onClientNameChange,
                    label = { Text("Nombre del Cliente") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.clientDoc,
                    onValueChange = onClientDocChange,
                    label = { Text("Documento (DNI/RUC)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.padding(12.dp))

                Text("Productos en el Carrito", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .weight(1f)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(uiState.products) { product ->
                            ListItem(
                                modifier = Modifier.combinedClickable(
                                    onClick = { /* Opcional */ },
                                    onLongClick = { onProductLongClick(product) }
                                ),
                                headlineContent = {
                                    Text(
                                        product.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "Cant: ${product.stockQuantity} | " +
                                                "P. Unit: ${product.priceSell.toCurrency()}\n" +
                                                "P. Sin IGV: ${product.priceExcludingIGV.toCurrency()}\n" +
                                                "Subtotal: ${(product.priceSell * product.stockQuantity).toCurrency()}"
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { onRemoveProduct(product) }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                                    }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        }

                        // Resumen de Total al final de la lista
                        item {
                            val total = uiState.products.sumOf { it.priceSell * it.stockQuantity }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "TOTAL A PAGAR",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = total.toCurrency(),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF388E3C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotFoundErrorDialog(code: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Producto no encontrado") },
        text = { Text("El código [$code] no está registrado. ¿Deseas agregarlo al inventario?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Agregar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddProductQuantityDialog(
    product: ProductModel?,
    quantity: String,
    isEditing: Boolean,
    onQuantityChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (product == null) return
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (isEditing) "Editar Cantidad" else "Añadir al Carrito",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.heightIn(8.dp))
                Text(product.name, color = Color.Gray)
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Cantidad") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = onConfirm,
                        enabled = quantity.isNotEmpty() && (quantity.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text(if (isEditing) "Actualizar" else "Añadir")
                    }
                }
            }
        }
    }
}
