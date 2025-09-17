package com.pinao.panchitaapp.presentation.ui.guiaremision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.ui.Screen
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PreviewTicketScreen(
    viewModel: GuiaRemisionViewModel,
) {
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val printingStatus by viewModel.printingStatus.collectAsState()
    printingStatus?.let { status ->
        LaunchedEffect(key1 = "print_status_$status") { // Unique key for re-launch if status changes
            scope.launch {
                snackbarHostState.showSnackbar(status)
                // viewModel.clearPrintingStatus() // Optional: if you add this to ViewModel
            }
        }
    }

    val downloadStatus by viewModel.downloadStatus.collectAsState()
    downloadStatus?.let { status ->
        LaunchedEffect(key1 = "download_status_$status") { // Unique key
            scope.launch {
                snackbarHostState.showSnackbar(status)
                viewModel.clearDownloadStatus() // Clear the message after showing
            }
        }
    }

    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomBar(viewModel = viewModel)
            }
        ) { innerPadding ->
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
private fun BottomBar(viewModel: GuiaRemisionViewModel) {
    Column {
        Button(
            onClick = {
                viewModel.initiatePrintTicket()
            },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Print Ticket",
                modifier = Modifier.padding(8.dp)
            )
        }
        Button(
            onClick = {
                viewModel.downloadTicketAsPdf()
            },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
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
                text = stringResource(R.string.bussines_name),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(text = stringResource(R.string.bussines_address))
            Text(text = stringResource(R.string.bussines_phone))
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = stringResource(R.string.ticket_title) + viewModel.code) // Should be dynamic
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = stringResource(R.string.ticket_date) + GetCurrentDateTime().getCurrentDateTime()) // Should be dynamic
            Text(text = stringResource(R.string.ticket_client)+ nameClient)
            Text(text = stringResource(R.string.ticket_document) + numDocClient)
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.ticket_quantity),
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = stringResource(R.string.ticket_product),
                    modifier = Modifier.weight(2f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = stringResource(R.string.ticket_price),
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
                Text(
                    text = stringResource(R.string.ticket_total),
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(thickness = 1.dp)
            }
            HorizontalDivider(thickness = 1.dp)
            ListProducts(
                products = products
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.ticket_total_title),
                    modifier = Modifier.padding(end = 8.dp)
                )
                // This should be dynamically calculated in the ViewModel or here
                val totalAmount = products.sumOf { it.price * it.stock }
                Text(
                    text = String.format(Locale.US,"%.2f", totalAmount),
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = stringResource(R.string.ticket_thanks),
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
                Text(text = "${product.stock}", modifier = Modifier.weight(1f))
                VerticalDivider(thickness = 1.dp)
                Text(text = product.name, modifier = Modifier.weight(2f))
                VerticalDivider(thickness = 1.dp)
                Text(text = "${product.price}", modifier = Modifier.weight(1f))
                VerticalDivider(thickness = 1.dp)
                Text(text = "${product.price * product.stock}", modifier = Modifier.weight(1f))
            }
            HorizontalDivider(thickness = 1.dp)
        }
    }
}
