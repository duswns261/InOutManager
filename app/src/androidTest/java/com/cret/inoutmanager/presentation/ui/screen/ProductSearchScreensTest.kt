package com.cret.inoutmanager.presentation.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductSearchBarClearButtonTag
import com.cret.inoutmanager.presentation.ui.components.ProductSearchBarFieldTag
import org.junit.Rule
import org.junit.Test

/**
 * 입고·출고·자재 현황 세 화면에 공통 적용된 검색·정렬을 검증한다.
 * `ProductSearch.kt`의 순수 함수 규칙(초성, substring 등)은 `ProductSearchTest`(JVM 단위 테스트)에서
 * 이미 검증하므로, 여기서는 화면 배치·즉시 필터링·빈 결과·지우기·뒤로가기·기존 콜백 유지에 집중한다.
 */
class ProductSearchScreensTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val products = listOf(
        Product(id = 1, name = "갤럭시 S24", location = "A-1", quantity = 7),
        Product(id = 2, name = "아이폰 15", location = "B-2", quantity = 20),
        Product(id = 3, name = "갤럭시 탭", location = "C-3", quantity = 7),
    )

    // ---- InboundScreen ----

    @Test
    fun inboundSearchFieldIsAboveTheFirstProductItem() {
        composeTestRule.setContent { InboundScreen(products = products) }

        val searchTop = composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).fetchSemanticsNode().boundsInRoot.top
        val firstItemTop = composeTestRule.onNodeWithText("아이폰 15").fetchSemanticsNode().boundsInRoot.top

        assert(searchTop < firstItemTop) { "검색 필드는 목록 첫 항목보다 위에 있어야 합니다" }
    }

    @Test
    fun inboundListIsSortedByQuantityDescendingThenIdAscending() {
        composeTestRule.setContent { InboundScreen(products = products) }

        val iphoneTop = composeTestRule.onNodeWithText("아이폰 15").fetchSemanticsNode().boundsInRoot.top
        val galaxyS24Top = composeTestRule.onNodeWithText("갤럭시 S24").fetchSemanticsNode().boundsInRoot.top
        val galaxyTabTop = composeTestRule.onNodeWithText("갤럭시 탭").fetchSemanticsNode().boundsInRoot.top

        assert(iphoneTop < galaxyS24Top && galaxyS24Top < galaxyTabTop) {
            "수량 내림차순, 동률이면 id 오름차순으로 정렬되어야 합니다"
        }
    }

    @Test
    fun inboundSearchFiltersToMatchingProductOnly() {
        composeTestRule.setContent { InboundScreen(products = products) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")

        composeTestRule.onNodeWithText("아이폰 15").assertIsDisplayed()
        composeTestRule.onNodeWithText("갤럭시 S24").assertDoesNotExist()
        composeTestRule.onNodeWithText("갤럭시 탭").assertDoesNotExist()
    }

    @Test
    fun inboundSearchWithNoMatchesShowsEmptyState() {
        composeTestRule.setContent { InboundScreen(products = products) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("존재하지않는제품명")

        composeTestRule.onNodeWithTag(InboundEmptySearchResultTag).assertIsDisplayed()
    }

    @Test
    fun inboundClearButtonRestoresFullList() {
        composeTestRule.setContent { InboundScreen(products = products) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")
        composeTestRule.onNodeWithTag(ProductSearchBarClearButtonTag).performClick()

        composeTestRule.onNodeWithText("갤럭시 S24").assertIsDisplayed()
        composeTestRule.onNodeWithText("아이폰 15").assertIsDisplayed()
        composeTestRule.onNodeWithText("갤럭시 탭").assertIsDisplayed()
        composeTestRule.onNodeWithTag(ProductSearchBarClearButtonTag).assertDoesNotExist()
    }

    @Test
    fun inboundBackPressWhileSearchingClearsQueryAndRestoresFullList() {
        composeTestRule.setContent { InboundScreen(products = products) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")
        composeTestRule.onNodeWithText("갤럭시 S24").assertDoesNotExist()

        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(ProductSearchBarClearButtonTag).assertDoesNotExist()
        composeTestRule.onNodeWithText("갤럭시 S24").assertIsDisplayed()
    }

    @Test
    fun inboundExistingProductClickCallbackIsPreservedAfterSearch() {
        var clicked: Product? = null
        composeTestRule.setContent { InboundScreen(products = products, onProductClick = { clicked = it }) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")
        composeTestRule.onNodeWithText("아이폰 15").performClick()

        assert(clicked?.id == 2) { "검색 결과에서도 기존 onProductClick 콜백이 유지되어야 합니다" }
    }

    // ---- OutboundScreen ----

    @Test
    fun outboundSearchFieldIsAboveTheFirstProductItem() {
        composeTestRule.setContent { OutboundScreen(products = products, onOutboundClick = {}) }

        val searchTop = composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).fetchSemanticsNode().boundsInRoot.top
        val firstItemTop = composeTestRule.onNodeWithText("아이폰 15").fetchSemanticsNode().boundsInRoot.top

        assert(searchTop < firstItemTop) { "검색 필드는 목록 첫 항목보다 위에 있어야 합니다" }
    }

    @Test
    fun outboundSearchFiltersToMatchingProductOnly() {
        composeTestRule.setContent { OutboundScreen(products = products, onOutboundClick = {}) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("ㄱㄹ")

        composeTestRule.onNodeWithText("갤럭시 S24").assertIsDisplayed()
        composeTestRule.onNodeWithText("갤럭시 탭").assertIsDisplayed()
        composeTestRule.onNodeWithText("아이폰 15").assertDoesNotExist()
    }

    @Test
    fun outboundSearchWithNoMatchesShowsEmptyState() {
        composeTestRule.setContent { OutboundScreen(products = products, onOutboundClick = {}) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("존재하지않는제품명")

        composeTestRule.onNodeWithTag(OutboundEmptySearchResultTag).assertIsDisplayed()
    }

    @Test
    fun outboundExistingOutboundClickCallbackIsPreservedAfterSearch() {
        var clicked: Product? = null
        composeTestRule.setContent { OutboundScreen(products = products, onOutboundClick = { clicked = it }) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")
        composeTestRule.onNodeWithText("아이폰 15").performClick()

        assert(clicked?.id == 2) { "검색 결과에서도 기존 onOutboundClick 콜백이 유지되어야 합니다" }
    }

    // ---- StatusScreen ----

    @Test
    fun statusSearchFieldIsBetweenSummaryAndTitle() {
        composeTestRule.setContent { StatusScreen(products = products, onDeleteRequest = {}) }

        val summaryTop = composeTestRule.onNodeWithText("총 제품 종류").fetchSemanticsNode().boundsInRoot.top
        val searchTop = composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).fetchSemanticsNode().boundsInRoot.top
        val titleTop = composeTestRule.onNodeWithText("전체 제품 현황").fetchSemanticsNode().boundsInRoot.top

        assert(summaryTop < searchTop && searchTop < titleTop) {
            "검색 필드는 요약 카드와 '전체 제품 현황' 제목 사이에 있어야 합니다"
        }
    }

    @Test
    fun statusSummaryTotalsStayBasedOnFullListWhileSearching() {
        composeTestRule.setContent { StatusScreen(products = products, onDeleteRequest = {}) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")

        composeTestRule.onNodeWithText("3개").assertIsDisplayed() // 총 제품 종류: 검색과 무관하게 전체 3종
        composeTestRule.onNodeWithText("34개").assertIsDisplayed() // 총 재고 수량: 7 + 20 + 7
    }

    @Test
    fun statusSearchFiltersListWithoutAffectingSummary() {
        composeTestRule.setContent { StatusScreen(products = products, onDeleteRequest = {}) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")

        composeTestRule.onNodeWithText("아이폰 15").assertIsDisplayed()
        composeTestRule.onNodeWithText("갤럭시 S24").assertDoesNotExist()
    }

    @Test
    fun statusSearchWithNoMatchesShowsEmptyState() {
        composeTestRule.setContent { StatusScreen(products = products, onDeleteRequest = {}) }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("존재하지않는제품명")

        composeTestRule.onNodeWithTag(StatusEmptySearchResultTag).assertIsDisplayed()
    }

    @Test
    fun statusExistingLongPressDeleteFlowIsPreservedAfterSearch() {
        var deleteRequested: Product? = null
        composeTestRule.setContent {
            StatusScreen(products = products, onDeleteRequest = { deleteRequested = it })
        }

        composeTestRule.onNodeWithTag(ProductSearchBarFieldTag).performTextInput("아이폰")
        composeTestRule.onNodeWithText("아이폰 15").performTouchInput { longClick() }

        composeTestRule.onNodeWithText("삭제").assertIsDisplayed()
        composeTestRule.onNodeWithText("삭제").performClick()

        assert(deleteRequested?.id == 2) { "검색 결과에서도 기존 길게 누르기 삭제 흐름이 유지되어야 합니다" }
    }
}
