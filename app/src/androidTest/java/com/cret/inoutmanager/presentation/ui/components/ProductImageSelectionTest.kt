package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import java.io.File
import org.junit.Rule
import org.junit.Test

class ProductImageSelectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        currentImage: Any? = null,
        onImageAcquired: (File, ProductImageOrigin) -> Unit = { _, _ -> },
        onRemoveRequested: (() -> Unit)? = null,
        enabled: Boolean = true,
    ) {
        composeTestRule.setContent {
            ProductImageSelection(
                currentImage = currentImage,
                productName = "테스트 제품",
                onImageAcquired = onImageAcquired,
                createTemporaryImageFile = { File.createTempFile("test", ".jpg") },
                discardTemporaryImage = {},
                importImage = { _, _ -> },
                enabled = enabled,
                onRemoveRequested = onRemoveRequested,
            )
        }
    }

    @Test
    fun noImageShowsAddContentDescriptionOnButton() {
        setContent(currentImage = null)

        composeTestRule
            .onNodeWithContentDescription("제품 이미지 추가")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("제품 이미지 변경")
            .assertDoesNotExist()
    }

    @Test
    fun existingImageShowsChangeContentDescriptionOnButton() {
        setContent(currentImage = "/existing/path.jpg")

        composeTestRule
            .onNodeWithContentDescription("제품 이미지 변경")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("제품 이미지 추가")
            .assertDoesNotExist()
    }

    @Test
    fun tappingButtonOpensSourceMenuWithCameraPickerAndCancel() {
        setContent(currentImage = null)

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionCameraOptionTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ProductImageSelectionPickerOptionTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ProductImageSelectionCancelOptionTag).assertIsDisplayed()
    }

    @Test
    fun cancelOptionDismissesMenuWithoutAcquiringImage() {
        var acquired = false
        setContent(currentImage = null, onImageAcquired = { _, _ -> acquired = true })

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()
        composeTestRule.onNodeWithTag(ProductImageSelectionCancelOptionTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionCameraOptionTag).assertDoesNotExist()
        assert(!acquired) { "취소를 눌렀을 때 이미지가 확정되면 안 됩니다" }
    }

    @Test
    fun removeOptionOnlyAppearsWhenCallbackProvidedAndImageExists() {
        setContent(currentImage = "/existing/path.jpg", onRemoveRequested = {})

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionRemoveOptionTag).assertIsDisplayed()
    }

    @Test
    fun removeOptionIsAbsentWithoutImageEvenIfCallbackProvided() {
        setContent(currentImage = null, onRemoveRequested = {})

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionRemoveOptionTag).assertDoesNotExist()
    }

    @Test
    fun removeOptionIsAbsentWhenCallbackNotProvided() {
        setContent(currentImage = "/existing/path.jpg", onRemoveRequested = null)

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionRemoveOptionTag).assertDoesNotExist()
    }

    @Test
    fun tappingRemoveOptionInvokesCallbackAndClosesMenu() {
        var removed = false
        setContent(currentImage = "/existing/path.jpg", onRemoveRequested = { removed = true })

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()
        composeTestRule.onNodeWithTag(ProductImageSelectionRemoveOptionTag).performClick()

        assert(removed) { "사진 제거를 눌렀을 때 onRemoveRequested가 호출되어야 합니다" }
        composeTestRule.onNodeWithTag(ProductImageSelectionCameraOptionTag).assertDoesNotExist()
    }
}
