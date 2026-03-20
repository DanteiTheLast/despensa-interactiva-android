package com.example.despensacuartel.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.model.SectionColor
import com.example.despensacuartel.ui.components.RadialWheel
import com.example.despensacuartel.ui.screens.AddProductScreen
import com.example.despensacuartel.ui.screens.CategoryScreen
import com.example.despensacuartel.ui.screens.ProductDetailScreen
import com.example.despensacuartel.ui.viewmodel.AddProductViewModel
import com.example.despensacuartel.ui.theme.AppColors
import androidx.compose.material3.MaterialTheme

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Category : Screen("category/{categoriaID}") {
        fun createRoute(categoriaID: String) = "category/$categoriaID"
    }
    data object Product : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    data object AddProduct : Screen("add_product")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    sectionColors: Map<Category, List<SectionColor>>,
    getItemsByCategory: (String) -> List<InventoryItem>,
    getItemById: (String) -> InventoryItem?,
    onQuantityChange: (String, Int) -> Unit,
    addProductViewModel: AddProductViewModel,
    isSyncing: Boolean = false,
    syncResult: String? = null,
    onSyncClick: () -> Unit = {},
    onClearSyncResult: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    sectionColors = sectionColors,
                    onCategoryClick = { categoryId ->
                        navController.navigate(Screen.Category.createRoute(categoryId))
                    },
                    onCenterClick = {
                        navController.navigate(Screen.AddProduct.route)
                    },
                    isSyncing = isSyncing,
                    syncResult = syncResult,
                    onSyncClick = onSyncClick,
                    onClearSyncResult = onClearSyncResult
                )
            }

            composable(
                route = Screen.Category.route,
                arguments = listOf(navArgument("categoriaID") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoriaID = backStackEntry.arguments?.getString("categoriaID") ?: ""
                val category = Category.fromId(categoriaID)

                CategoryScreen(
                    categoryName = category?.displayName ?: categoriaID,
                    categoryEmoji = category?.emoji ?: "",
                    items = getItemsByCategory(categoriaID),
                    onItemClick = { itemId ->
                        navController.navigate(Screen.Product.createRoute(itemId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Product.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val item = getItemById(productId)

                ProductDetailScreen(
                    item = item,
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(productId, newQuantity)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.AddProduct.route) {
                AddProductScreen(
                    viewModel = addProductViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    sectionColors: Map<Category, List<SectionColor>>,
    onCategoryClick: (String) -> Unit,
    onCenterClick: () -> Unit = {},
    isSyncing: Boolean = false,
    syncResult: String? = null,
    onSyncClick: () -> Unit = {},
    onClearSyncResult: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(syncResult) {
        syncResult?.let {
            snackbarHostState.showSnackbar(it)
            onClearSyncResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Despensa del Cuartel",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            IconButton(
                onClick = onSyncClick,
                enabled = !isSyncing,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Sincronizar con BD",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            RadialWheel(
                sectionColors = sectionColors,
                onSectionClick = onCategoryClick,
                onCenterClick = onCenterClick
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
