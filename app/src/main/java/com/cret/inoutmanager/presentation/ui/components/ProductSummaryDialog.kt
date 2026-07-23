package com.cret.inoutmanager.presentation.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandSurface
import java.io.File
import java.io.InputStream

private val SummaryImageSize = 200.dp

/**
 * 입고 목록에서 제품 카드를 눌렀을 때 여는 요약 modal입니다.
 * 전체 제품명 → 큰 이미지 → 위치 → 현재 수량 순으로 표시하고,
 * 이미지 유무와 관계없이 [ProductImageSelection]의 + 버튼으로 추가·교체·제거를 제공합니다.
 *
 * [attachImage]로 새 이미지 commit과 [Product] update를 위임하며, [removeImage]로 기존 이미지를
 * 독립적으로 제거할 수 있습니다. 저장이 끝날 때까지 이미지 선택과 dismiss(닫기 버튼/바깥 탭/
 * 뒤로가기)를 잠급니다. 저장이 성공하면 [product]가 최신 `imagePath`로 다시 전달된다고 가정하고
 * 별도 지역 상태로 이미지 경로를 들고 있지 않습니다.
 */
@Composable
fun ProductSummaryDialog(
    product: Product,
    onDismiss: () -> Unit,
    attachImage: (temporaryImageFile: File, origin: ProductImageOrigin, onResult: (Boolean) -> Unit) -> Unit,
    removeImage: (onResult: (Boolean) -> Unit) -> Unit,
    createTemporaryImageFile: () -> File,
    discardTemporaryImage: (File) -> Unit,
    importImage: (openStream: () -> InputStream, onResult: (Result<File>) -> Unit) -> Unit,
    onCameraOpened: () -> Unit = {},
    onCameraCaptureCompleted: () -> Unit = {},
    onCameraCaptureFailed: (CameraCaptureFailure) -> Unit = {},
    onCameraPermissionDenied: () -> Unit = {},
) {
    var isSaving by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun dismissIfIdle() {
        if (!isSaving && !isImporting) onDismiss()
    }

    fun handleImageAcquired(file: File, origin: ProductImageOrigin) {
        isSaving = true
        attachImage(file, origin) { success ->
            isSaving = false
            if (!success) {
                Toast.makeText(context, "이미지 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleRemoveRequested() {
        isSaving = true
        removeImage { success ->
            isSaving = false
            if (!success) {
                Toast.makeText(context, "이미지 제거에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(onDismissRequest = ::dismissIfIdle) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            modifier = Modifier.heightIn(max = 640.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DialogHeader(title = product.name, onClose = ::dismissIfIdle, fontSize = 20.sp, expandable = true)
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProductImageSelection(
                        currentImage = product.imagePath,
                        productName = product.name,
                        onImageAcquired = ::handleImageAcquired,
                        createTemporaryImageFile = createTemporaryImageFile,
                        discardTemporaryImage = discardTemporaryImage,
                        importImage = importImage,
                        imageSize = SummaryImageSize,
                        enabled = !isSaving && !isImporting,
                        onRemoveRequested = ::handleRemoveRequested,
                        onImportStateChanged = { isImporting = it },
                        onCameraOpened = onCameraOpened,
                        onCameraCaptureCompleted = onCameraCaptureCompleted,
                        onCameraCaptureFailed = onCameraCaptureFailed,
                        onCameraPermissionDenied = onCameraPermissionDenied,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "위치: ${product.location}",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "현재 수량: ${product.quantity}개",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrandAccent,
                        textAlign = TextAlign.Center,
                    )

                    if (isSaving || isImporting) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "이미지 처리 중...", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductSummaryDialog() {
    ProductSummaryDialog(
        product = Product(id = 1, name = "프리뷰 제품", location = "A-1 창고", quantity = 42),
        onDismiss = {},
        attachImage = { _, _, onResult -> onResult(true) },
        removeImage = { onResult -> onResult(true) },
        createTemporaryImageFile = { File.createTempFile("preview", ".jpg") },
        discardTemporaryImage = {},
        importImage = { _, _ -> },
    )
}
