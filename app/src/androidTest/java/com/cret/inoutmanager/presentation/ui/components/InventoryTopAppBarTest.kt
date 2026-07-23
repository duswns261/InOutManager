package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import org.junit.Rule
import org.junit.Test

class InventoryTopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        currentRoute: String,
        onRouteSelected: (String) -> Unit = {},
        onAddProductClick: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            InventoryTopAppBar(
                currentRoute = currentRoute,
                onRouteSelected = onRouteSelected,
                onAddProductClick = onAddProductClick,
            )
        }
    }

    @Test
    fun inboundRouteShowsAddButtonWithContentDescription() {
        setContent(currentRoute = InventoryRoute.Inbound.route)

        composeTestRule.onNodeWithTag(InventoryTopAppBarAddButtonTag).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("신규 제품 등록").assertIsDisplayed()
    }

    @Test
    fun outboundRouteDoesNotShowAddButton() {
        setContent(currentRoute = InventoryRoute.Outbound.route)

        composeTestRule.onNodeWithTag(InventoryTopAppBarAddButtonTag).assertDoesNotExist()
    }

    @Test
    fun statusRouteDoesNotShowAddButton() {
        setContent(currentRoute = InventoryRoute.Status.route)

        composeTestRule.onNodeWithTag(InventoryTopAppBarAddButtonTag).assertDoesNotExist()
    }

    @Test
    fun tappingTitleOpensMenuWithAllThreeRoutesInFixedOrder() {
        setContent(currentRoute = InventoryRoute.Inbound.route)

        composeTestRule.onNodeWithTag(InventoryTopAppBarTitleTag).performClick()

        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Inbound.route)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Status.route)).assertIsDisplayed()
    }

    @Test
    fun currentRouteMenuItemIsMarkedSelected() {
        setContent(currentRoute = InventoryRoute.Outbound.route)

        composeTestRule.onNodeWithTag(InventoryTopAppBarTitleTag).performClick()

        composeTestRule.onNodeWithContentDescription("출고, 선택됨").assertIsDisplayed()
    }

    @Test
    fun reselectingCurrentRouteClosesMenuWithoutInvokingCallback() {
        var invoked = false
        setContent(currentRoute = InventoryRoute.Inbound.route, onRouteSelected = { invoked = true })

        composeTestRule.onNodeWithTag(InventoryTopAppBarTitleTag).performClick()
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Inbound.route)).performClick()

        assert(!invoked) { "현재 화면을 재선택했을 때 onRouteSelected가 호출되면 안 됩니다" }
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).assertDoesNotExist()
    }

    @Test
    fun selectingAnotherRouteInvokesCallbackAndClosesMenu() {
        var selectedRoute: String? = null
        setContent(currentRoute = InventoryRoute.Inbound.route, onRouteSelected = { selectedRoute = it })

        composeTestRule.onNodeWithTag(InventoryTopAppBarTitleTag).performClick()
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).performClick()

        assert(selectedRoute == InventoryRoute.Outbound.route) {
            "다른 화면을 선택하면 onRouteSelected(route)가 호출되어야 합니다"
        }
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).assertDoesNotExist()
    }

    @Test
    fun menuClosesAfterTheRouteChangesExternally() {
        val currentRoute = mutableStateOf(InventoryRoute.Inbound.route)
        composeTestRule.setContent {
            InventoryTopAppBar(
                currentRoute = currentRoute.value,
                onRouteSelected = {},
                onAddProductClick = {},
            )
        }

        composeTestRule.onNodeWithTag(InventoryTopAppBarTitleTag).performClick()
        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).assertIsDisplayed()

        composeTestRule.runOnIdle { currentRoute.value = InventoryRoute.Outbound.route }

        composeTestRule.onNodeWithTag(inventoryTopAppBarMenuItemTag(InventoryRoute.Outbound.route)).assertDoesNotExist()
    }
}
