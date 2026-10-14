package com.cret.inoutmanager.presentation.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.LocalRenderDialogContentInline
import com.cret.inoutmanager.presentation.ui.components.NewProductDialog
import com.cret.inoutmanager.presentation.ui.components.OutboundQuantityDialog
import com.cret.inoutmanager.presentation.ui.components.ProductSummaryDialog
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import java.io.File

/**
 * Compose의 `Dialog`는 별도 윈도우에 렌더링돼 Preview surface에 캡처되지 않는다.
 * 스크린샷에서만 내용을 인라인으로 그려 기준 이미지를 남긴다(scrim과 중앙 배치는 담기지 않는다).
 */
@Composable
private fun DialogContentScreenshot(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalRenderDialogContentInline provides true) {
        InOutManagerTheme { content() }
    }
}

// 정적 렌더링에서는 호출되지 않는 콜백이지만, 실제 파일을 만들지 않도록 생성 없이 경로만 가진 File을 쓴다.
private val PreviewImageFile = File("preview-placeholder.jpg")

@PreviewTest
@DialogPreviews
@Composable
private fun NewProductDialogScreenshot() {
    DialogContentScreenshot {
        NewProductDialog(
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _ -> },
            createTemporaryImageFile = { PreviewImageFile },
            discardTemporaryImage = {},
            importImage = { _, _ -> },
        )
    }
}

@PreviewTest
@DialogPreviews
@Composable
private fun ProductSummaryDialogScreenshot() {
    DialogContentScreenshot {
        ProductSummaryDialog(
            product = Product(id = 1, name = "프리뷰 제품", location = "A-1 창고", quantity = 42),
            onDismiss = {},
            attachImage = { _, _, onResult -> onResult(true) },
            removeImage = { onResult -> onResult(true) },
            createTemporaryImageFile = { PreviewImageFile },
            discardTemporaryImage = {},
            importImage = { _, _ -> },
        )
    }
}

@PreviewTest
@DialogPreviews
@Composable
private fun OutboundQuantityDialogScreenshot() {
    DialogContentScreenshot {
        OutboundQuantityDialog(
            productName = "테스트 제품",
            currentQty = 50,
            onDismiss = {},
            onNext = {},
        )
    }
}
