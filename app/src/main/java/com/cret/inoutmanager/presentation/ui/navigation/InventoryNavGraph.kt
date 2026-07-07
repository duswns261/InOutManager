package com.cret.inoutmanager.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.screens.InboundScreen
import com.cret.inoutmanager.presentation.ui.screens.OutboundScreen
import com.cret.inoutmanager.presentation.ui.screens.StatusScreen

@Composable
fun InventoryNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    products: List<Product>,
    onAddClick: () -> Unit,
    onOutboundClick: (Product) -> Unit,
    onDeleteRequest: (Product) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = InventoryRoute.Inbound.route,
        modifier = modifier,
    ) {
        composable(InventoryRoute.Inbound.route) {
            InboundScreen(
                products = products,
                onAddClick = onAddClick,
            )
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
