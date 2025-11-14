package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun AddProductScreen(
    navController: NavController,
){
    AddProductContent(
    )
}

@Preview(showBackground = true)
@Composable
fun AddProductContent() {
    Screen {
        Scaffold(
            topBar = {
                TopApp(
                    title = "Add Product"
                )
            },
            bottomBar = {
                BottomApp(
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                CenterAppAddProduct()
            }
        }
    }
}

@Composable
private fun TopApp(title: String) {
    TODO("Not yet implemented")
}

@Composable
private fun BottomApp() {
    TODO("Not yet implemented")
}

@Composable
private fun CenterAppAddProduct() {
    Column {
        Text(
            text = "Add Product",
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            color = Color.Black
        )
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Code Product",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                color = Color.Black
            )
        }

    }
}