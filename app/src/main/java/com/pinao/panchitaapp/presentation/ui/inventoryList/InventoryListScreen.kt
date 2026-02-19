package com.pinao.panchitaapp.presentation.ui.inventoryList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.SnackbarDuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.presentation.ui.Screen
import com.pinao.panchitaapp.presentation.ui.login.UiText
import org.koin.androidx.compose.koinViewModel
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.ProductModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    viewModel: InventoryListViewModel = koinViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is InventoryListUiState.Error -> {
                val errorState = uiState as InventoryListUiState.Error
                val message = when (val uiText = errorState.message) {
                    is UiText.DynamicString -> uiText.value
                    is UiText.StringResource -> context.resources.getString(
                        uiText.resId, *uiText.args
                    )
                }
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }

    }

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.inventary_center)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = viewModel::onNavigateToAddItem) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_produdct)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is InventoryListUiState.Loading -> CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )

                    else -> InventoryListContent(products = uiState.inventoryList,
                        onItemClick = { product ->
                            viewModel.onItemClick(product.productId)
                        },
                        onDeleteClick = { product ->
                            viewModel.onDeleteClick(product.productId)
                        })
                }
            }
        }
    }

}

@Composable
fun InventoryListContent(
    products: List<ProductModel> = emptyList(),
    onItemClick: (ProductModel) -> Unit = {},
    onDeleteClick: (ProductModel) -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { item ->
            InventoryItemCard(
                product = item,
                onItemClick = {onItemClick(item)},
                onDeleteClick = {onDeleteClick(item)}
            )
        }
    }
}

@Composable
fun InventoryItemCard(
    product: ProductModel,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.barcode, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.priceBuy.toString(), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                val stockColor =
                    if (product.stockQuantity > product.stockMin)
                        MaterialTheme.colorScheme.primary else Color.Red
                Text(
                    text = "Stock: ${product.stockQuantity}",
                    fontSize = 14.sp,
                    color = stockColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = product.priceSell.toString(), fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_produdct)
                    )
                }

            }
        }
    }
}
