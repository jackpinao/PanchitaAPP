package com.pinao.panchitaapp.presentation.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    // Recargar dispositivos al abrir
    LaunchedEffect(Unit) {
        viewModel.refreshPairedDevices()
    }

    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Sección 1: Configuración de Impresora
                item {
                    PrinterSection(
                        uiState = uiState,
                        onRefreshClick = { viewModel.refreshPairedDevices() },
                        onDeviceSelect = { viewModel.selectPrinter(it) },
                        onPrintTestClick = { viewModel.printTestTicket() },
                        onOpenBluetoothSettings = {
                            context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                        }
                    )
                }

                // Sección 2: Datos del Negocio
                item {
                    BusinessDetailsSection(
                        uiState = uiState,
                        onNameChange = { viewModel.onStoreNameChange(it) },
                        onRucChange = { viewModel.onStoreRucChange(it) },
                        onAddressChange = { viewModel.onStoreAddressChange(it) },
                        onPhoneChange = { viewModel.onStorePhoneChange(it) },
                        onSaveClick = { viewModel.saveStoreDetails() }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun PrinterSection(
    uiState: SettingsUiState,
    onRefreshClick: () -> Unit,
    onDeviceSelect: (String) -> Unit,
    onPrintTestClick: () -> Unit,
    onOpenBluetoothSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Impresora",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Impresora Térmica (80mm)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                OutlinedButton(
                    onClick = onRefreshClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buscar", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estado de Bluetooth
            if (!uiState.isBluetoothEnabled) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BluetoothDisabled,
                            contentDescription = "Bluetooth Apagado",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Bluetooth Desactivado",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Enciende el Bluetooth para listar impresoras.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                        Button(
                            onClick = onOpenBluetoothSettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Activar", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                Text(
                    text = "Dispositivos Vinculados:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (uiState.pairedDevices.isEmpty()) {
                    Text(
                        text = "No se encontraron dispositivos Bluetooth vinculados. Vincule su impresora térmica en los ajustes de su celular.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    OutlinedButton(
                        onClick = onOpenBluetoothSettings,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vincular nuevo dispositivo")
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.pairedDevices.forEach { device ->
                            val isSelected = device.address == uiState.selectedPrinterAddress
                            DeviceItem(
                                device = device,
                                isSelected = isSelected,
                                onClick = { onDeviceSelect(device.address) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Botón de prueba
            Button(
                onClick = onPrintTestClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.selectedPrinterAddress.isNullOrBlank() && !uiState.isTestingPrint,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isTestingPrint) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Imprimiendo...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Prueba",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Imprimir Ticket de Prueba")
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: BluetoothDeviceModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = device.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionada",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BusinessDetailsSection(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onRucChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Negocio",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Datos del Negocio (Ticket)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Estos datos se sincronizan automáticamente con la nube, pero puedes modificarlos localmente para la impresión de tickets.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            OutlinedTextField(
                value = uiState.storeName,
                onValueChange = onNameChange,
                label = { Text("Nombre del Negocio") },
                placeholder = { Text("Ej. BODEGA PANCHITA") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = uiState.storeRuc,
                onValueChange = onRucChange,
                label = { Text("R.U.C.") },
                placeholder = { Text("Ej. 10452391032") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = uiState.storeAddress,
                onValueChange = onAddressChange,
                label = { Text("Dirección") },
                placeholder = { Text("Ej. Av. El Sol 456") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = uiState.storePhone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono") },
                placeholder = { Text("Ej. 984523190") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving && uiState.storeName.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cambios")
                }
            }
        }
    }
}
