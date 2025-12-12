package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun AddCategoryScreen(navController: NavController) {
    AddCategoryContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryContent(navController: NavController) {
    Screen {
        Scaffold(
            topBar = {
                AddCategoryTopBar(onBackClick = { navController.popBackStack() })
            },
            bottomBar = {
                AddCategoryBottomBar()
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                // Contenido de la pantalla
                AddCategoryCenter()
            }
        }
    }
}

@Composable
fun AddCategoryCenter() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            label = { Text("Nombre de Categoria") },
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Row(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "",
                onValueChange = { /*TODO*/ },
                label = { Text("Ganancia") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Text(
                text = " % ",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.5f)
            )
        }
    }
}

@Composable
fun AddCategoryBottomBar() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Nueva Categoria") },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(16.dp)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddCategoryScreenPreview() {
    AddCategoryContent(rememberNavController())
}
