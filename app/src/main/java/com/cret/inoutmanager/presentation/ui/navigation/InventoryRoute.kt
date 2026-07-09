package com.cret.inoutmanager.presentation.ui.navigation

sealed class InventoryRoute(val route: String, val title: String) {
    data object Inbound : InventoryRoute("inbound", "입고")
    data object Outbound : InventoryRoute("outbound", "출고")
    data object Status : InventoryRoute("status", "자재 현황")

    companion object {
        val ordered = listOf(Inbound, Outbound, Status)

        fun indexOf(route: String?): Int =
            ordered.indexOfFirst { it.route == route }.coerceAtLeast(0)
    }
}
