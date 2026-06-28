package com.pinao.panchitaapp.presentation.ui.fastSale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.common.toCurrency
import com.pinao.panchitaapp.presentation.ui.Screen
import com.pinao.panchitaapp.presentation.ui.common.PrinterSelectionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastSalePreviewScreen(
    viewModel: FastSaleViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.saleCompleted) {
        if (uiState.saleCompleted) {
            navController.popBackStack()
            viewModel.resetSale()
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = viewModel::initiatePrintTicket,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("Imprimir Ticket")
                    }
                    OutlinedButton(
                        onClick = viewModel::downloadTicket,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("Descargar PDF")
                    }
                    TextButton(
                        onClick = viewModel::finalizeSale,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Finalizar Venta")
                    }
                }
            }
        ) { padding ->
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fast_sale_preview_title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
                            Text(
                                text = stringResource(R.string.bussines_name),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(text = stringResource(R.string.bussines_address), style = MaterialTheme.typography.labelSmall)
                            Text(text = stringResource(R.string.bussines_phone), style = MaterialTheme.typography.labelSmall)

                            HorizontalDivider(Modifier.padding(vertical = 8.dp))

                            // Ticket Info
                            Text(text = "${stringResource(R.string.ticket_title)} #${viewModel.sessionTicketId}", fontWeight = FontWeight.Bold)
                            Text(text = "${stringResource(R.string.ticket_date)} ${GetCurrentDateTime().getCurrentDateTime()}")
                            if (uiState.clientName.isNotBlank()) {
                                Text(text = "${stringResource(R.string.ticket_client)} ${uiState.clientName}")
                            }
                            if (uiState.clientDoc.isNotBlank()) {
                                Text(text = "${stringResource(R.string.ticket_document)} ${uiState.clientDoc}")
                            }

                            HorizontalDivider(Modifier.padding(vertical = 8.dp))

                            // Items Table Header
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Cant", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text(text = "Producto", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text(text = "P. Unit", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text(text = "Total", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

                            // Items
                            uiState.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(text = "${item.quantity}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                                    Text(text = item.name, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall)
                                    Text(text = "%.2f".format(item.price), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = "%.2f".format(item.price * item.quantity),
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            HorizontalDivider(Modifier.padding(vertical = 8.dp))

                            // Total
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "TOTAL: ", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    text = uiState.total.toCurrency(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = stringResource(R.string.ticket_thanks),
                                modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
