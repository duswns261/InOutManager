package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class ProductThumbnailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nullImagePathShowsFixedSizePlaceholderWithDistinctContentDescription() {
        composeTestRule.setContent {
            ProductThumbnail(imagePath = null, productName = "테스트 제품")
        }

        composeTestRule
            .onNodeWithContentDescription("제품 사진 없음")
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(64.dp)
    }

    @Test
    fun nonexistentImagePathFallsBackToPlaceholderWithoutCrashing() {
        composeTestRule.setContent {
            ProductThumbnail(imagePath = "/no/such/file.jpg", productName = "테스트 제품")
        }

        // Coil의 비동기 로딩/실패 전환은 Compose 테스트 클록에 묶여있지 않아, 최종 상태가
        // 반영될 때까지 polling한다. loading과 error 상태 모두 같은 placeholder를 그리므로
        // 이 대기는 두 상태 중 어느 쪽에서도 통과하며, 핵심은 crash 없이 같은 크기의
        // placeholder가 유지된다는 점이다.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithContentDescription("제품 사진 없음")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithContentDescription("제품 사진 없음")
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(64.dp)
    }
}
