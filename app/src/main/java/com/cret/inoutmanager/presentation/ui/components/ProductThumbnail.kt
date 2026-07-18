package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import coil3.compose.SubcomposeAsyncImage
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandChipBackground

private val ThumbnailSize = 64.dp
private val ThumbnailCornerRadius = 8.dp

/**
 * 입고·출고·재고 현황 카드가 공통으로 쓰는 64dp 정사각형 제품 썸네일입니다.
 * 이미지 없음/로딩 중/로딩 실패 상태 모두 같은 크기의 placeholder를 유지해
 * 비동기 로딩 전후로 카드 높이나 텍스트 위치가 흔들리지 않게 합니다.
 * 클릭 동작은 두지 않으며, 부모 카드의 클릭·롱클릭이 이 영역 위에서도 그대로 전달됩니다.
 */
@Composable
fun ProductThumbnail(
    imagePath: String?,
    productName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ThumbnailSize)
            .clip(RoundedCornerShape(ThumbnailCornerRadius)),
    ) {
        if (imagePath.isNullOrBlank()) {
            ThumbnailPlaceholder(productName = productName)
        } else {
            SubcomposeAsyncImage(
                model = imagePath,
                contentDescription = "$productName 제품 사진",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { ThumbnailPlaceholder(productName = productName) },
                error = { ThumbnailPlaceholder(productName = productName) },
            )
        }
    }
}

@Composable
private fun ThumbnailPlaceholder(productName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandChipBackground)
            .semantics { contentDescription = "제품 사진 없음" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = productName.trim().firstOrNull()?.uppercase() ?: "?",
            color = BrandAccent,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductThumbnailWithImagePreview() {
    ProductThumbnail(imagePath = "/nonexistent/path.jpg", productName = "프리뷰 제품")
}

@Preview(showBackground = true)
@Composable
private fun ProductThumbnailPlaceholderPreview() {
    ProductThumbnail(imagePath = null, productName = "프리뷰 제품")
}
