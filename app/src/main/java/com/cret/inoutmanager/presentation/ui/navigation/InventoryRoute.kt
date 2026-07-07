package com.cret.inoutmanager.presentation.ui.navigation

sealed class InventoryRoute(val route: String) {
    data object Inbound : InventoryRoute("inbound")
    data object Outbound : InventoryRoute("outbound")
    data object Status : InventoryRoute("status")

    companion object {
        val ordered = listOf(Inbound, Outbound, Status)
    }
}
