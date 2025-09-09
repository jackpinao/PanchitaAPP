package com.pinao.panchitaapp.presentation.ui.guiaremision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
// Remove LazyColumn and items imports if no longer used elsewhere, but for now, keep them
// import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun PreviewTicketScreen(
    viewModel: GuiaRemisionViewModel,
) {
    // This function is a placeholder for the preview of the Ticket Screen.
    // It can be used to display a static preview of the UI without needing to run the app.
    // You can implement your preview logic here.
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val productsUiState by produceState<ProductsUiState>(
        initialValue = ProductsUiState.Loading,
        key1 = viewModel,
        key2 = lifecycle
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.productsUiState.collect {
                value = it
            }
        }
    }
    when (productsUiState) {
        is ProductsUiState.Loading -> {
            CircularProgressIndicator()
        }

        is ProductsUiState.Error -> {
            // Handle error state
        }

        is ProductsUiState.Success -> {
            PreviewTicketContent(
                products = (productsUiState as ProductsUiState.Success).productsModelList,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun PreviewTicketContent(
    products: List<ProductModel>,
    viewModel: GuiaRemisionViewModel
) {
    Screen {
        Scaffold(
            bottomBar = {
                BottomBar()
            }
        ) { innerPadding ->
            // Here you can add the content of the Ticket Screen.
            // For example, you can display a preview of a ticket layout.
            // This is just a placeholder for the actual content.
            // Replace with your actual UI components.
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                CenterAppPreviewTicket(
                    products = products,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun BottomBar() {
    Column {
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Print Ticket",
                modifier = Modifier.padding(8.dp)
            )
        }
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Download Ticket",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun CenterAppPreviewTicket(
    products: List<ProductModel>,
    viewModel: GuiaRemisionViewModel
) {
    // This function can be used to display the main content of the Ticket Screen.
    // You can implement your UI components here.
    // For example, you can show a preview of a ticket layout or any other relevant information.
    // This is just a placeholder for the actual content.
    val nameClient by viewModel.nameClient.collectAsState()
    val numDocClient by viewModel.numDocClient.collectAsState()

    Text(
        text = "Preview of Ticket Screen",
        modifier = Modifier.padding(16.dp)
    )
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Bodega 'El Chasqui'",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Add more details about the ticket here
            Text(text = "Av. Antigua Panamericana Nª451, Mala, Cañete, Lima")
            Text(text = "Telefono: 12345678")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "Ticket #123456")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "Date: 2023-10-01")
            Text(text = "Time: 10:00 AM")
            Text(text = "Cliente: $nameClient")
            Text(text = "Documento: $numDocClient")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            //Encabezados de la tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cant.",
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = "Producto",
                    modifier = Modifier.weight(2f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = "Precio",
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = "Total",
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
            }
            HorizontalDivider(thickness = 1.dp)
            ListProducts(
                products = products
            )
            Row(modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp),) {
                Text(
                    text = "Total a Pagar:",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "70.00",
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "¡¡GRACIAS POR SU COMPRA!!",
                modifier = Modifier.padding(top = 15.dp)
            )
        }

    }
}

@Composable
private fun ListProducts(
    products: List<ProductModel>
) {
    Column { // Changed from LazyColumn
        products.forEach { product -> // Changed from items block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "${product.stock}",
                    modifier = Modifier.weight(1f))
                VerticalDivider(thickness = 1.dp)
                Text(text = product.name,
                    modifier = Modifier.weight(2f))
                VerticalDivider(thickness = 1.dp)
                Text(text = "${product.price}",
                    modifier = Modifier.weight(1f))
                VerticalDivider(thickness = 1.dp)
                Text(text = "${product.price * product.stock}",
                    modifier = Modifier.weight(1f))
            }
            HorizontalDivider(thickness = 1.dp)
        }
    }
}
