package com.cret.inoutmanager.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import java.io.File
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProductImageSelectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /** `PickVisualMedia` launcher가 항상 같은 [uri]를 즉시 돌려주게 만드는 테스트 전용 registry입니다. */
    private class ImmediateResultRegistry(private val uri: Uri) : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?,
        ) {
            @Suppress("UNCHECKED_CAST")
            dispatchResult(requestCode, uri as O)
        }
    }

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
    fun tappingButtonOpensSourceMenuWithAlbumAndCameraOptions() {
        setContent(currentImage = null)

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()

        composeTestRule.onNodeWithTag(ProductImageSelectionPickerOptionTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ProductImageSelectionCameraOptionTag).assertIsDisplayed()
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

    @Test
    fun pickerImportInProgressReportsBusyAndDisablesTheButton() {
        var pendingOnResult: ((Result<File>) -> Unit)? = null
        var reportedBusy = false

        composeTestRule.setContent {
            val registryOwner = remember {
                object : ActivityResultRegistryOwner {
                    override val activityResultRegistry = ImmediateResultRegistry(Uri.parse("content://test/1"))
                }
            }
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                ProductImageSelection(
                    currentImage = null,
                    productName = "테스트 제품",
                    onImageAcquired = { _, _ -> },
                    createTemporaryImageFile = { File.createTempFile("test", ".jpg") },
                    discardTemporaryImage = {},
                    importImage = { _, onResult -> pendingOnResult = onResult },
                    onImportStateChanged = { reportedBusy = it },
                )
            }
        }

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()
        composeTestRule.onNodeWithTag(ProductImageSelectionPickerOptionTag).performClick()

        assertTrue("import 시작 시 onImportStateChanged(true)가 호출되어야 합니다", reportedBusy)
        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).assertIsNotEnabled()
    }

    @Test
    fun lateImportSuccessAfterDisposeDiscardsTheFileInsteadOfAcquiringIt() {
        var pendingOnResult: ((Result<File>) -> Unit)? = null
        var acquired = false
        val discarded = mutableListOf<File>()
        val showSelection = mutableStateOf(true)

        composeTestRule.setContent {
            val registryOwner = remember {
                object : ActivityResultRegistryOwner {
                    override val activityResultRegistry = ImmediateResultRegistry(Uri.parse("content://test/1"))
                }
            }
            val visible by showSelection
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                if (visible) {
                    ProductImageSelection(
                        currentImage = null,
                        productName = "테스트 제품",
                        onImageAcquired = { _, _ -> acquired = true },
                        createTemporaryImageFile = { File.createTempFile("test", ".jpg") },
                        discardTemporaryImage = { discarded += it },
                        importImage = { _, onResult -> pendingOnResult = onResult },
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(ProductImageSelectionButtonTag).performClick()
        composeTestRule.onNodeWithTag(ProductImageSelectionPickerOptionTag).performClick()
        composeTestRule.waitForIdle()

        // 다이얼로그가 이미 닫힌 상황을 재현하기 위해 import가 끝나기 전에 composable을 폐기합니다.
        showSelection.value = false
        composeTestRule.waitForIdle()

        val importedFile = File.createTempFile("late-import", ".jpg")
        pendingOnResult?.invoke(Result.success(importedFile))
        composeTestRule.waitForIdle()

        assertTrue("폐기된 뒤에는 onImageAcquired가 호출되면 안 됩니다", !acquired)
        assertEquals(listOf(importedFile), discarded)
    }
}
