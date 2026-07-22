package com.cret.inoutmanager.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
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

        composeTestRule
            .onNodeWithTag(ProductThumbnailPlaceholderIconTag)
            .assertExists()

        composeTestRule
            .onNodeWithText("테")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("?")
            .assertDoesNotExist()
    }

    @Test
    fun nonexistentImagePathFallsBackToPlaceholderAfterCoilRequestSettles() {
        composeTestRule.setContent {
            ProductThumbnail(imagePath = "/no/such/file.jpg", productName = "테스트 제품")
        }

        // Coil의 onSuccess/onError는 Compose 테스트 클록과 무관한 실제 코루틴에서 온다.
        // loading과 error가 같은 placeholder를 그리므로, placeholder의 존재만으로는
        // 요청이 실제로 끝났는지 증명하지 못한다(첫 composition에서도 참). 그래서
        // ProductThumbnail이 onSuccess/onError에서만 켜는 "settled" testTag가 뜰 때까지
        // 기다려, 존재하지 않는 파일의 요청이 실제로 State.Error에 도달한 뒤를 검증한다.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(ProductThumbnailSettledTag)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithContentDescription("제품 사진 없음")
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(64.dp)

        composeTestRule
            .onNodeWithTag(ProductThumbnailPlaceholderIconTag)
            .assertExists()
    }

    @Test
    fun successfulImageLoadSwitchesSemanticsFromPlaceholderToProductPhoto() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val imageFile = File(context.cacheDir, "product-thumbnail-test-${System.nanoTime()}.png")
        val bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.RED)
        }
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        try {
            composeTestRule.setContent {
                ProductThumbnail(imagePath = imageFile.absolutePath, productName = "테스트 제품")
            }

            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule
                    .onAllNodesWithTag(ProductThumbnailSettledTag)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeTestRule
                .onNodeWithContentDescription("테스트 제품 제품 사진")
                .assertWidthIsEqualTo(64.dp)
                .assertHeightIsEqualTo(64.dp)
        } finally {
            imageFile.delete()
        }
    }
}
