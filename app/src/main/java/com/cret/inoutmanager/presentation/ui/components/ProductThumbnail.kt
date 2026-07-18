package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandChipBackground

private val ThumbnailSize = 64.dp
private val ThumbnailCornerRadius = 8.dp

/** 테스트가 Coil 요청의 최종 상태(성공/실패)에 도달했는지 결정적으로 기다릴 수 있게 노출하는 테스트 전용 tag. */
internal const val ProductThumbnailSettledTag = "product-thumbnail-settled"
private const val ProductThumbnailPendingTag = "product-thumbnail-pending"

/**
 * 입고·출고·재고 현황 카드가 공통으로 쓰는 64dp 정사각형 제품 썸네일입니다.
 * 이미지 없음/로딩 중/로딩 실패 상태 모두 같은 크기의 placeholder를 유지해
 * 비동기 로딩 전후로 카드 높이나 텍스트 위치가 흔들리지 않게 합니다.
 * 클릭 동작은 두지 않으며, 부모 카드의 클릭·롱클릭이 이 영역 위에서도 그대로 전달됩니다.
 *
 * 목록(LazyColumn) 각 행에서 호출되므로 subcomposition 비용이 있는 `SubcomposeAsyncImage` 대신
 * 일반 `AsyncImage`를 쓴다. placeholder를 항상 배경으로 먼저 그리고, 이미지가 성공적으로
 * 로드됐을 때만 그 위를 덮어 loading/error 상태에서는 placeholder가 그대로 드러나게 한다.
 */
@Composable
fun ProductThumbnail(
    imagePath: String?,
    productName: String,
    modifier: Modifier = Modifier,
) {
    var isImageLoaded by remember(imagePath) { mutableStateOf(false) }
    var hasSettled by remember(imagePath) { mutableStateOf(imagePath.isNullOrBlank()) }

    Box(
        modifier = modifier
            .size(ThumbnailSize)
            .clip(RoundedCornerShape(ThumbnailCornerRadius))
            .semantics {
                contentDescription = if (isImageLoaded) "$productName 제품 사진" else "제품 사진 없음"
            }
            .testTag(if (hasSettled) ProductThumbnailSettledTag else ProductThumbnailPendingTag),
    ) {
        ThumbnailPlaceholder(productName = productName)

        if (!imagePath.isNullOrBlank()) {
            AsyncImage(
                model = imagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    isImageLoaded = true
                    hasSettled = true
                },
                onError = {
                    isImageLoaded = false
                    hasSettled = true
                },
            )
        }
    }
}

@Composable
private fun ThumbnailPlaceholder(productName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandChipBackground),
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
