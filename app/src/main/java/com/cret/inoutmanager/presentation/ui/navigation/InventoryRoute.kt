package com.cret.inoutmanager.presentation.ui.navigation

sealed class InventoryRoute(val route: String, val title: String) {
    data object Home : InventoryRoute("home", "홈")
    data object Inbound : InventoryRoute("inbound", "입고")
    data object Outbound : InventoryRoute("outbound", "출고")
    data object Status : InventoryRoute("status", "자재 현황")

    companion object {
        // sealed class 초기화 도중 companion object가 형제 data object를 즉시 참조하면
        // ART의 순환 클래스 초기화로 인해 해당 object가 null로 읽힐 수 있어 lazy로 늦춘다.
        val featureRoutes: List<InventoryRoute> by lazy { listOf(Inbound, Outbound, Status) }

        fun indexOfFeature(route: String?): Int =
            featureRoutes.indexOfFirst { it.route == route }.coerceAtLeast(0)
    }
}
