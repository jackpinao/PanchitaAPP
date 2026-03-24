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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.navigation.AppScreens
import com.pinao.panchitaapp.presentation.ui.Screen
import com.pinao.panchitaapp.presentation.ui.login.UiText
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    viewModel: InventoryListViewModel = koinViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
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

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is InventoryListViewModel.InventoryListEvent.NavigateToEdit -> {
                    navController.navigate(AppScreens.AddProduct.route + "?barcode=${event.barcode}")
                }
            }
        }
    }

    Screen {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(AppScreens.AddProduct.route) }
                ) {
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
                if (uiState is InventoryListUiState.Loading && uiState.inventoryList.isEmpty()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    InventoryListContent(
                        products = uiState.inventoryList,
                        searchQuery = searchQuery,
                        isRefreshing = uiState is InventoryListUiState.Loading,
                        onRefresh = viewModel::onRefresh,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onItemClick = { product ->
                            viewModel.onItemClick(product.productId)
                        },
                        onDeleteClick = { product ->
                            viewModel.onDeleteClick(product.productId)
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListContent(
    products: List<ProductModel> = emptyList(),
    searchQuery: String = "",
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onItemClick: (ProductModel) -> Unit = {},
    onDeleteClick: (ProductModel) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(R.string.search_product_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_action))
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.productId }) { item ->
                    InventoryItemCard(
                        product = item,
                        onItemClick = {
                            onItemClick(item)
                            expanded = false
                        },
                        onDeleteClick = { onDeleteClick(item) }
                    )
                }
            }
        }

        if (!expanded) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.productId }) { item ->
                        InventoryItemCard(
                            product = item,
                            onItemClick = { onItemClick(item) },
                            onDeleteClick = { onDeleteClick(item) }
                        )
                    }
                }
            }
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
                    text = stringResource(R.string.stock_count_label, product.stockQuantity),
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
