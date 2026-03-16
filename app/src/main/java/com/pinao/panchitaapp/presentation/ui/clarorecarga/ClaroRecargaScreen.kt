package com.pinao.panchitaapp.presentation.ui.clarorecarga

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun ClaroRecargaScreen(
    claroRecargaViewModel: ClaroRecargaViewModel
) {

    var isEnabled by rememberSaveable { mutableStateOf(false) }
    var isValRechargeAmount by rememberSaveable { mutableStateOf("") }
    var isNumPhone by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val uiState by claroRecargaViewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is RechangeUiState.Error -> {
            Log.e(
                "ClaroRecargaScreen",
                "Error loading recargas: ${(uiState as RechangeUiState.Error).throwable}"
            )
        }

        RechangeUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
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
                (uiState as RechangeUiState.Success).rechangeModelList
            )
        }
    }
}

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
    listRechangeModel: List<RechangeModel>
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
            listRechangeModel
        )

    }
}

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
    listRechangeModel: List<RechangeModel>
) {

    val tabs = listOf(
        TabData(
            stringResource(R.string.recharge_tab),
            ImageVector.vectorResource(R.drawable.baseline_add_call_24)
        ),
        TabData(
            stringResource(R.string.history_tab),
            ImageVector.vectorResource(R.drawable.baseline_history_24)
        )
    )
    val selectedTab = remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab.intValue) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.intValue == index,
                    onClick = { selectedTab.intValue = index },
                    text = { Text(text = tab.title) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title
                        )
                    }
                )
            }
        }
        when (selectedTab.intValue) {
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
                    listRechangeModel = listRechangeModel
                )
            }
        }
    }

}

data class TabData(val title: String, val icon: ImageVector)

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
            LazyRow(
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    AddTextFieldPhone(
                        isNumPhone,
                        onNumPhone,
                    )
                }
                item {
                    EraserText(
                        onText = onNumPhone,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val numRec = listOf(3, 5, 7, 10, 15, 20)
                items(numRec, key = { it }) { amount ->
                    var isClick by rememberSaveable { mutableStateOf(false) }
                    AddButtonOutlined(
                        amount.toString(),
                        isClick,
                        onClick = { isClick = it },
                        isValRechargeAmount,
                        onValRechargeAmount,
                        isEnabled,
                    )
                }
                item {
                    AddButtonOutlinedOther(
                        isEnabled,
                        onEnable,
                        onValRechargeAmount,
                    )
                }
            }
            AddTextFieldAmount(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenterApp2(
    padding: PaddingValues,
    claroRecargaViewModel: ClaroRecargaViewModel,
    listRechangeModel: List<RechangeModel>
) {
    val datePickerState = rememberDatePickerState()
    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier)
    val openDialog = remember { mutableStateOf(false) }
    val dateTime = GetCurrentDateTime().getCurrentDateTime2()
    var isDate by rememberSaveable { mutableStateOf(dateTime) }
    val onDate: (String) -> Unit = { isDate = it }

    val rechanges by claroRecargaViewModel.dateFilterRechanges.collectAsStateWithLifecycle()
    var filterDate by rememberSaveable { mutableStateOf("") }
    val amountTotal: Int = listRechangeModel.sumOf { it.amount }
    val amountTotal2: Int = rechanges.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

        Text(text = stringResource(R.string.history_title))
        Text(text = stringResource(R.string.recharge_date))

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
                    contentDescription = stringResource(R.string.recharge_date)
                )
            }
            if (openDialog.value) {
                val confirmEnabled = remember {
                    derivedStateOf {
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
                            Text(stringResource(R.string.ok_button))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text(stringResource(R.string.cancel_button))
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
                    text = stringResource(R.string.total_label, amountTotal)
                )

            } else {
                Text(
                    text = stringResource(R.string.total_label, amountTotal2)
                )
            }
        }


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (filterDate == "") {
                if (listRechangeModel.isEmpty()) {
                    item {
                        Text(stringResource(R.string.no_recharges))
                    }
                } else {
                    items(listRechangeModel, key = { it.id }) { rechange ->
                        ItemRechange(rechange)
                    }
                }
            } else {
                if (rechanges.isEmpty()) {
                    item {
                        Text(stringResource(R.string.no_recharges))
                    }
                } else {
                    items(rechanges, key = { it.id }) { rechange ->
                        ItemRechange(rechange)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRechange(rechangeModel: RechangeModel) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.phone_number_label, rechangeModel.numPhone),
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.amount_label, rechangeModel.amount),
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.date_label, rechangeModel.date),
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
private fun AddTextFieldPhone(
    isNumPhone: String,
    onNumPhone: (String) -> Unit
) {
    val num = 9
    LimitedTextField(
        value = isNumPhone,
        onValueChange = { onNumPhone(it) },
        label = { Text(stringResource(R.string.enter_phone_number)) },
        modifier = Modifier.padding(16.dp),
        placeholder = { Text(stringResource(R.string.phone_placeholder)) },
        maxLength = num,
        enable = true
    )
}

@Composable
private fun EraserText(
    onText: (String) -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = {
            onText("")
        },
        modifier = modifier,
        enabled = true
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_auto_delete_24),
            contentDescription = stringResource(R.string.delete_description)
        )
    }
}

@Composable
private fun AddTextFieldAmount(
    isEnabled: Boolean,
    isValRechargeAmount: String,
    onValRechargeAmount: (String) -> Unit
) {
    val num = 2
    LimitedTextField(
        value = isValRechargeAmount,
        onValueChange = { onValRechargeAmount(it) },
        label = { Text(stringResource(R.string.enter_amount)) },
        modifier = Modifier.padding(16.dp),
        placeholder = { Text(stringResource(R.string.amount_placeholder)) },
        maxLength = num,
        enable = isEnabled
    )
}


@Composable
private fun AddButtonOutlined(
    num: String,
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
            text = num + "\n" + stringResource(R.string.soles_label),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddButtonOutlinedOther(
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
            text = stringResource(R.string.others_label),
            textAlign = TextAlign.Center
        )
    }
}

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
            val rechangeModel = RechangeModel(
                numPhone = isNumPhone,
                date = date,
                amount = isValRechargeAmount.toInt()
            )

            claroRecargaViewModel.updateRechange(rechangeModel)

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
            text = stringResource(R.string.recharge_button),
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
    modifier: Modifier,
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
