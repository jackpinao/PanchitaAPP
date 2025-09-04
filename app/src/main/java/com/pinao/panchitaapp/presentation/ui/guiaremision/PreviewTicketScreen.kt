package com.pinao.panchitaapp.presentation.ui.guiaremision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pinao.panchitaapp.presentation.ui.Screen

@Composable
fun PreviewTicketScreen() {
    // This function is a placeholder for the preview of the Ticket Screen.
    // It can be used to display a static preview of the UI without needing to run the app.
    // You can implement your preview logic here.
    PreviewTicketContent()
}

@Composable
fun PreviewTicketContent() {
    Screen {
        Scaffold(
            bottomBar = {
                BottomBar()
            }
        ) { innerPadding ->
            // Here you can add the content of the Ticket Screen.
            // For example, you can display a preview of a ticket layout.
            // This is just a placeholder for the actual content.
            // Replace with your actual UI components.
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                CenterAppPreviewTicket()
            }
        }
    }
}

@Composable
private fun BottomBar() {
    Column {
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Print Ticket",
                modifier = Modifier.padding(8.dp)
            )
        }
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Download Ticket",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun CenterAppPreviewTicket() {
    // This function can be used to display the main content of the Ticket Screen.
    // You can implement your UI components here.
    // For example, you can show a preview of a ticket layout or any other relevant information.
    // This is just a placeholder for the actual content.
    Text(
        text = "Preview of Ticket Screen",
        modifier = Modifier.padding(16.dp)
    )
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Bodega 'El Chasqui'",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Add more details about the ticket here
            Text(text = "Av. Antigua Panamericana Nª451, Mala, Cañete, Lima")
            Text(text = "Telefono: 12345678")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "Ticket #123456")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "Date: 2023-10-01")
            Text(text = "Time: 10:00 AM")
            Text(text = "Cliente: Juan Pérez")
            Text(text = "Documento: 123456789")
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }
        //Encabezados de la tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cant.",
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(thickness = 1.dp)
            Text(
                text = "Producto",
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(thickness = 1.dp)
            Text(
                text = "Precio",
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(thickness = 1.dp)
            Text(
                text = "Total",
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(thickness = 1.dp)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "1 |")
            Text(text = "Producto 1 |")
            Text(text = "10.00 |")
            Text(text = "10.00 |")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "2 |")
            Text(text = "Producto 2 |")
            Text(text = "20.00 |")
            Text(text = "40.00 |")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Total a Pagar:",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "70.00",
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = "¡¡GRACIAS POR SU COMPRA!!",
            modifier = Modifier.padding(top = 8.dp)
        )

    }
}