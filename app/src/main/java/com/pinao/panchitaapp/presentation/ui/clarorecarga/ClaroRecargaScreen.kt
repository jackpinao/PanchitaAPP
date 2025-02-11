package com.pinao.panchitaapp.presentation.ui.clarorecarga

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.presentation.ui.Screen
//import org.koin.androidx.compose.koinViewModel
//import org.koin.androidx.viewmodel
//import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClaroRecargaScreen(
    claroRecargaViewModel: ClaroRecargaViewModel
) {

    val state by claroRecargaViewModel.state.collectAsState()
    var isEnabled by rememberSaveable { mutableStateOf(false) }
    var isValRechargeAmount by rememberSaveable { mutableStateOf("") }
    var isNumPhone by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    state.rechange?.let {
        isEnabled = it.amount != 0
        isValRechargeAmount = it.amount.toString()
        isNumPhone = it.numPhone
    }

    ClaroRecargaScreenContent(
        isEnabled,
        onEnable = { isEnabled = it },
        isValRechargeAmount,
        onValRechargeAmount = { isValRechargeAmount = it },
        claroRecargaViewModel,
        isNumPhone,
        onNumPhone = { isNumPhone = it },
        context,
    )
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
    context: Context
) {

    Screen {
        Scaffold(
            topBar = {
                TopBar()
            },
            bottomBar = {
                BottomBar()
            }
        ) { padding ->
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
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar() {
    TopAppBar(
        title = { Text(text = "Recarga Claro") }
    )
}

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

@Composable
private fun BottomBar() {
    BottomAppBar() {
        Text(text = "Recarga Claro Bottom")
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

@Composable
private fun AddButtonElevate(
    isNumPhone: String,
    isValRechargeAmount: String,
    context: Context,
    claroRecargaViewModel: ClaroRecargaViewModel
) {
    ElevatedButton(
        onClick = {
            val rechange = Rechange(
                numPhone = isNumPhone,
                amount =  isValRechargeAmount.toInt()
            )

            claroRecargaViewModel.updateRechange(rechange)

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:*789*1*$isNumPhone*$isValRechargeAmount*1*1357#")
            }
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .wrapContentWidth(align = Alignment.End),
        enabled = isNumPhone.isNotEmpty() && isValRechargeAmount.isNotEmpty()
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