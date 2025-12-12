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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import androidx.compose.ui.platform.LocalContext

@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AddProductViewModel
) {
    AddProductContent(
        navController,
        viewModel
    )
}

@Composable
fun AddProductContent(
    navController: NavController? = null,
    viewModel: AddProductViewModel = viewModel()
) {

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
                CenterAppAddProduct(
                    navController,
                    viewModel
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

@Composable
private fun CenterAppAddProduct(
    navController: NavController?,
    viewModel: AddProductViewModel
) {
    val textState by viewModel.scannedText.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = "Add Product",
//            modifier = Modifier.padding(16.dp),
//            fontSize = 24.sp,
//            color = Color.Black
//        )
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textState,
                onValueChange = { viewModel.onTextChanged(it) },
                label = { Text("Agregar Codigo") },
                modifier = Modifier.weight(4f),
                singleLine = true
            )
            Button(
                onClick = { viewModel.startScanning(context) },
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f)
            ) {
                Icon(
                    //painter = painterResource(id = R.drawable.outline_calendar_view_week_24),
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "QR Scanner",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            label = { Text("Name Product") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            label = { Text("Precio de Compra") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        //Agregar un listado
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(8.dp)
                    .weight(2f)
            ) {
                Text(text = "Categoria")
            }
            Button(
                onClick = {
                    navController?.navigate(route = AppScreens.AddCategory.route)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(text = "+")
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            label = { Text("Stock") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    //AddProductContent(viewModel = viewModel)
}