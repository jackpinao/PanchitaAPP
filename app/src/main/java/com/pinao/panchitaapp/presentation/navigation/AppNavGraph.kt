package com.pinao.panchitaapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinao.panchitaapp.presentation.ui.AppDrawer
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaScreen
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.GuiaRemisionScreen
import com.pinao.panchitaapp.presentation.ui.guiaremision.GuiaRemisionViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.PreviewTicketScreen
import com.pinao.panchitaapp.presentation.ui.home.HomeScreen
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.presentation.ui.addCategory.AddCategoryScreen
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductScreen
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.search.ProductSearchScreen
import com.pinao.panchitaapp.presentation.ui.login.LoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    rechargeViewModel: ClaroRecargaViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    loginViewModel: LoginViewModel = koinViewModel(),
    guiaRemisionViewModel: GuiaRemisionViewModel = koinViewModel(),
    addProductsViewModel: AddProductViewModel = koinViewModel()
) {
    //val scope = rememberCoroutineScope()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = currentNavBackStackEntry?.destination?.route ?: AppScreens.Home.route
    //val currentRoute = "Panchita APP"
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: ""

    // Determinar si debemos mostrar el Scaffold (TopBar/Drawer)
    // Normalmente el Login no lleva Drawer ni TopBar de la App
    val showMainUI = currentRoute != AppScreens.Login.route

    val navigationActions = remember(navController) {
        AppNavigationActions(navController = navController)
    }

    ModalNavigationDrawer(
        //gesturesEnabled = showMainUI, // Bloqueamos el drawer en el login
        drawerContent = {
            if (showMainUI) {
                AppDrawer(
                    route = currentRoute,
                    navigationToHome = { navigationActions.navigateToHome() },
                    navigationToRecarga = { navigationActions.navigateToRecarga() },
                    navigationToGuiaRemision = { navigationActions.navigateToGuiaRemision() },
                    navigationToAddProduct = { navigationActions.navigateToAddProduct() },
                    //navigationToAddCategory = { navigationActions.navigateToAddCategory() },
                    closeDrawer = { coroutineScope.launch { drawerState.close() } },
                    modifier = Modifier
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                if (showMainUI) {
                    TopAppBar(
                        title = {
                            Text(
                                text = currentRoute,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                },
                                content = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.outline_add_home_24),                             //imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            },
            modifier = Modifier
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppScreens.Login.route,
                modifier = modifier.padding(
                    if (showMainUI) paddingValues else PaddingValues(0.dp)
                )
            ) {
                composable(route = AppScreens.Login.route) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController
                    )
                }
                composable(route = AppScreens.Home.route) {
                    HomeScreen(
                        navController = navController,
                        homeViewModel = homeViewModel
                    )
                }
                composable(route = AppScreens.Recarga.route) {
                    ClaroRecargaScreen(
                        claroRecargaViewModel = rechargeViewModel
                    )
                }
                composable(route = AppScreens.GuiaRemision.route) {
                    // Add your GuiaRemisionScreen here
                    GuiaRemisionScreen(
                        viewModel = guiaRemisionViewModel,
                        navController = navController
                    )
                }
                composable(route = AppScreens.PreviewTicket.route) {
                    PreviewTicketScreen(
                        viewModel = guiaRemisionViewModel,
                        navController = navController
                    )
                }
                composable(
                    route = AppScreens.AddProduct.route + "?barcode={barcode}",
                    arguments = listOf(
                        navArgument("barcode") {
                            type = NavType.StringType
                            nullable = true // Permite que sea null (viniendo del Drawer)
                            defaultValue = null // Valor por defecto
                        }
                    )
                ) { backStackEntry ->
                    val barcode = backStackEntry.arguments?.getString("barcode")
                    // Add your AddProductScreen here
                    AddProductScreen(
                        navController = navController,
                        initialBarcode = barcode
                        //viewModel = addProductsViewModel
                    )
                }
                composable(route = AppScreens.AddCategory.route) {
                    // Add your AddCategoryScreen here
                    AddCategoryScreen(
                        navController = navController
                    )
                }
                composable(route = AppScreens.ProductSearch.route) {
                    ProductSearchScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}