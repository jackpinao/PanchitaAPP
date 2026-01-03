package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
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
    viewModel: AddProductViewModel = koinViewModel()
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShow()
        }
    }

    AddProductContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onCodeChange = viewModel::onCodeChanged,
        onPriceChange = viewModel::onPriceChange,
        onStockChange = viewModel::onStockChange,
        onCategoryChange = viewModel::onCategoryChange,
        onScannedClick = viewModel::startScanning,
        onCategoryClick = { navController.navigate(AppScreens.AddCategory.route) },
        //navController,
        //viewModel = viewModel
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
    onScannedClick: () -> Unit,
    onCategoryClick: () -> Unit,
    //navController: NavController? = null,
    //viewModel: AddProductViewModel = viewModel()
) {

    val context = LocalContext.current
    val listCategories = uiState.listOfCategories

    // Estado para controlar si el menú está desplegado o no
    var expanded by remember { mutableStateOf(false) }

    Screen {
        Scaffold(
            topBar = {
                TopApp()
            },
            bottomBar = {
//                BottomApp(
//                )
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
                        label = { Text("Agregar Codigo") },
                        modifier = Modifier.weight(4f),
                        singleLine = true
                    )
                    Button(
                        //onClick = { viewModel.startScanning(context) },
                        onClick = onScannedClick,
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                //painter = painterResource(id = R.drawable.outline_calendar_view_week_24),
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "QR Scanner",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            //Text(text = "Escanear")
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = uiState.productName,
                    onValueChange = onNameChange,
                    label = { Text("Name Product") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = uiState.productPrice,
                    onValueChange = onPriceChange,
                    label = { Text("Precio de Compra") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))

                // --- SECCIÓN CATEGORÍA (DROPDOWN + BOTÓN AGREGAR) ---
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Implementación del Spinner (ExposedDropdownMenuBox)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .weight(2f)
                            .padding(end = 8.dp) // Espacio entre el dropdown y el botón "+"
                    ) {
                        // El campo de texto que muestra la selección
                        TextField(
                            value = uiState.productCategory, // Viene del UI State
                            onValueChange = {}, // ReadOnly, no se escribe manualmente
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                //.menuAnchor(MenuAnchorType.PrimaryNotEditable, true) // Conecta el menú al TextField
                                .menuAnchor(
                                    ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    true
                                ) // Conecta el menú al TextField
                                .fillMaxWidth()
                        )

                        // La lista desplegable
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (listCategories.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Sin categorías") },
                                    onClick = { expanded = false }
                                )
                            } else {
                                listCategories.forEach { categoryName ->
                                    DropdownMenuItem(
                                        text = { Text(text = categoryName) },
                                        onClick = {
                                            onCategoryChange(categoryName) // Actualiza el ViewModel
                                            expanded = false // Cierra el menú
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }

                    // Botón para agregar nueva categoría
                    Button(
                        onClick = {
                            onCategoryClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp) // Ajuste visual
                    ) {
                        Text(text = "+")
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = uiState.productStock,
                    onValueChange = onStockChange,
                    label = { Text("Stock") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopApp() {
    TopAppBar(
        title = { Text(text = "Agregar Producto") },
    )
}

@Composable
private fun BottomApp() {

}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    val uiState = AddProductUiState("", "", "", "", "")
    AddProductContent(uiState, {}, {}, {}, {}, {}, {}, {})
}
