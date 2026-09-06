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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.common.toCurrency
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import org.koin.androidx.compose.koinViewModel

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.height

/**
 * Pantalla principal del Módulo de Ventas refactorizada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaRemisionScreen(
    viewModel: GuiaRemisionViewModel = koinViewModel(),
    navController: NavController,
    windowSize: WindowSizeClass? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val okLabel = stringResource(R.string.ok_button)

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
            snackbarHostState.showSnackbar(message = message, actionLabel = okLabel)
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
                    text = stringResource(R.string.add_product_title),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                ListItem(
                    modifier = Modifier.clickable { viewModel.startScanningProduct() },
                    headlineContent = { Text(stringResource(R.string.scan_code_action)) },
                    leadingContent = {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(R.string.scan_code_action),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
                ListItem(
                    modifier = Modifier.clickable {
                        viewModel.closeDialogs()
                        navController.navigate(AppScreens.ProductSearch.route)
                    },
                    headlineContent = { Text(stringResource(R.string.manual_search_action)) },
                    leadingContent = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.manual_search_action),
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
        },
        windowSize = windowSize
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
    onNavigateToPreview: () -> Unit,
    windowSize: WindowSizeClass? = null
) {
    val isWideScreen = windowSize?.widthSizeClass != WindowWidthSizeClass.Compact
    val listState = rememberLazyListState()

    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (!isWideScreen) {
                    FloatingActionButton(onClick = onScanClick) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(R.string.add_product_title)
                        )
                    }
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    Button(
                        onClick = onNavigateToPreview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = uiState.products.isNotEmpty() && !uiState.isLoading
                    ) {
                        Text(
                            if (uiState.isLoading) stringResource(R.string.processing_action) else stringResource(
                                R.string.finish_sale_action
                            )
                        )
                    }
                }
            }
        ) { padding ->
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Columna Izquierda: Datos del cliente y escaneo
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.minimarket_sale_title),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        OutlinedTextField(
                            value = uiState.clientName,
                            onValueChange = onClientNameChange,
                            label = { Text(stringResource(R.string.client_name_label)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uiState.clientDoc,
                            onValueChange = onClientDocChange,
                            label = { Text(stringResource(R.string.client_doc_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onScanClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(stringResource(R.string.add_product_title))
                        }
                    }

                    // Columna Derecha: Carrito, totales y finalizar
                    Column(
                        modifier = Modifier.weight(1.1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.cart_products_title),
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(uiState.products, key = { it.detailTicketEntityId }) { product ->
                                    ListItem(
                                        modifier = Modifier.combinedClickable(
                                            onClick = { /* Opcional */ },
                                            onLongClick = { onProductLongClick(product) }
                                        ),
                                        headlineContent = {
                                            Text(
                                                product.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                stringResource(
                                                    R.string.product_details_format,
                                                    product.stockQuantity.toString(),
                                                    product.priceSell.toCurrency(),
                                                    product.priceExcludingIGV.toCurrency(),
                                                    (product.priceSell * product.stockQuantity).toCurrency()
                                                )
                                            )
                                        },
                                        trailingContent = {
                                            IconButton(onClick = { onRemoveProduct(product) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = stringResource(R.string.delete_produdct),
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }

                        // Resumen de Total y Botón de Pago en Tarjeta
                        val total = uiState.products.sumOf { it.priceSell * it.stockQuantity }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        stringResource(R.string.total_to_pay_label),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                                    )
                                    Text(
                                        text = total.toCurrency(),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Button(
                                    onClick = onNavigateToPreview,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = uiState.products.isNotEmpty() && !uiState.isLoading
                                ) {
                                    Text(
                                        if (uiState.isLoading) stringResource(R.string.processing_action)
                                        else stringResource(R.string.finish_sale_action)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.minimarket_sale_title),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    OutlinedTextField(
                        value = uiState.clientName,
                        onValueChange = onClientNameChange,
                        label = { Text(stringResource(R.string.client_name_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    OutlinedTextField(
                        value = uiState.clientDoc,
                        onValueChange = onClientDocChange,
                        label = { Text(stringResource(R.string.client_doc_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.padding(12.dp))

                    Text(
                        stringResource(R.string.cart_products_title),
                        style = MaterialTheme.typography.titleSmall
                    )

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
                            items(uiState.products, key = { it.detailTicketEntityId }) { product ->
                                ListItem(
                                    modifier = Modifier.combinedClickable(
                                        onClick = { /* Opcional */ },
                                        onLongClick = { onProductLongClick(product) }
                                    ),
                                    headlineContent = {
                                        Text(
                                            product.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            stringResource(
                                                R.string.product_details_format,
                                                product.stockQuantity.toString(),
                                                product.priceSell.toCurrency(),
                                                product.priceExcludingIGV.toCurrency(),
                                                (product.priceSell * product.stockQuantity).toCurrency()
                                            )
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(onClick = { onRemoveProduct(product) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete_produdct),
                                                tint = MaterialTheme.colorScheme.error
                                            )
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
                                        stringResource(R.string.total_to_pay_label),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                                    )
                                    Text(
                                        text = total.toCurrency(),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
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
        title = { Text(stringResource(R.string.product_not_found_title)) },
        text = { Text(stringResource(R.string.product_not_found_text, code)) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.add_action)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_action)) } }
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
                    if (isEditing) stringResource(R.string.edit_quantity_title) else stringResource(
                        R.string.add_to_cart_title
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.heightIn(8.dp))
                Text(product.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text(stringResource(R.string.quantity_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_action)) }
                    Button(
                        onClick = onConfirm,
                        enabled = quantity.isNotEmpty() && (quantity.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text(
                            if (isEditing) stringResource(R.string.update_action) else stringResource(
                                R.string.add_action
                            )
                        )
                    }
                }
            }
        }
    }
}
