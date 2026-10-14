package com.cret.inoutmanager.presentation.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.screens.HomeScreen
import com.cret.inoutmanager.presentation.ui.screens.InboundScreen
import com.cret.inoutmanager.presentation.ui.screens.OutboundScreen
import com.cret.inoutmanager.presentation.ui.screens.StatusScreen
import com.cret.inoutmanager.ui.theme.InOutManagerTheme

private val InboundSampleProducts = listOf(
    Product(1, "프리뷰 제품 1", "A-1 창고", 50),
    Product(2, "프리뷰 제품 2", "B-2 창고", 10),
    Product(3, "프리뷰 제품 3", "C-3 창고", 0),
)

private val OutboundSampleProducts = listOf(
    Product(1, "아이폰 15", "A-1 구역", 25),
    Product(2, "갤럭시 S24", "B-3 구역", 12),
)

private val StatusSampleProducts = listOf(
    Product(1, "갤럭시 S24", "창고 A", 100),
    Product(2, "아이폰 15", "창고 B", 50),
)

@PreviewTest
@ScreenPreviews
@Composable
private fun HomeScreenScreenshot() {
    InOutManagerTheme {
        HomeScreen(onNavigate = {})
    }
}

/** 메뉴 그룹이 viewport보다 커져 스크롤로 전환되는 경계(Issue #34)를 기준선으로 유지한다. */
@PreviewTest
@Preview(name = "SmallHeight", showBackground = true, widthDp = 360, heightDp = 320)
@Composable
private fun HomeScreenSmallHeightScreenshot() {
    InOutManagerTheme {
        HomeScreen(onNavigate = {})
    }
}

@PreviewTest
@ScreenPreviews
@Composable
private fun InboundScreenScreenshot() {
    InOutManagerTheme {
        InboundScreen(products = InboundSampleProducts)
    }
}

@PreviewTest
@ScreenPreviews
@Composable
private fun OutboundScreenScreenshot() {
    InOutManagerTheme {
        OutboundScreen(products = OutboundSampleProducts, onOutboundClick = {})
    }
}

@PreviewTest
@ScreenPreviews
@Composable
private fun StatusScreenScreenshot() {
    InOutManagerTheme {
        StatusScreen(products = StatusSampleProducts, onDeleteRequest = {})
    }
}
