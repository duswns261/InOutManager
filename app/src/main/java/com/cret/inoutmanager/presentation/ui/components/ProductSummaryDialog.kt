package com.cret.inoutmanager.presentation.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * 큰 이미지 → 전체 제품명 → 전체 위치 → 현재 수량 순으로 표시하고,
 * 이미지 유무와 관계없이 [ProductImageSelection]의 + 버튼으로 최초 추가 또는 교체를 제공합니다.
 * 독립적인 이미지 삭제 명령은 제공하지 않습니다.
 *
 * [attachImage]로 새 이미지 commit과 [Product] update를 위임하며, 저장이 끝날 때까지 이미지
 * 선택과 dismiss(닫기 버튼/바깥 탭/뒤로가기)를 잠급니다. 저장이 성공하면 [product]가 최신
 * `imagePath`로 다시 전달된다고 가정하고 별도 지역 상태로 이미지 경로를 들고 있지 않습니다.
 */
@Composable
fun ProductSummaryDialog(
    product: Product,
    onDismiss: () -> Unit,
    attachImage: (temporaryImageFile: File, origin: ProductImageOrigin, onResult: (Boolean) -> Unit) -> Unit,
    createTemporaryImageFile: () -> File,
    discardTemporaryImage: (File) -> Unit,
    importImage: (openStream: () -> InputStream, onResult: (Result<File>) -> Unit) -> Unit,
    onCameraOpened: () -> Unit = {},
    onCameraCaptureCompleted: () -> Unit = {},
    onCameraCaptureFailed: (CameraCaptureFailure) -> Unit = {},
    onCameraPermissionDenied: () -> Unit = {},
) {
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun dismissIfIdle() {
        if (!isSaving) onDismiss()
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

    Dialog(onDismissRequest = ::dismissIfIdle) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            modifier = Modifier.heightIn(max = 640.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = ::dismissIfIdle) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                Column(
                    modifier = Modifier
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
                        enabled = !isSaving,
                        onCameraOpened = onCameraOpened,
                        onCameraCaptureCompleted = onCameraCaptureCompleted,
                        onCameraCaptureFailed = onCameraCaptureFailed,
                        onCameraPermissionDenied = onCameraPermissionDenied,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.location,
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

                    if (isSaving) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "이미지 저장 중...", fontSize = 12.sp)
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
        createTemporaryImageFile = { File.createTempFile("preview", ".jpg") },
        discardTemporaryImage = {},
        importImage = { _, _ -> },
    )
}
