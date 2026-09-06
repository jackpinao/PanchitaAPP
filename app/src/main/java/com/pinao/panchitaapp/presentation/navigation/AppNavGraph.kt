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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.presentation.ui.AppDrawer
import com.pinao.panchitaapp.presentation.ui.addCategory.AddCategoryScreen
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductScreen
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaScreen
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.fastSale.FastSalePreviewScreen
import com.pinao.panchitaapp.presentation.ui.fastSale.FastSaleScreen
import com.pinao.panchitaapp.presentation.ui.fastSale.FastSaleViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeScreen
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.inventoryList.InventoryListScreen
import com.pinao.panchitaapp.presentation.ui.inventoryList.InventoryListViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginScreen
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import com.pinao.panchitaapp.presentation.ui.moduloVenta.GuiaRemisionScreen
import com.pinao.panchitaapp.presentation.ui.moduloVenta.GuiaRemisionViewModel
import com.pinao.panchitaapp.presentation.ui.moduloVenta.PreviewTicketScreen
import com.pinao.panchitaapp.presentation.ui.moduloVenta.search.ProductSearchScreen
import com.pinao.panchitaapp.presentation.ui.settings.SettingsScreen
import com.pinao.panchitaapp.presentation.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    rechargeViewModel: ClaroRecargaViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    loginViewModel: LoginViewModel = koinViewModel(),
    guiaRemisionViewModel: GuiaRemisionViewModel = koinViewModel(),
    inventaryViewModel: InventoryListViewModel = koinViewModel(),
    fastSaleViewModel: FastSaleViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val sessionManager: SessionManager = koinInject()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: ""
    val currentRouteBase = currentRoute.substringBefore("?")

    // Determinar si debemos mostrar el Drawer
    val showMainUI = currentRouteBase != AppScreens.Login.route

    // Determinar si debemos mostrar el TopAppBar global
    val screensWithGlobalTopBar = listOf(
        AppScreens.Home.route,
        AppScreens.Recarga.route,
        AppScreens.GuiaRemision.route,
        AppScreens.InventoryList.route,
        AppScreens.FastSale.route,
        AppScreens.Settings.route
    )
    val showGlobalTopBar = currentRouteBase in screensWithGlobalTopBar

    val titleText = when (currentRouteBase) {
        AppScreens.Home.route -> stringResource(R.string.home)
        AppScreens.Recarga.route -> stringResource(R.string.recarga)
        AppScreens.GuiaRemision.route -> stringResource(R.string.guia_remision)
        AppScreens.InventoryList.route -> stringResource(R.string.inventory_title)
        AppScreens.FastSale.route -> stringResource(R.string.fast_sale_title)
        AppScreens.Settings.route -> stringResource(R.string.settings)
        else -> "PanchitaAPP"
    }

    val navigationActions = remember(navController) {
        AppNavigationActions(navController = navController)
    }

    val isExpandedScreen = windowSize.widthSizeClass == WindowWidthSizeClass.Expanded
    val usePermanentDrawer = isExpandedScreen && showMainUI

    val drawerContent = @Composable {
        if (showMainUI) {
            AppDrawer(
                route = currentRouteBase,
                userName = sessionManager.getUserName() ?: "",
                navigationToHome = { navigationActions.navigateToHome() },
                navigationToRecarga = { navigationActions.navigateToRecarga() },
                navigationToGuiaRemision = { navigationActions.navigateToGuiaRemision() },
                navigationToAddProduct = { navigationActions.navigateToAddProduct() },
                navigationToInventoryList = { navigationActions.navigateToInventary() },
                navigationToFastSale = { navigationActions.navigateToFastSale() },
                navigationToSettings = { navigationActions.navigateToSettings() },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                modifier = Modifier,
                isPermanent = usePermanentDrawer
            )
        }
    }

    val scaffoldContent = @Composable {
        Scaffold(
            topBar = {
                if (showGlobalTopBar && !usePermanentDrawer) {
                    TopAppBar(
                        title = {
                            Text(
                                text = titleText,
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
                                        imageVector = ImageVector.vectorResource(id = R.drawable.outline_add_home_24),
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
                } else if (showGlobalTopBar && usePermanentDrawer) {
                    // En pantallas expandidas, solo mostramos título si quieres, sin menú de hamburguesa
                    TopAppBar(
                        title = {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
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
                    if (showGlobalTopBar) paddingValues else if (showMainUI) PaddingValues(0.dp) else PaddingValues(
                        0.dp
                    )
                )
            ) {
                composable(route = AppScreens.Login.route) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController,
                        windowSize = windowSize
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
                        claroRecargaViewModel = rechargeViewModel,
                        windowSize = windowSize
                    )
                }
                composable(route = AppScreens.GuiaRemision.route) {
                    GuiaRemisionScreen(
                        viewModel = guiaRemisionViewModel,
                        navController = navController,
                        windowSize = windowSize
                    )
                }
                composable(route = AppScreens.PreviewTicket.route) {
                    PreviewTicketScreen(
                        viewModel = guiaRemisionViewModel,
                        navController = navController
                    )
                }
                composable(route = AppScreens.InventoryList.route) {
                    InventoryListScreen(
                        navController = navController,
                        viewModel = inventaryViewModel,
                        windowSize = windowSize
                    )
                }

                composable(
                    route = "${AppScreens.AddProduct.route}?barcode={barcode}",
                    arguments = listOf(
                        navArgument("barcode") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val barcode = backStackEntry.arguments?.getString("barcode")
                    AddProductScreen(
                        navController = navController,
                        initialBarcode = barcode,
                        windowSize = windowSize
                    )
                }

                composable(route = AppScreens.AddCategory.route) {
                    AddCategoryScreen(navController = navController)
                }

                composable(route = AppScreens.ProductSearch.route) {
                    ProductSearchScreen(navController = navController)
                }

                composable(route = AppScreens.FastSale.route) {
                    FastSaleScreen(
                        viewModel = fastSaleViewModel,
                        navController = navController,
                        windowSize = windowSize
                    )
                }

                composable(route = AppScreens.FastSalePreview.route) {
                    FastSalePreviewScreen(
                        viewModel = fastSaleViewModel,
                        navController = navController
                    )
                }

                composable(route = AppScreens.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        navController = navController
                    )
                }
            }
        }
    }

    if (usePermanentDrawer) {
        androidx.compose.material3.PermanentNavigationDrawer(
            drawerContent = drawerContent
        ) {
            scaffoldContent()
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = drawerContent,
            drawerState = drawerState
        ) {
            scaffoldContent()
        }
    }
}
