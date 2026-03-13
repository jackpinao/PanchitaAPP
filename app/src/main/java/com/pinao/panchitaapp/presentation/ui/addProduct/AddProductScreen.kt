package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddProductScreen(
    navController: NavController,
    initialBarcode: String? = null,
    viewModel: AddProductViewModel = koinViewModel()
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
) {
    val listCategories = uiState.listOfCategoriesName
    val listBrands = uiState.listOfBrandsName
    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    Screen {
        Scaffold(
            topBar = {
                TopApp(isEditMode = uiState.isEditMode)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = uiState.productCode,
                        onValueChange = onCodeChange,
                        label = { Text("Código de barras") },
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
                                    contentDescription = "QR Scanner",
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
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = uiState.productPurchasePrice,
                    onValueChange = onPriceChange,
                    label = { Text("Precio de Compra") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                            label = { Text("Categoría") },
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
                                    text = { Text("Sin categorías") },
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
                            label = { Text("Marca") },
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
                                    text = { Text("Sin marcas") },
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
                TextField(
                    value = uiState.productStock,
                    onValueChange = onStockChange,
                    label = { Text("Stock") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.padding(15.dp))
                Button(
                    onClick = { onSavenClick() },
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = if (uiState.isEditMode) "Actualizar" else "Guardar")
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopApp(isEditMode: Boolean) {
    TopAppBar(
        title = { Text(text = if (isEditMode) "Editar Producto" else "Agregar Producto") },
    )
}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    val uiState = AddProductUiState(productName = "Producto Test", isEditMode = true)
    AddProductContent(uiState, {}, {}, {}, {}, {}, {}, {}, {}, {})
}
