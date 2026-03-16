package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.presentation.ui.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCategoryScreen(
    navController: NavController,
    viewModel: AddCategoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddCategoryUiState.Success -> {
                navController.popBackStack()
            }

            is AddCategoryUiState.Error -> {
                val errorState = uiState as AddCategoryUiState.Error
                val message = errorState.message.asString(context)
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }

//    val currentCategory = when (uiState) {
//        is AddCategoryUiState.Idle -> (uiState as AddCategoryUiState.Idle).category
//        is AddCategoryUiState.Loading -> (uiState as AddCategoryUiState.Loading).category
//        is AddCategoryUiState.Error -> (uiState as AddCategoryUiState.Error).category
//        else -> CategoryModel()
//    }

    AddCategoryContent(
        uiState = uiState,
        //category = currentCategory,
        snackbarHostState = snackbarHostState,
        onNameChange = viewModel::onNameChange,
        onRevenueChange = viewModel::onRevenueChange,
        onSaveClick = viewModel::saveCategory,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryContent(
    uiState: AddCategoryUiState,
    //category: CategoryModel,
    snackbarHostState: SnackbarHostState,
    onNameChange: (String) -> Unit,
    onRevenueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Screen {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AddCategoryTopBar(onBackClick = onBackClick)
            },
        ) { innerPadding ->
            val category = uiState.category
            val isLoading = uiState is AddCategoryUiState.Loading
            val isError = uiState is AddCategoryUiState.Error

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = category.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.category_name)) },
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    isError = isError,
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = if (category.revenue == 0.0) "" else category.revenue.toString(),
                        onValueChange = onRevenueChange,
                        label = { Text(stringResource(R.string.revenue)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                Spacer(modifier = Modifier.padding(15.dp))
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(text =  stringResource(R.string.new_category)) },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
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
    val category = CategoryModel(name = "Bebidas", revenue = 10.5)
    val uiState = AddCategoryUiState.Idle(category)
    AddCategoryContent(
        uiState = uiState,
        //category = category,
        snackbarHostState = SnackbarHostState(),
        onNameChange = {},
        onRevenueChange = {},
        onSaveClick = {},
        onBackClick = {}
    )
}
