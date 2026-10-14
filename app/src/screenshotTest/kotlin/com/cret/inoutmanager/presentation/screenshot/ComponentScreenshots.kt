package com.cret.inoutmanager.presentation.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.InventoryTopAppBar
import com.cret.inoutmanager.presentation.ui.components.ProductCard
import com.cret.inoutmanager.presentation.ui.components.ProductThumbnail
import com.cret.inoutmanager.presentation.ui.components.SummaryCard
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import com.cret.inoutmanager.ui.theme.InOutManagerTheme

@PreviewTest
@LightDarkPreviews
@Composable
private fun InventoryTopAppBarInboundScreenshot() {
    InOutManagerTheme {
        InventoryTopAppBar(
            currentRoute = InventoryRoute.Inbound.route,
            onRouteSelected = {},
            onAddProductClick = {},
        )
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun InventoryTopAppBarOutboundScreenshot() {
    InOutManagerTheme {
        InventoryTopAppBar(
            currentRoute = InventoryRoute.Outbound.route,
            onRouteSelected = {},
            onAddProductClick = {},
        )
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun InventoryTopAppBarStatusScreenshot() {
    InOutManagerTheme {
        InventoryTopAppBar(
            currentRoute = InventoryRoute.Status.route,
            onRouteSelected = {},
            onAddProductClick = {},
        )
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun ProductCardScreenshot() {
    InOutManagerTheme {
        ProductCard(product = Product(1, "프리뷰 제품 1", "A-1 창고", 82))
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun SummaryCardScreenshot() {
    InOutManagerTheme {
        SummaryCard(title = "총 제품 종류", value = "15개")
    }
}

/** 이미지 로드 실패 경로. 실제 파일을 두지 않아 placeholder가 그대로 드러나는 상태를 기록한다. */
@PreviewTest
@LightDarkPreviews
@Composable
private fun ProductThumbnailWithImageScreenshot() {
    InOutManagerTheme {
        ProductThumbnail(imagePath = "/nonexistent/path.jpg", productName = "프리뷰 제품")
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun ProductThumbnailPlaceholderScreenshot() {
    InOutManagerTheme {
        ProductThumbnail(imagePath = null, productName = "프리뷰 제품")
    }
}

@PreviewTest
@LightDarkPreviews
@Composable
private fun ProductThumbnailLargePlaceholderScreenshot() {
    InOutManagerTheme {
        ProductThumbnail(
            imagePath = null,
            productName = "프리뷰 제품",
            size = 160.dp,
            cornerRadius = 16.dp,
        )
    }
}
