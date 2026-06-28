package com.pinao.panchitaapp.presentation.ui.moduloVenta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.ui.Screen
import com.pinao.panchitaapp.presentation.ui.common.PrinterSelectionBottomSheet
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla de Vista Previa del Ticket.
 * Implementa Clean Architecture y reactividad total mediante el UiState del ViewModel.
 */
@Composable
fun PreviewTicketScreen(
    viewModel: GuiaRemisionViewModel = koinViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejo reactivo de estados de descarga, impresión y errores
    LaunchedEffect(uiState.downloadStatus) {
        uiState.downloadStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearDownloadStatus()
        }
    }

    LaunchedEffect(uiState.printingStatus) {
        uiState.printingStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearPrintingStatus()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (uiState.showPrinterSelection) {
        PrinterSelectionBottomSheet(
            onDismissRequest = { viewModel.showPrinterSelectionSheet(false) },
            pairedPrinters = uiState.pairedPrinters,
            selectedPrinterAddress = uiState.selectedPrinterAddress,
            onPrinterSelected = viewModel::selectPrinter,
            isBluetoothEnabled = viewModel.isBluetoothEnabled(),
            onRefreshPrinters = viewModel::loadPairedPrinters
        )
    }

    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                PreviewBottomActions(
                    onPrint = viewModel::initiatePrintTicket,
                    onDownload = viewModel::downloadTicket,
                    onBack = {
                        navController.popBackStack()
                        viewModel.finalizeSale()
                    }
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                TicketPreviewContent(
                    uiState = uiState,
                    ticketId = viewModel.sessionTicketId,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun TicketPreviewContent(
    uiState: GuiaRemisionUiState,
    ticketId: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Vista Previa del Ticket",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Información del Negocio
                BusinessHeader()

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Información del Cliente y Ticket
                TicketDetailHeader(
                    ticketId = ticketId,
                    clientName = uiState.clientName,
                    clientDoc = uiState.clientDoc
                )

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Tabla de Productos
                ProductsTableHeader()
                HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

                ProductItemsList(uiState.products)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Resumen de Totales
                TotalSummary(uiState.products)

                Text(
                    text = stringResource(R.string.ticket_thanks),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun BusinessHeader() {
    Text(
        text = stringResource(R.string.bussines_name),
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    )
    Text(text = stringResource(R.string.bussines_address), style = MaterialTheme.typography.labelSmall)
    Text(text = stringResource(R.string.bussines_phone), style = MaterialTheme.typography.labelSmall)
}

@Composable
private fun TicketDetailHeader(ticketId: String, clientName: String, clientDoc: String) {
    Text(text = "${stringResource(R.string.ticket_title)} #$ticketId", fontWeight = FontWeight.Bold)
    Text(text = "${stringResource(R.string.ticket_date)} ${GetCurrentDateTime().getCurrentDateTime()}")
    Text(text = "${stringResource(R.string.ticket_client)} $clientName")
    Text(text = "${stringResource(R.string.ticket_document)} $clientDoc")
}

@Composable
private fun ProductsTableHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Cant",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Producto",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "P. Unit",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Total",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun ProductItemsList(products: List<ProductModel>) {
    products.forEach { product ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        ) {
            Text(text = "${product.stockQuantity}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
            Text(text = product.name, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall)
            Text(
                text = "%.2f".format(product.priceSell),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "%.2f".format(product.priceSell * product.stockQuantity),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun TotalSummary(products: List<ProductModel>) {
    val total = products.sumOf { it.priceSell * it.stockQuantity }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "TOTAL A PAGAR: ", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        Text(
            text = "S/. %.2f".format(total),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PreviewBottomActions(
    onPrint: () -> Unit,
    onDownload: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = onPrint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Imprimir Ticket")
        }
        OutlinedButton(
            onClick = onDownload,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Descargar PDF")
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Finalizar y Volver")
        }
    }
}
