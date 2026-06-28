package com.pinao.panchitaapp.presentation.ui.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel

/**
 * Componente de hoja inferior (BottomSheet) reutilizable y sumamente estético diseñado con Material 3.
 * Gestiona el escaneo, selección de permisos y vinculación de impresoras térmicas Bluetooth.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    pairedPrinters: List<BluetoothDeviceModel>,
    selectedPrinterAddress: String?,
    onPrinterSelected: (BluetoothDeviceModel) -> Unit,
    isBluetoothEnabled: Boolean,
    onRefreshPrinters: () -> Unit
) {
    val context = LocalContext.current
    var permissionsGranted by remember {
        mutableStateOf(hasBluetoothPermissions(context))
    }

    // Solicitud interactiva de permisos de Bluetooth según versión de Android (SDK 31+)
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
        if (permissionsGranted) {
            onRefreshPrinters()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la Hoja Inferior con Iconografía Elegante
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Printer Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Configurar Impresora",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = onDismissRequest) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Estado: Si no tiene permisos de Bluetooth
            if (!permissionsGranted) {
                PermissionRequiredScreen(
                    onRequestPermissions = {
                        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            arrayOf(
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                            )
                        } else {
                            arrayOf(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN
                            )
                        }
                        permissionsLauncher.launch(permissions)
                    }
                )
            }
            // 2. Estado: Si Bluetooth está apagado
            else if (!isBluetoothEnabled) {
                BluetoothDisabledScreen(
                    onOpenSettings = {
                        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                    }
                )
            }
            // 3. Estado: Todo bien, listar impresoras
            else {
                PrintersListScreen(
                    pairedPrinters = pairedPrinters,
                    selectedPrinterAddress = selectedPrinterAddress,
                    onPrinterSelected = onPrinterSelected,
                    onOpenSettings = {
                        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                    },
                    onRefresh = onRefreshPrinters
                )
            }
        }
    }
}

@Composable
private fun PermissionRequiredScreen(onRequestPermissions: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Permiso de Bluetooth Requerido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "PanchitaAPP requiere acceder al Bluetooth para escanear y conectarse a la impresora termica de 80mm.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Conceder Permiso")
            }
        }
    }
}

@Composable
private fun BluetoothDisabledScreen(onOpenSettings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth Off",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bluetooth Desactivado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Por favor, enciende el Bluetooth en los ajustes de tu telefono para poder conectarte a la impresora termica.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onOpenSettings) {
                Text("Activar Bluetooth")
            }
        }
    }
}

@Composable
private fun PrintersListScreen(
    pairedPrinters: List<BluetoothDeviceModel>,
    selectedPrinterAddress: String?,
    onPrinterSelected: (BluetoothDeviceModel) -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Impresoras Vinculadas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pairedPrinters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No se encontraron impresoras vinculadas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onOpenSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vincular en Ajustes")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pairedPrinters) { device ->
                    val isSelected = device.address == selectedPrinterAddress
                    val cardBorder = if (isSelected) {
                        CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                            width = 2.dp
                        )
                    } else {
                        CardDefaults.outlinedCardBorder()
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPrinterSelected(device) },
                        border = cardBorder,
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Printer Device",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = device.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onOpenSettings,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vincular otro dispositivo en Android")
            }
        }
    }
}

/**
 * Helper para validar permisos de Bluetooth de acuerdo a la API del dispositivo.
 */
private fun hasBluetoothPermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED
    }
}
