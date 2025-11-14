package com.pinao.panchitaapp.presentation.ui.guiaremision

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen


@Composable
fun GuiaRemisionScreen(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    navController: NavController
) {

    val showDialog by guiaRemisionViewModel.showDialog.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val productsUiState by produceState<ProductsUiState>(
        initialValue = ProductsUiState.Loading,
        key1 = guiaRemisionViewModel,
        key2 = lifecycle
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            guiaRemisionViewModel.productsUiState.collect {
                value = it
            }
        }
    }

    when (productsUiState) {
        is ProductsUiState.Loading -> {
            // Show loading state
            CircularProgressIndicator()
        }

        is ProductsUiState.Error -> {
            // Handle error state
            Log.w(
                "GuiaRemisionScreen",
                "Error loading products: ${(productsUiState as ProductsUiState.Error).throwable}"
            )
        }

        is ProductsUiState.Success -> {
            // Handle success state
            GuiaRemisionContent(
                guiaRemisionViewModel = guiaRemisionViewModel,
                showDialog,
                (productsUiState as ProductsUiState.Success).productsModelList,
                navController,
            )
        }
    }

}

@Composable
fun GuiaRemisionContent(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    showDialog: Boolean,
    productsModelList: List<ProductModel>,
    navController: NavController,
) {
    Screen {
        Scaffold(
            topBar = {
                TopBar(
                    guiaRemisionViewModel,
                    showDialog
                )
            },
            bottomBar = {
                BottomApp(
                    guiaRemisionViewModel = guiaRemisionViewModel,
                    productsModelList = productsModelList,
                    navController = navController
                )
            },
            floatingActionButton = {
                FabDialog(
                    guiaRemisionViewModel = guiaRemisionViewModel
                )
            }

        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                CenterAppGuiaRemision(
                    guiaRemisionViewModel,
                    showDialog,
                    productsModelList
                )
            }

        }
    }
}

@Composable
private fun BottomApp(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    productsModelList: List<ProductModel>,
    navController: NavController
) {
    PreviewTicketButton(
        guiaRemisionViewModel = guiaRemisionViewModel,
        productsModelList = productsModelList,
        navController = navController
    )
}

@Composable
private fun FabDialog(guiaRemisionViewModel: GuiaRemisionViewModel) {
    FloatingActionButton(
        onClick = {
            guiaRemisionViewModel.onShowDialogClick() // Show the dialog when the FAB is clicked
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Product")
    }
}

@Composable
private fun TopBar(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    showDialog: Boolean
) {
    Column {

    }
}

@Composable
private fun CenterAppGuiaRemision(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    showDialog: Boolean,
    productsModelList: List<ProductModel>,

    ) {
    val nameClient by guiaRemisionViewModel.nameClient.collectAsState()
    val numDocClient by guiaRemisionViewModel.numDocClient.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Guia de Remision",
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            color = Color.Black
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NameClientTextField(
                nameClient = nameClient,
                modifier = Modifier
                    .weight(4f)
                    .padding(8.dp),
                onValueChange = { guiaRemisionViewModel.onNameClientChange(it) }
            )
            EraserTextClient(
                guiaRemisionViewModel = guiaRemisionViewModel,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumDocClientTextField(
                numDocClient = numDocClient,
                modifier = Modifier
                    .weight(4f)
                    .padding(8.dp),
                onValueChange = { guiaRemisionViewModel.onNumDocClientChange(it) }
            )
            EraserTextDocClient(
                guiaRemisionViewModel = guiaRemisionViewModel,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            )
        }
        ProductList(
            products = productsModelList,
            guiaRemisionViewModel = guiaRemisionViewModel
        )
    }
    AddProductDialog(
        guiaRemisionViewModel,
        showDialog,
        onDismiss = { guiaRemisionViewModel.onDialogClose() }, // Close the dialog when dismissed
        productsModelList,

        )
}

