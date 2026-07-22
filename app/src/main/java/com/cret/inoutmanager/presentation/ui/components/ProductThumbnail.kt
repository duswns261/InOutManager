package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cret.inoutmanager.R
import com.cret.inoutmanager.ui.theme.BrandAccent

private val ThumbnailSize = 64.dp
private val ThumbnailCornerRadius = 8.dp
private const val PlaceholderIconSizeRatio = 0.5f
private val PlaceholderIconMaxSize = 56.dp
private val PlaceholderBackground = lerp(BrandAccent, Color.White, 0.35f)
private val PlaceholderIconTint = Color(0xFFFFFFFF)

/** 테스트가 Coil 요청의 최종 상태(성공/실패)에 도달했는지 결정적으로 기다릴 수 있게 노출하는 테스트 전용 tag. */
internal const val ProductThumbnailSettledTag = "product-thumbnail-settled"
private const val ProductThumbnailPendingTag = "product-thumbnail-pending"

/** 박스 아이콘 placeholder 존재를 검증하기 위한 테스트 전용 tag. 접근성 tree에는 영향을 주지 않는다. */
internal const val ProductThumbnailPlaceholderIconTag = "product-thumbnail-placeholder-icon"

/**
 * 입고·출고·재고 현황 카드가 공통으로 쓰는 정사각형 제품 썸네일입니다.
 * 이미지 없음/로딩 중/로딩 실패 상태 모두 같은 크기의 placeholder를 유지해
 * 비동기 로딩 전후로 카드 높이나 텍스트 위치가 흔들리지 않게 합니다.
 * 클릭 동작은 두지 않으며, 부모 카드의 클릭·롱클릭이 이 영역 위에서도 그대로 전달됩니다.
 *
 * 목록(LazyColumn) 각 행에서 호출되므로 subcomposition 비용이 있는 `SubcomposeAsyncImage` 대신
 * 일반 `AsyncImage`를 쓴다. placeholder를 항상 배경으로 먼저 그리고, 이미지가 성공적으로
 * 로드됐을 때만 그 위를 덮어 loading/error 상태에서는 placeholder가 그대로 드러나게 한다.
 *
 * `size`/`cornerRadius`는 목록 64dp 호출부의 기존 동작을 바꾸지 않는 기본값을 가지며,
 * 제품 등록·요약 화면처럼 더 큰 이미지 영역에서 같은 placeholder를 재사용할 때만 지정한다.
 *
 * `imagePath`는 Coil의 `AsyncImage`가 그대로 받아들이는 model이라면 영구 저장 경로 `String`뿐
 * 아니라 등록 중인 미확정 선택을 나타내는 `File`도 받을 수 있어, 신규 등록·이미지 선택 UI가
 * 같은 placeholder/로딩 상태 경계를 재사용할 수 있습니다.
 */
@Composable
fun ProductThumbnail(
    imagePath: Any?,
    productName: String,
    modifier: Modifier = Modifier,
    size: Dp = ThumbnailSize,
    cornerRadius: Dp = ThumbnailCornerRadius,
) {
    val hasImage = when (imagePath) {
        null -> false
        is String -> imagePath.isNotBlank()
        else -> true
    }
    var isImageLoaded by remember(imagePath) { mutableStateOf(false) }
    var hasSettled by remember(imagePath) { mutableStateOf(!hasImage) }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .semantics {
                contentDescription = if (isImageLoaded) "$productName 제품 사진" else "제품 사진 없음"
            }
            .testTag(if (hasSettled) ProductThumbnailSettledTag else ProductThumbnailPendingTag),
    ) {
        ThumbnailPlaceholder(size = size)

        if (hasImage) {
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
private fun ThumbnailPlaceholder(size: Dp) {
    val iconSize = minOf(size * PlaceholderIconSizeRatio, PlaceholderIconMaxSize)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaceholderBackground),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_inventory_2_24),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .testTag(ProductThumbnailPlaceholderIconTag),
            tint = PlaceholderIconTint,
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

@Preview(showBackground = true)
@Composable
private fun ProductThumbnailLargePlaceholderPreview() {
    ProductThumbnail(
        imagePath = null,
        productName = "프리뷰 제품",
        size = 160.dp,
        cornerRadius = 16.dp,
    )
}
