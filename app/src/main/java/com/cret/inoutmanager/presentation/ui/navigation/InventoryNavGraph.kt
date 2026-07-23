package com.cret.inoutmanager.presentation.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.screens.HomeScreen
import com.cret.inoutmanager.presentation.ui.screens.InboundScreen
import com.cret.inoutmanager.presentation.ui.screens.OutboundScreen
import com.cret.inoutmanager.presentation.ui.screens.StatusScreen

@Composable
fun InventoryNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    products: List<Product>,
    onNavigateToFeature: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onOutboundClick: (Product) -> Unit,
    onDeleteRequest: (Product) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = InventoryRoute.Home.route,
        modifier = modifier,
        enterTransition = {
            val initialRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            if (initialRoute == InventoryRoute.Home.route || targetRoute == InventoryRoute.Home.route) {
                fadeIn(tween(250))
            } else {
                val forward = InventoryRoute.indexOfFeature(targetRoute) > InventoryRoute.indexOfFeature(initialRoute)
                slideInHorizontally(tween(250)) { width -> if (forward) width else -width } + fadeIn(tween(250))
            }
        },
        exitTransition = {
            val initialRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            if (initialRoute == InventoryRoute.Home.route || targetRoute == InventoryRoute.Home.route) {
                fadeOut(tween(250))
            } else {
                val forward = InventoryRoute.indexOfFeature(targetRoute) > InventoryRoute.indexOfFeature(initialRoute)
                slideOutHorizontally(tween(250)) { width -> if (forward) -width else width } + fadeOut(tween(250))
            }
        }
    ) {
        composable(InventoryRoute.Home.route) {
            HomeScreen(onNavigate = onNavigateToFeature)
        }
        composable(InventoryRoute.Inbound.route) {
            InboundScreen(products = products, onProductClick = onProductClick)
        }
        composable(InventoryRoute.Outbound.route) {
            OutboundScreen(
                products = products,
                onOutboundClick = onOutboundClick,
            )
        }
        composable(InventoryRoute.Status.route) {
            StatusScreen(
                products = products,
                onDeleteRequest = onDeleteRequest,
            )
        }
    }
}
