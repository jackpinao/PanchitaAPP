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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
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
import java.util.Locale
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    viewModel: InventoryListViewModel = koinViewModel(),
    navController: NavController,
    windowSize: WindowSizeClass? = null
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
                    is UiText.StringResource -> context.getString(
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
                        },
                        windowSize = windowSize
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
    windowSize: WindowSizeClass? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val columns = when (windowSize?.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_action))
                                }
                            }
                            IconButton(
                                onClick = onRefresh,
                                enabled = !isRefreshing
                            ) {
                                if (isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar/Descargar productos")
                                }
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.barcode, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = String.format(Locale.getDefault(), "S/ %.2f", product.priceBuy), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val stockColor =
                    if (product.stockQuantity > product.stockMin)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Text(
                    text = stringResource(R.string.stock_count_label, product.stockQuantity),
                    style = MaterialTheme.typography.labelLarge,
                    color = stockColor,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "S/ %.2f", product.priceSell), style = MaterialTheme.typography.bodyLarge,
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
