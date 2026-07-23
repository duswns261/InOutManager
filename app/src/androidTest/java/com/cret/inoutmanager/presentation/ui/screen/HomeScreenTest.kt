package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import kotlin.math.abs
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun guidanceTextIsRemoved() {
        composeTestRule.setContent { HomeScreen(onNavigate = {}) }

        composeTestRule.onNodeWithText("이동할 화면을 선택하세요").assertDoesNotExist()
    }

    @Test
    fun menuGroupIsVerticallyCenteredInTheViewport() {
        composeTestRule.setContent {
            Box(modifier = Modifier.size(360.dp, 640.dp)) {
                HomeScreen(onNavigate = {})
            }
        }

        val viewportBounds = composeTestRule.onNodeWithTag(HomeScreenViewportTag).fetchSemanticsNode().boundsInRoot
        val groupBounds = composeTestRule.onNodeWithTag(HomeScreenMenuGroupTag).fetchSemanticsNode().boundsInRoot

        val viewportCenterY = viewportBounds.top + viewportBounds.height / 2f
        val groupCenterY = groupBounds.top + groupBounds.height / 2f

        assert(abs(viewportCenterY - groupCenterY) < 8f) {
            "메뉴 그룹은 viewport 세로 중앙에 있어야 합니다 (viewportCenterY=$viewportCenterY, groupCenterY=$groupCenterY)"
        }
    }

    @Test
    fun allThreeCardsShowTitleAndSubtitle() {
        composeTestRule.setContent { HomeScreen(onNavigate = {}) }

        composeTestRule.onNodeWithText(InventoryRoute.Inbound.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("신규 제품을 등록하고 입고 목록을 확인합니다").assertIsDisplayed()
        composeTestRule.onNodeWithText(InventoryRoute.Outbound.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("제품을 선택해 출고 수량을 처리합니다").assertIsDisplayed()
        composeTestRule.onNodeWithText(InventoryRoute.Status.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("전체 재고 현황을 확인하고 제품을 삭제합니다").assertIsDisplayed()
    }

    @Test
    fun cardsAreOrderedInboundThenOutboundThenStatus() {
        composeTestRule.setContent { HomeScreen(onNavigate = {}) }

        val inboundTop = composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Inbound.route))
            .fetchSemanticsNode().boundsInRoot.top
        val outboundTop = composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Outbound.route))
            .fetchSemanticsNode().boundsInRoot.top
        val statusTop = composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Status.route))
            .fetchSemanticsNode().boundsInRoot.top

        assert(inboundTop < outboundTop && outboundTop < statusTop) {
            "카드는 입고 → 출고 → 자재 현황 순서로 배치되어야 합니다"
        }
    }

    @Test
    fun tappingEachCardInvokesOnNavigateWithItsRoute() {
        var navigatedRoute: String? = null
        composeTestRule.setContent { HomeScreen(onNavigate = { navigatedRoute = it }) }

        composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Outbound.route)).performClick()

        assert(navigatedRoute == InventoryRoute.Outbound.route) {
            "카드를 누르면 해당 route로 onNavigate가 호출되어야 합니다"
        }
    }

    @Test
    fun tappingSubtitleTextStillInvokesTheFullCardClickArea() {
        var navigatedRoute: String? = null
        composeTestRule.setContent { HomeScreen(onNavigate = { navigatedRoute = it }) }

        composeTestRule.onNodeWithText("전체 재고 현황을 확인하고 제품을 삭제합니다").performClick()

        assert(navigatedRoute == InventoryRoute.Status.route) {
            "카드 내부 어느 지점을 눌러도 카드 전체 클릭 영역으로 동작해야 합니다"
        }
    }

    @Test
    fun eachCardMergesTitleAndSubtitleWithAClickAction() {
        composeTestRule.setContent { HomeScreen(onNavigate = {}) }

        composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Inbound.route))
            .assertHasClickAction()
            .assert(hasText(InventoryRoute.Inbound.title))
            .assert(hasText("신규 제품을 등록하고 입고 목록을 확인합니다"))
    }

    @Test
    fun smallViewportHeightStillAllowsScrollingToAndClickingTheLastCard() {
        var navigatedRoute: String? = null
        composeTestRule.setContent {
            Box(modifier = Modifier.size(360.dp, 320.dp)) {
                HomeScreen(onNavigate = { navigatedRoute = it })
            }
        }

        composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Status.route)).performScrollTo()
        composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Status.route)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Status.route)).performClick()

        assert(navigatedRoute == InventoryRoute.Status.route) {
            "작은 높이에서도 스크롤로 마지막 카드까지 접근하고 클릭할 수 있어야 합니다"
        }
    }

    @Test
    fun largeFontScaleKeepsCardsFromOverlappingEachOther() {
        composeTestRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density = density.density, fontScale = 2f)) {
                HomeScreen(onNavigate = {})
            }
        }

        composeTestRule.onNodeWithText(InventoryRoute.Inbound.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("신규 제품을 등록하고 입고 목록을 확인합니다").assertIsDisplayed()

        val inboundBounds = composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Inbound.route))
            .fetchSemanticsNode().boundsInRoot
        val outboundBounds = composeTestRule.onNodeWithTag(homeMenuCardTag(InventoryRoute.Outbound.route))
            .fetchSemanticsNode().boundsInRoot

        assert(inboundBounds.bottom <= outboundBounds.top) {
            "큰 글꼴에서 텍스트가 늘어나도 입고 카드와 출고 카드는 서로 겹치면 안 됩니다"
        }
    }
}
