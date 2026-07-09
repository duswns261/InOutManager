package com.cret.inoutmanager.presentation.ui.navigation

sealed class InventoryRoute(val route: String, val title: String) {
    data object Home : InventoryRoute("home", "홈")
    data object Inbound : InventoryRoute("inbound", "입고")
    data object Outbound : InventoryRoute("outbound", "출고")
    data object Status : InventoryRoute("status", "자재 현황")

    companion object {
        val featureRoutes = listOf(Inbound, Outbound, Status)

        fun indexOfFeature(route: String?): Int =
            featureRoutes.indexOfFirst { it.route == route }.coerceAtLeast(0)
    }
}
