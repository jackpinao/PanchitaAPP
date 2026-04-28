package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
        onManualPriceChange = viewModel::onManualPriceChange,
        onTabSelected = viewModel::onTabSelected,
        onScannedClick = viewModel::startScanning,
        onCategoryClick = { navController.navigate(AppScreens.AddCategory.route) },
        onSaveNewClick = viewModel::saveProduct,
        onSaveInfoClick = viewModel::saveProductInfo,
        onRegisterStockClick = viewModel::registerStockEntry,
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
    onManualPriceChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onScannedClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onSaveNewClick: () -> Unit,
    onSaveInfoClick: () -> Unit,
    onRegisterStockClick: () -> Unit,
    onBackClick: () -> Unit,
    isExpanded: Boolean = false
) {
    Screen {
        Scaffold(
            topBar = {
                TopApp(isEditMode = uiState.isEditMode, onBackClick = onBackClick)
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (uiState.isEditMode) {
                    PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                        Tab(
                            selected = uiState.selectedTab == 0,
                            onClick = { onTabSelected(0) },
                               text = { Text(stringResource(R.string.tab_info)) }
                        )
                        Tab(
                            selected = uiState.selectedTab == 1,
                            onClick = { onTabSelected(1) },
                               text = { Text(stringResource(R.string.stock_label)) }
                        )
                    }

                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        label = "edit_tab_content"
                    ) { tab ->
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.padding(8.dp))
                            if (tab == 0) {
                                InfoTabContent(
                                    uiState = uiState,
                                    onNameChange = onNameChange,
                                    onCategoryChange = onCategoryChange,
                                    onBrandChange = onBrandChange,
                                    onManualPriceChange = onManualPriceChange,
                                    onCategoryClick = onCategoryClick,
                                    onSaveInfoClick = onSaveInfoClick
                                )
                            } else {
                                StockTabContent(
                                    uiState = uiState,
                                    onPriceChange = onPriceChange,
                                    onStockChange = onStockChange,
                                    onRegisterStockClick = onRegisterStockClick
                                )
                            }
                        }
                    }
                } else {
                    // Modo creaciÃ³n: formulario Ãºnico
                    if (isExpanded) {
                        Row(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(modifier = Modifier.padding(8.dp))
                                NewProductLeftContent(
                                    uiState = uiState,
                                    onNameChange = onNameChange,
                                    onCodeChange = onCodeChange,
                                    onCategoryChange = onCategoryChange,
                                    onBrandChange = onBrandChange,
                                    onScannedClick = onScannedClick,
                                    onCategoryClick = onCategoryClick
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(modifier = Modifier.padding(8.dp))
                                NewProductRightContent(
                                    uiState = uiState,
                                    onPriceChange = onPriceChange,
                                    onStockChange = onStockChange,
                                    onSaveClick = onSaveNewClick
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Spacer(modifier = Modifier.padding(8.dp))
                            NewProductLeftContent(
                                uiState = uiState,
                                onNameChange = onNameChange,
                                onCodeChange = onCodeChange,
                                onCategoryChange = onCategoryChange,
                                onBrandChange = onBrandChange,
                                onScannedClick = onScannedClick,
                                onCategoryClick = onCategoryClick
                            )
                            NewProductRightContent(
                                uiState = uiState,
                                onPriceChange = onPriceChange,
                                onStockChange = onStockChange,
                                onSaveClick = onSaveNewClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoTabContent(
    uiState: AddProductUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onManualPriceChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    onSaveInfoClick: () -> Unit
) {
    val listCategories = uiState.listOfCategoriesName
    val listBrands = uiState.listOfBrandsName
    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    TextField(
        value = uiState.productName,
        onValueChange = { onNameChange(it.uppercase()) },
        label = { Text(stringResource(R.string.product_name_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    )
    Spacer(modifier = Modifier.padding(8.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
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
                            text = { Text(categoryName) },
                            onClick = { onCategoryChange(categoryName); categoryExpanded = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        Button(
            onClick = onCategoryClick,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) { Text("+") }
    }
    Spacer(modifier = Modifier.padding(8.dp))

    ExposedDropdownMenuBox(
        expanded = brandExpanded,
        onExpandedChange = { brandExpanded = !brandExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        TextField(
            value = uiState.productBrand,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.brand_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
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
                        text = { Text(brandName) },
                        onClick = { onBrandChange(brandName); brandExpanded = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))

    if (uiState.productRevenueCategory > 0) {
        Text(
              text = stringResource(R.string.category_margin_format, uiState.productRevenueCategory),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
    }

    TextField(
        value = uiState.productManualPrice,
        onValueChange = onManualPriceChange,
            label = { Text(stringResource(R.string.manual_price_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        supportingText = {
            Text(
                    stringResource(R.string.manual_price_helper),
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
    Spacer(modifier = Modifier.padding(15.dp))

    Button(
        onClick = onSaveInfoClick,
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(stringResource(R.string.save_info_action))
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
}

@Composable
private fun StockTabContent(
    uiState: AddProductUiState,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onRegisterStockClick: () -> Unit
) {
    TextField(
        value = uiState.productTotalCost,
        onValueChange = onPriceChange,
            label = { Text(stringResource(R.string.total_stock_cost_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    Spacer(modifier = Modifier.padding(8.dp))

    TextField(
        value = uiState.productStock,
        onValueChange = onStockChange,
           label = { Text(stringResource(R.string.stock_to_add_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )

    Text(
        text = stringResource(R.string.current_stock_info_format, uiState.existingStock, uiState.existingPriceBuy),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 8.dp)
    )
    Text(
        text = stringResource(R.string.final_stock_format, uiState.finalCalculatedStock),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
    )
    Text(
        text = stringResource(R.string.new_avg_cost_format, uiState.calculatedUnitPrice),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 2.dp)
    )
    if (uiState.calculatedSellingPrice > 0) {
        Text(
            text = stringResource(R.string.calculated_sell_price_format, uiState.calculatedSellingPrice),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 2.dp)
        )
    }

    Spacer(modifier = Modifier.padding(15.dp))

    Button(
        onClick = onRegisterStockClick,
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(stringResource(R.string.register_stock_entry_action))
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewProductLeftContent(
    uiState: AddProductUiState,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onScannedClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    val listCategories = uiState.listOfCategoriesName
    val listBrands = uiState.listOfBrandsName
    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
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
        Button(
            onClick = onScannedClick,
            modifier = Modifier.padding(2.dp).weight(1f),
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
    Spacer(modifier = Modifier.padding(8.dp))

    TextField(
        value = uiState.productName,
        onValueChange = { onNameChange(it.uppercase()) },
        label = { Text(stringResource(R.string.product_name_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    )
    Spacer(modifier = Modifier.padding(8.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded },
            modifier = Modifier.weight(2f).padding(end = 8.dp)
        ) {
            TextField(
                value = uiState.productCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
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
                            text = { Text(categoryName) },
                            onClick = { onCategoryChange(categoryName); categoryExpanded = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        Button(
            onClick = onCategoryClick,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) { Text("+") }
    }
    Spacer(modifier = Modifier.padding(8.dp))

    ExposedDropdownMenuBox(
        expanded = brandExpanded,
        onExpandedChange = { brandExpanded = !brandExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        TextField(
            value = uiState.productBrand,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.brand_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
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
                        text = { Text(brandName) },
                        onClick = { onBrandChange(brandName); brandExpanded = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
}

@Composable
private fun NewProductRightContent(
    uiState: AddProductUiState,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    TextField(
        value = uiState.productTotalCost,
        onValueChange = onPriceChange,
            label = { Text(stringResource(R.string.total_purchase_cost_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    Spacer(modifier = Modifier.padding(8.dp))

    TextField(
        value = uiState.productStock,
        onValueChange = onStockChange,
        label = { Text(stringResource(R.string.stock_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )

    if (uiState.calculatedUnitPrice > 0) {
        Text(
                text = stringResource(R.string.calculated_unit_cost_format, uiState.calculatedUnitPrice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 8.dp)
        )
    }
    if (uiState.calculatedSellingPrice > 0) {
        Text(
                text = stringResource(R.string.calculated_sell_price_format, uiState.calculatedSellingPrice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, bottom = 4.dp)
        )
    }
    if (uiState.productCategory.isNotEmpty()) {
        Text(
                text = stringResource(R.string.suggested_price_format, uiState.calculatedSellingPrice, uiState.productRevenueCategory),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, bottom = 4.dp)
        )
    }

    Spacer(modifier = Modifier.padding(15.dp))
    Button(
        onClick = onSaveClick,
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(stringResource(R.string.save))
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
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
    AddProductContent(
        uiState = uiState,
        onNameChange = {}, onCodeChange = {}, onPriceChange = {}, onStockChange = {},
        onCategoryChange = {}, onBrandChange = {}, onManualPriceChange = {},
        onTabSelected = {}, onScannedClick = {}, onCategoryClick = {},
        onSaveNewClick = {}, onSaveInfoClick = {}, onRegisterStockClick = {}, onBackClick = {}
    )
}


