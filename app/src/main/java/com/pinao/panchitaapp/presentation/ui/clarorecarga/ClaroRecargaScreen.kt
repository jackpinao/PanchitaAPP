package com.pinao.panchitaapp.presentation.ui.clarorecarga

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TabRow
import androidx.compose.material.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.ui.Screen
import androidx.core.net.toUri

//import org.koin.androidx.compose.koinViewModel
//import org.koin.androidx.viewmodel
//import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClaroRecargaScreen(
    claroRecargaViewModel: ClaroRecargaViewModel
) {

//    val state by claroRecargaViewModel.state.collectAsState()
    var isEnabled by rememberSaveable { mutableStateOf(false) }
    var isValRechargeAmount by rememberSaveable { mutableStateOf("") }
    var isNumPhone by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<RechangeUiState>(
        initialValue = RechangeUiState.Loading,
        key1 = lifecycle,
        key2 = claroRecargaViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            claroRecargaViewModel.uiState.collect {
                value = it
            }
        }
    }

    when (uiState) {
        is RechangeUiState.Error -> {

        }

        RechangeUiState.Loading -> {
            CircularProgressIndicator()
        }

        is RechangeUiState.Success -> {
            ClaroRecargaScreenContent(
                isEnabled,
                onEnable = { isEnabled = it },
                isValRechargeAmount,
                onValRechargeAmount = { isValRechargeAmount = it },
                claroRecargaViewModel,
                isNumPhone,
                onNumPhone = { isNumPhone = it },
                context,
                (uiState as RechangeUiState.Success).rechangeList
            )
        }
    }

//    state.rechange?.let {
//        isEnabled = it.amount != 0
//        isValRechargeAmount = it.amount.toString()
//        isNumPhone = it.numPhone
//    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClaroRecargaScreenContent(
    isEnabled: Boolean,
    onEnable: (Boolean) -> Unit,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit,
    claroRecargaViewModel: ClaroRecargaViewModel,
    isNumPhone: String,
    onNumPhone: (String) -> Unit,
    context: Context,
    listRechange: List<Rechange>
) {

    Screen {
        TopBar(
            PaddingValues(10.dp),
            isEnabled,
            onEnable,
            isValRechargeAmount,
            onValRechargeAmount,
            claroRecargaViewModel,
            isNumPhone,
            onNumPhone,
            context,
            listRechange
        )

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TopBar(
    padding: PaddingValues,
    isEnabled: Boolean,
    onEnable: (Boolean) -> Unit,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit,
    claroRecargaViewModel: ClaroRecargaViewModel,
    isNumPhone: String,
    onNumPhone: (String) -> Unit,
    context: Context,
    listRechange: List<Rechange>
) {

    val tabs = listOf(
        TabData("Recarga", ImageVector.vectorResource(R.drawable.baseline_add_call_24)),
        TabData("Historial", ImageVector.vectorResource(R.drawable.baseline_history_24))
    )
    val selectedTab = remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab.value) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.value == index,
                    onClick = { selectedTab.value = index },
                    text = { Text(text = tab.title) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null
                        )
                    }
                )
            }
        }
        when (selectedTab.value) {
            0 -> {
                CenterApp(
                    padding,
                    isEnabled,
                    onEnable,
                    isValRechargeAmount,
                    onValRechargeAmount,
                    isNumPhone,
                    onNumPhone,
                    claroRecargaViewModel,
                    context
                )
            }

            1 -> {
                CenterApp2(
                    padding,
                    claroRecargaViewModel,
                    listRechange = listRechange
                )
            }
        }
    }

}

