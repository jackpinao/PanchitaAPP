package com.pinao.panchitaapp.presentation.ui.claroDetall

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun ClaroDetallScreen(
    claroDetallViewModel: ClaroDetallViewModel
) {

    ClaroDetallScreenContent()

}

@Composable
fun ClaroDetallScreenContent() {
    Screen {
        Scaffold(
            topBar = {
                TopBar()
            },
            bottomBar = {
                BottomBar()
            }
        ) { padding ->
            CenterApp(padding)
        }
    }
}

@Composable
fun BottomBar() {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "Detalles RecargaClaro")
        }
    )
}

@Composable
fun CenterApp(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

        item {
            Text(text = "Fecha de recarga")
        }
        item {

            LazyRow {
                item {
                    Text(text = "Monto de Recarga")
                }
                item {
                    Text(text = "10274125")
                }
            }
        }
    }
}
