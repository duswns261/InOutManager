package com.cret.inoutmanager.presentation.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.InventoryRouteMenuItems
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
        SummaryCard(title = "총 제품 종류", value = "15개", modifier = Modifier.width(160.dp))
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

/**
 * 화면 전환 메뉴의 열린 상태. 실제 `DropdownMenu`가 사용하는 것과 같은 `surface` 표면 위에 실제 메뉴 항목
 * [InventoryRouteMenuItems]을 그려, 메뉴 배경·항목 텍스트·선택 체크의 대비를 라이트·다크 두 모드에서
 * 기록한다(현재 다크 모드 결함 포함 기준선). `DropdownMenu`의 `Popup`은 열림 애니메이션 때문에 Preview
 * 0프레임에서 항목이 보이지 않으므로 이렇게 표면 위에 직접 그린다.
 */
@PreviewTest
@LightDarkPreviews
@Composable
private fun RouteSwitchMenuOpenScreenshot() {
    InOutManagerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(4.dp),
            shadowElevation = 3.dp,
        ) {
            Column(modifier = Modifier.width(200.dp).padding(vertical = 8.dp)) {
                InventoryRouteMenuItems(
                    currentRoute = InventoryRoute.Inbound.route,
                    onRouteSelected = {},
                    onItemSelected = {},
                )
            }
        }
    }
}