data class TabData(val title: String, val icon: ImageVector)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CenterApp(
    padding: PaddingValues,
    isEnabled: Boolean,
    onEnable: (Boolean) -> Unit,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit,
    isNumPhone: String,
    onNumPhone: (String) -> Unit,
    claroRecargaViewModel: ClaroRecargaViewModel,
    context: Context
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        item {
            val text = "telefono"
            val num = 9
            AddTextFieldPhone(
                text, num,
                isNumPhone,
                onNumPhone,
            )
        }
        item {
            LazyRow {
                val numRec = listOf(3, 5, 7, 10, 15, 20)
                val text = "Soles"
                items(numRec.size) { it ->
                    var isClick by rememberSaveable { mutableStateOf(false) }
                    AddButtonOutlined(
                        numRec[it].toString(), text,
                        isClick,
                        onClick = { isClick = it },
                        isValRechargeAmount,
                        onValRechargeAmount,
                        isEnabled,
                    )
                }
                item {
                    AddButtonOutlinedOther(
                        "Otros",
                        "",
                        isEnabled,
                        onEnable,
                        onValRechargeAmount,
                    )
                }
            }
            AddTextFieldAmount(
                "monto",
                2,
                isEnabled,
                isValRechargeAmount,
                onValRechargeAmount
            )
        }
        item {
            AddButtonElevate(
                isNumPhone,
                isValRechargeAmount,
                context,
                claroRecargaViewModel
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenterApp2(
    padding: PaddingValues,
    claroRecargaViewModel: ClaroRecargaViewModel,
    listRechange: List<Rechange>
) {
    val datePickerState = rememberDatePickerState()
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier)
    val openDialog = remember { mutableStateOf(false) }
    val dateTime = GetCurrentDateTime().getCurrentDateTime2()
    var isDate by rememberSaveable { mutableStateOf(dateTime) }
    val onDate: (String) -> Unit = { isDate = it }
    val coroutineScope = rememberCoroutineScope()
    val rechanges by claroRecargaViewModel.dateFilterRechanges.collectAsState()
//    var filterDate: String? = null
    var filterDate by rememberSaveable { mutableStateOf("") }
    val amountTotal: Int = listRechange.sumOf { it.amount }
    val amountTotal2: Int = rechanges.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

//        var filterDate by rememberSaveable { mutableStateOf("") }
        Text(text = "Historial de recargas")
        //DatePicker(state = datePickerState)
        Text(text = "Fecha de recarga")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(isDate)
            IconButton(
                onClick = {
                    openDialog.value = true

                },
                modifier = Modifier.padding(end = 10.dp, start = 10.dp),
                enabled = true
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = null
                )
            }
            if (openDialog.value) {
                val confirmEnabled = remember {
                    derivedStateOf {
                        //datePickerState.selectedDateMillis != null
                        true
                    }
                }
                DatePickerDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false

                                filterDate = datePickerState.selectedDateMillis?.let {
                                    GetCurrentDateTime().getCurrentDateTime3(
                                        it
                                    )
                                }.toString()
                                println(filterDate)
                                onDate(filterDate)
                                claroRecargaViewModel.getForDateRechange(filterDate)
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
            if (filterDate == "") {
                Text(
                    text = "Total: $amountTotal"
                )

            }else {
                Text(
                    text = "Total: $amountTotal2"
                )
            }
        }


        LazyColumn {
            if (filterDate == "") {
                if (listRechange.isEmpty()) {
                    item {
                        Text("No hay recargas")
                    }
                } else {
                    items(listRechange, key = { it.id }) { rechange ->
                        ItemRechange(rechange, claroRecargaViewModel)
                         rechange.amount
                    }
                }
            } else {
                if (rechanges.isEmpty()) {
                    item {
                        Text("No hay recargas")
                    }
                } else {
                    items(rechanges) { rechange ->
                        ItemRechange(rechange, claroRecargaViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRechange(rechange: Rechange, claroRecargaViewModel: ClaroRecargaViewModel) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface,
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.primary
        ),
        modifier = Modifier
            //.size(width = 240.dp, height = 100.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Número telefónico: " + rechange.numPhone,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Monto: " + rechange.amount,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Fecha: " + rechange.date,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
private fun AddTextFieldPhone(
    text: String,
    num: Int,
    isNumPhone: String,
    onNumPhone: (String) -> Unit
) {
    LimitedTextField(
        value = isNumPhone,
        onValueChange = { onNumPhone(it) },
        label = { Text("Ingrese el número de $text") },
        modifier = Modifier.padding(16.dp),
        placeholder = { Text("000000000") },
        maxLength = num,
        enable = true
    )
}

@Composable
private fun AddTextFieldAmount(
    text: String, num: Int,
    isEnabled: Boolean,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit
) {
    LimitedTextField(
        value = isValRechargeAmount,
        onValueChange = { onValRechargeAmount(it) },
        label = { Text("Ingrese el número de $text") },
        modifier = Modifier.padding(16.dp),
        placeholder = { Text("00") },
        maxLength = num,
        enable = isEnabled
    )
}


@Composable
private fun AddButtonOutlined(
    num: String,
    text: String,
    isClick: Boolean,
    onClick: (Boolean) -> Unit,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit,
    isEnabled: Boolean
) {
    OutlinedButton(
        onClick = {

            if (!isClick) {
                onValRechargeAmount(num)
                onClick(true)
            } else {
                onValRechargeAmount("")
                onClick(false)
            }

        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isClick && isValRechargeAmount == num) Color.Gray else Color.White,
            contentColor = Color.Red
        ),
        enabled = !isEnabled
    ) {
        Text(
            "$num\n$text",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddButtonOutlinedOther(
    num: String,
    text: String,
    isTextFieldEnabled: Boolean,
    onEnableTextField: (Boolean) -> Unit,
    onValRechargeAmount: (String) -> Unit,
) {
    OutlinedButton(
        onClick = {
            if (!isTextFieldEnabled) {
                onEnableTextField(true)
                onValRechargeAmount("")
            } else {
                onEnableTextField(false)
            }
        }
    ) {
        Text(
            "$num\n$text",
            textAlign = TextAlign.Center
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AddButtonElevate(
    isNumPhone: String,
    isValRechargeAmount: String,
    context: Context,
    claroRecargaViewModel: ClaroRecargaViewModel
) {
    ElevatedButton(
        onClick = {

            val date = GetCurrentDateTime().getCurrentDateTime()
            val rechange = Rechange(
                numPhone = isNumPhone,
                date = date,
                amount = isValRechargeAmount.toInt()
            )

            claroRecargaViewModel.updateRechange(rechange)
            //claroRecargaViewModel.updateRechange2(isNumPhone, isValRechargeAmount.toInt(), date)

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:*789*1*$isNumPhone*$isValRechargeAmount*1*1357#".toUri()
            }
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .wrapContentWidth(align = Alignment.End),
        enabled = isValRechargeAmount.isNotEmpty() && isNumPhone.length == 9
    ) {
        Text(
            text = "RECARGAR",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
fun LimitedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    maxLength: Int,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    enable: Boolean
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                onValueChange(it)
            }
        },
        label = label,
        visualTransformation = VisualTransformation.None,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enable,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

