package com.cret.inoutmanager.presentation.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import java.io.File
import java.io.InputStream
import org.junit.Rule
import org.junit.Test

class ProductSummaryDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val longProductName = "아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 긴 제품 이름입니다 정말로 깁니다"

    private fun setContent(
        productName: String,
        onDismiss: () -> Unit = {},
        attachImage: (temporaryImageFile: File, origin: ProductImageOrigin, onResult: (Boolean) -> Unit) -> Unit = { _, _, onResult -> onResult(true) },
        removeImage: (onResult: (Boolean) -> Unit) -> Unit = { onResult -> onResult(true) },
    ) {
        composeTestRule.setContent {
            ProductSummaryDialog(
                product = Product(id = 1, name = productName, location = "A-1", quantity = 5),
                onDismiss = onDismiss,
                attachImage = attachImage,
                removeImage = removeImage,
                createTemporaryImageFile = { File.createTempFile("test", ".jpg") },
                discardTemporaryImage = {},
                importImage = { _: () -> InputStream, _: (Result<File>) -> Unit -> },
            )
        }
    }

    @Test
    fun shortNameShowsNoExpandEntryPoint() {
        setContent(productName = "펜")

        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertDoesNotExist()
    }

    @Test
    fun longNameCanBeExpandedToShowTheFullText() {
        setContent(productName = longProductName)

        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertDoesNotExist()

        composeTestRule.onNodeWithTag(DialogHeaderNameRowTag).performClick()

        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertIsDisplayed()
    }

    @Test
    fun tappingTheHeaderAgainCollapsesTheExpandedName() {
        setContent(productName = longProductName)

        composeTestRule.onNodeWithTag(DialogHeaderNameRowTag).performClick()
        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertIsDisplayed()

        composeTestRule.onNodeWithTag(DialogHeaderNameRowTag).performClick()

        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertDoesNotExist()
    }

    @Test
    fun backPressWhileExpandedCollapsesWithoutDismissingTheDialog() {
        var dismissed = false
        setContent(productName = longProductName, onDismiss = { dismissed = true })

        composeTestRule.onNodeWithTag(DialogHeaderNameRowTag).performClick()
        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertIsDisplayed()

        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(DialogHeaderExpandedNameTag).assertDoesNotExist()
        assert(!dismissed) { "펼친 상태에서 뒤로가기는 이름만 접어야 하며 요약 다이얼로그를 닫으면 안 됩니다" }
    }
}