@Composable
fun PreviewTicketButton(
    navController: NavController,
    guiaRemisionViewModel: GuiaRemisionViewModel,
    productsModelList: List<ProductModel>
) {
    val nameClient by guiaRemisionViewModel.nameClient.collectAsState()
    val numDocClient by guiaRemisionViewModel.numDocClient.collectAsState()
    ElevatedButton(
        onClick = {
            // Handle the preview ticket logic here
            // For example, you can call a function in the ViewModel to generate the ticket
            //guiaRemisionViewModel.previewTicket(productsModelList)
            guiaRemisionViewModel.saveClient(
                clientModel = ClientModel(
                    name = nameClient,
                    numDoc = numDocClient
                )
            )
            navController.navigate(route = AppScreens.PreviewTicket.route) // Navigate to the preview ticket screen
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Preview Ticket",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ProductList(products: List<ProductModel>, guiaRemisionViewModel: GuiaRemisionViewModel) {
    Column {
        Text(
            text = "Products",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            color = Color.Black
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            LazyColumn(
                //modifier = Modifier.fillMaxHeight()
            ) {
                items(items = products, key = { it.id }) { product ->
                    // Display each product in the list
                    Text(
                        text = "Codigo Producto: " +
                                "\n ${product.code} " +
                                "\n Nombre Producto: ${product.name} " +
                                "\n Precio: ${product.price} " +
                                "\n Cantidad: ${product.stock} " +
                                "\n Total: " +
                                "%.2f".format(product.price * product.stock),
                        modifier = Modifier.padding(8.dp)
                    )
                    ElevatedButton(
                        onClick = {
                            guiaRemisionViewModel.onItemRemove(product)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Remove")
                    }
                }
            }

        }
    }
}

@Composable
fun NameClientTextField(
    nameClient: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        value = nameClient,
        onValueChange = onValueChange,
        label = {
            Text(
                text = "Nombre del Cliente",
                color = Color.Gray
            )
        },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun NumDocClientTextField(
    numDocClient: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
) {
    TextField(
        value = numDocClient,
        onValueChange = onValueChange,
        label = {
            Text(
                text = "Numero de Documento",
                color = Color.Gray
            )
        },
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun AddProductDialog(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    showDialog: Boolean?,
    onDismiss: () -> Unit,
    productsModelList: List<ProductModel>,
) {

    val isCodeProduct by guiaRemisionViewModel.codeProduct.collectAsState()
    Log.d("GuiaRemisionScreen", "codeProduct: $isCodeProduct")
    val isValNameProduct by guiaRemisionViewModel.nameProduct.collectAsState()
    val isValPriceProduct by guiaRemisionViewModel.priceProduct.collectAsState()
    val isValQuantityProduct by guiaRemisionViewModel.quantityProduct.collectAsState()

    if (showDialog == true) {
        // Implement the dialog content here
        // For example, you can use a TextField to input product details
        // and a Button to save the product.
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.White) // Set background color for the dialog
            ) {
                // Add your dialog content here
                // For example, TextField for product name, price, etc.
                Spacer(modifier = Modifier.padding(8.dp))
                CodeProductTextField(
                    codeProduct = isCodeProduct,
                ) {
                    guiaRemisionViewModel.onCodeProductChange(it)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                NameProductTextField(
                    nameProduct = isValNameProduct,
                )
                {
                    guiaRemisionViewModel.onNameProductChange(it)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                PriceProductTextField(
                    priceProduct = isValPriceProduct.toString(),
                ) {
                    guiaRemisionViewModel.onPriceProductChange(it)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                QualityProductTextField(
                    quantityProduct = isValQuantityProduct.toString(),
                ) {
                    guiaRemisionViewModel.onQuantityProductChange(it)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                AddProductButton(
                    isCodeProduct,
                    isValNameProduct,
                    isValPriceProduct,
                    isValQuantityProduct,
                    guiaRemisionViewModel = guiaRemisionViewModel,
                    onDismiss = onDismiss
                )
            }

        }
    }
}

@Composable
fun AddProductButton(
    isCodeProduct: String,
    isValNameProduct: String,
    isValPriceProduct: String,
    isValQuantityProduct: String,
    guiaRemisionViewModel: GuiaRemisionViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ElevatedButton(
        onClick = {
            //Comprobar que codeProduct no se repite en la lista de productos
//            val isValCodeProduct = guiaRemisionViewModel.checkCodeProduct(isCodeProduct)
//            if (isValCodeProduct) {
//                Log.d("GuiaRemisionScreen", "El codigo del producto ya existe")
//                Toast.makeText(context, "El codigo del producto ya existe", Toast.LENGTH_SHORT).show()
//                return@ElevatedButton
//            }else{
//                Log.d("GuiaRemisionScreen", "El codigo del producto no existe")
//                guiaRemisionViewModel.updateProduct(
//                    ProductModel(
//                        name = isValNameProduct,
//                        price = isValPriceProduct.toDouble(),
//                        stock = isValQuantityProduct.toDouble(),
//                        //code = isCodeProduct
//                    )
//                )
//                guiaRemisionViewModel.onCodeProductChange("")
//                guiaRemisionViewModel.onNameProductChange("")
//                guiaRemisionViewModel.onPriceProductChange("")
//                guiaRemisionViewModel.onQuantityProductChange("")
//            }
            guiaRemisionViewModel.updateProduct(
                ProductModel(
                    name = isValNameProduct,
                    price = if (isValPriceProduct.isEmpty()) 0.0 else isValPriceProduct.toDouble(),
                    stock = if (isValQuantityProduct.isEmpty()) 0.0 else isValQuantityProduct.toDouble() ,
                    code = isCodeProduct
                )
            )
            val code = System.currentTimeMillis().toString()
            guiaRemisionViewModel.onCodeProductChange(code)
            guiaRemisionViewModel.onNameProductChange("")
            guiaRemisionViewModel.onPriceProductChange("")
            guiaRemisionViewModel.onQuantityProductChange("")
            onDismiss() // Close the dialog after adding the product
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Add Product",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
fun QualityProductTextField(quantityProduct: String, onValueChange: (String) -> Unit) {
    TextField(
        value = quantityProduct,
        onValueChange = { onValueChange(it) },
        label = {
            Text(
                text = "Quantity",
                color = Color.Gray
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun PriceProductTextField(priceProduct: String, onValueChange: (String) -> Unit) {
    TextField(
        value = priceProduct,
        onValueChange = { onValueChange(it) },
        label = {
            Text(
                text = "Price",
                color = Color.Gray
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun NameProductTextField(nameProduct: String, onValueChange: (String) -> Unit) {
    TextField(
        value = nameProduct,
        onValueChange = { onValueChange(it) },
        label = {
            Text(
                text = "Product Name",
                color = Color.Gray
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun CodeProductTextField(codeProduct: String, onValueChange: (String) -> Unit) {
    TextField(
        value = codeProduct,
        onValueChange = { onValueChange(it) },
        label = {
            Text(
                text = "Product Code",
                color = Color.Gray
            )
        },
        singleLine = true,
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
private fun EraserTextClient(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    modifier: Modifier
) {
    IconButton(
        onClick = {
            guiaRemisionViewModel.onNameClientChange("")
        },
        //modifier = Modifier.padding(5.dp),
        modifier = modifier,
        enabled = true
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete"
        )
    }
}

@Composable
private fun EraserTextDocClient(
    guiaRemisionViewModel: GuiaRemisionViewModel,
    modifier: Modifier
) {
    IconButton(
        onClick = {
            guiaRemisionViewModel.onNumDocClientChange("")
        },
        //modifier = Modifier.padding(5.dp),
        modifier = modifier,
        enabled = true
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete"
        )
    }
}