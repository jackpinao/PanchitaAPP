package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun AddProductScreen(
    navController: NavController,
    initialBarcode: String? = null,
    viewModel: AddProductViewModel = koinViewModel(),
    windowSize: WindowSizeClass? = null
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        initialBarcode?.let { viewModel.loadProduct(it) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShow()
        }
    }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            navController.popBackStack()
        }
    }

    AddProductContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onCodeChange = viewModel::onCodeChanged,
        onPriceChange = viewModel::onPriceChange,
        onStockChange = viewModel::onStockChange,
        onCategoryChange = viewModel::onCategoryChange,
        onBrandChange = viewModel::onBrandChange,
        onScannedClick = viewModel::startScanning,
        onCategoryClick = { navController.navigate(AppScreens.AddCategory.route) },
        onSavenClick = viewModel::saveProduct,
        onBackClick = { navController.popBackStack() },
        isExpanded = windowSize?.widthSizeClass == WindowWidthSizeClass.Expanded
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductContent(
    uiState: AddProductUiState,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onScannedClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onSavenClick: () -> Unit,
    onBackClick: () -> Unit,
    isExpanded: Boolean = false
) {
    val listCategories = uiState.listOfCategoriesName
    val listBrands = uiState.listOfBrandsName
    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    val leftSideContent = @Composable {
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = uiState.productCode,
                onValueChange = onCodeChange,
                label = { Text(stringResource(R.string.barcode_label)) },
                modifier = Modifier.weight(4f),
                singleLine = true,
                enabled = !uiState.isEditMode
            )
            if (!uiState.isEditMode) {
                Button(
                    onClick = onScannedClick,
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(R.string.qr_scanner_description),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = uiState.productName,
            onValueChange = { onNameChange(it.uppercase()) },
            label = { Text(stringResource(R.string.product_name_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Categoría
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 8.dp)
            ) {
                TextField(
                    value = uiState.productCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.category_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    if (listCategories.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_categories)) },
                            onClick = { categoryExpanded = false }
                        )
                    } else {
                        listCategories.forEach { categoryName ->
                            DropdownMenuItem(
                                text = { Text(text = categoryName) },
                                onClick = {
                                    onCategoryChange(categoryName)
                                    categoryExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onCategoryClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = "+")
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Marca (Brand)
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = brandExpanded,
                onExpandedChange = { brandExpanded = !brandExpanded },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = uiState.productBrand,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.brand_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = brandExpanded,
                    onDismissRequest = { brandExpanded = false }
                ) {
                    if (listBrands.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_brands)) },
                            onClick = { brandExpanded = false }
                        )
                    } else {
                        listBrands.forEach { brandName ->
                            DropdownMenuItem(
                                text = { Text(text = brandName) },
                                onClick = {
                                    onBrandChange(brandName)
                                    brandExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }

    val rightSideContent = @Composable {
        // COSTO TOTAL DEPENDIENDO DEL MODO (NUEVO vs EDICIÓN/AÑADIR STOCK)
        TextField(
            value = uiState.productTotalCost,
            onValueChange = onPriceChange,
            label = { 
                Text(if (uiState.isEditMode) "Costo Total del Stock Añadido" else "Costo Total de Compra") 
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // STOCK DEPENDIENDO DEL MODO
        TextField(
            value = uiState.productStock,
            onValueChange = onStockChange,
            label = { 
                Text(if (uiState.isEditMode) "Cantidad de Stock a Añadir" else stringResource(R.string.stock_label)) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // TEXTO INFORMATIVO PARA MOSTRAR EL CÁLCULO PPP
        if (uiState.isEditMode) {
            Text(
                text = String.format(Locale.getDefault(), "Stock Actual: %.2f | Costo Unitario Actual: S/%.2f", uiState.existingStock, uiState.existingPriceBuy),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 8.dp)
            )
            Text(
                text = String.format(Locale.getDefault(), "Stock Final: %.2f", uiState.finalCalculatedStock),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 4.dp)
            )
            Text(
                text = String.format(Locale.getDefault(), "Nuevo Costo Promedio (PPP): S/%.2f", uiState.calculatedUnitPrice),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 2.dp)
            )
            if (uiState.calculatedSellingPrice > 0) {
                Text(
                    text = String.format(Locale.getDefault(), "Precio de Venta Calculado: S/%.2f", uiState.calculatedSellingPrice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 2.dp)
                )
            }
        } else {
            if (uiState.calculatedUnitPrice > 0) {
                Text(
                    text = String.format(Locale.getDefault(), "Costo Unitario Calculado: S/%.2f", uiState.calculatedUnitPrice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 8.dp)
                )
            }
            if (uiState.calculatedSellingPrice > 0) {
                Text(
                    text = String.format(Locale.getDefault(), "Precio de Venta Calculado: S/%.2f", uiState.calculatedSellingPrice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 2.dp)
                )
            }
        }
        
        if (uiState.productCategory.isNotEmpty()) {
            Text(
                text = String.format(
                    Locale.getDefault(), 
                    "Precio de Venta Sugerido: S/%.2f (Margen: %.0f%%)", 
                    uiState.calculatedSellingPrice, 
                    uiState.productRevenueCategory
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.padding(15.dp))
        Button(
            onClick = { onSavenClick() },
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (uiState.isEditMode) {
                    stringResource(R.string.update_action)
                } else {
                    stringResource(R.string.save)
                }
            )
        }
        Spacer(modifier = Modifier.padding(15.dp))
    }

    Screen {
        Scaffold(
            topBar = {
                TopApp(isEditMode = uiState.isEditMode, onBackClick = onBackClick)
            }
        ) { innerPadding ->
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        leftSideContent()
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        rightSideContent()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    leftSideContent()
                    rightSideContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopApp(isEditMode: Boolean, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditMode) {
                    stringResource(R.string.edit_product_title)
                } else {
                    stringResource(R.string.add_produdct)
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cancel_action)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    val uiState = AddProductUiState(productName = "Producto Test", isEditMode = true)
    AddProductContent(uiState, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
}
