package com.cret.inoutmanager.presentation.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandSurface
import java.io.File

/** [ProductCameraDialog] 촬영 실패의 원인입니다. 권한 처리는 호출부 책임이라 포함하지 않습니다. */
enum class CameraCaptureFailure {
    CAPTURE_ERROR,
    CAMERA_UNAVAILABLE,
}

private enum class CameraDialogStep { PREVIEW, REVIEW }

/**
 * 제품 등록용 후면 카메라 Preview/촬영/재촬영 흐름을 제공하는 full-screen dialog입니다.
 * CAMERA 권한이 이미 허용된 상태에서만 호출한다고 가정하며, 권한 요청/거부 처리는 호출부의 책임입니다.
 * 취소, 재촬영, dialog dismiss 시 관리 대상 임시 파일을 [discardTemporaryFile]로 정리합니다.
 */
@Composable
fun ProductCameraDialog(
    createTemporaryFile: () -> File,
    discardTemporaryFile: (File) -> Unit,
    onPhotoConfirmed: (File) -> Unit,
    onDismiss: () -> Unit,
    onCaptureCompleted: () -> Unit = {},
    onCaptureFailed: (CameraCaptureFailure) -> Unit = {},
) {
    var step by remember { mutableStateOf(CameraDialogStep.PREVIEW) }
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var pendingOutputFile by remember { mutableStateOf<File?>(null) }

    fun dismissAndCleanUp() {
        pendingOutputFile?.let(discardTemporaryFile)
        capturedFile?.let(discardTemporaryFile)
        onDismiss()
    }

    Dialog(
        onDismissRequest = ::dismissAndCleanUp,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            when (step) {
                CameraDialogStep.PREVIEW -> CameraPreviewContent(
                    onClose = ::dismissAndCleanUp,
                    onImageCaptured = { file ->
                        capturedFile = file
                        pendingOutputFile = null
                        step = CameraDialogStep.REVIEW
                    },
                    onImageCaptureFailed = { file ->
                        discardTemporaryFile(file)
                        pendingOutputFile = null
                        onCaptureFailed(CameraCaptureFailure.CAPTURE_ERROR)
                    },
                    onCameraUnavailable = {
                        onCaptureFailed(CameraCaptureFailure.CAMERA_UNAVAILABLE)
                        dismissAndCleanUp()
                    },
                    prepareOutputFile = {
                        val file = createTemporaryFile()
                        pendingOutputFile = file
                        file
                    },
                    discardOutputFile = discardTemporaryFile,
                )

                CameraDialogStep.REVIEW -> capturedFile?.let { file ->
                    CameraReviewContent(
                        file = file,
                        onRetake = {
                            discardTemporaryFile(file)
                            capturedFile = null
                            step = CameraDialogStep.PREVIEW
                        },
                        onConfirm = {
                            onCaptureCompleted()
                            onPhotoConfirmed(file)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    onClose: () -> Unit,
    onImageCaptured: (File) -> Unit,
    onImageCaptureFailed: (File) -> Unit,
    onCameraUnavailable: () -> Unit,
    prepareOutputFile: () -> File,
    discardOutputFile: (File) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraReady by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    // Preview와 같은 ViewPort로 bind된, 현재 촬영에 쓸 수 있는 ImageCapture입니다.
    // rebind가 일어나면 이전 인스턴스 대신 새로 bind된 인스턴스로 교체됩니다.
    var boundImageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    // 마지막으로 bind에 사용한 PreviewView 크기/rotation입니다. 값이 바뀌면 오래된 ViewPort로
    // 촬영하지 않도록 다시 bind합니다.
    var lastBoundKey by remember { mutableStateOf<CameraBindKey?>(null) }
    // Compose가 이 컴포저블을 폐기(dismiss/재촬영 전환)한 뒤에도 provider 초기화나 촬영 콜백이
    // 늦게 도착할 수 있어, 그 시점엔 bind/파일 채택을 하지 않고 그대로 무시하거나 폐기하기 위한 활성 플래그입니다.
    var isActive by remember { mutableStateOf(true) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // PreviewView가 layout되어 실제 크기와 display rotation을 확보하기 전에는 viewPort가 null이라
    // bind할 수 없습니다. camera provider 준비와 view layout 준비가 각각 다른 타이밍에 끝날 수 있어
    // 두 콜백(provider 리스너, layout change 리스너)이 모두 이 함수를 호출하고, 여기서 준비 상태와
    // rebind 필요 여부를 함께 판단합니다.
    fun tryBind(previewView: PreviewView) {
        if (!isActive || !cameraProviderFuture.isDone) return
        val display = previewView.display ?: return
        val width = previewView.width
        val height = previewView.height
        if (!isCameraBindReady(width, height)) return
        val viewPort = previewView.viewPort ?: return

        val key = CameraBindKey(width = width, height = height, rotation = display.rotation)
        if (!shouldRebindCamera(lastBoundKey, key)) return

        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(display.rotation)
                .build()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build()

            cameraReady = false
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                useCaseGroup,
            )
            boundImageCapture = imageCapture
            lastBoundKey = key
            cameraReady = true
        } catch (e: Exception) {
            cameraReady = false
            boundImageCapture = null
            onCameraUnavailable()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isActive = false
            // provider가 이미 준비된 경우에만 즉시 unbind한다. 아직 준비되지 않았다면 위 listener가
            // isActive를 먼저 확인해 bind 자체를 하지 않으므로 정리할 대상이 없고, 여기서 블로킹
            // get()을 호출해 메인 스레드를 막지 않는다.
            if (cameraProviderFuture.isDone) {
                runCatching { cameraProviderFuture.get().unbindAll() }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    // CameraX WYSIWYG 문서의 ViewPort 예시는 FILL_CENTER를 전제로 하며, 이 값이
                    // Preview에 보이는 영역과 ViewPort가 계산하는 crop 영역을 일치시키는 명시적 계약입니다.
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                // 화면 크기/rotation이 바뀔 때마다(최초 layout 포함) 다시 bind를 시도합니다.
                previewView.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    val sizeChanged = (right - left) != (oldRight - oldLeft) || (bottom - top) != (oldBottom - oldTop)
                    if (sizeChanged) {
                        tryBind(previewView)
                    }
                }
                cameraProviderFuture.addListener(
                    { tryBind(previewView) },
                    ContextCompat.getMainExecutor(ctx),
                )
                previewView
            },
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        ) {
            Icon(Icons.Default.Close, contentDescription = "닫기", tint = Color.White)
        }

        Button(
            onClick = {
                if (isCapturing) return@Button
                val currentImageCapture = boundImageCapture ?: return@Button
                isCapturing = true
                val outputFile = prepareOutputFile()
                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                currentImageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            if (isActive) {
                                isCapturing = false
                                onImageCaptured(outputFile)
                            } else {
                                discardOutputFile(outputFile)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            if (isActive) {
                                isCapturing = false
                                onImageCaptureFailed(outputFile)
                            } else {
                                discardOutputFile(outputFile)
                            }
                        }
                    },
                )
            },
            enabled = cameraReady && !isCapturing && boundImageCapture != null,
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
        ) {
            Text("촬영")
        }
    }
}

@Composable
private fun CameraReviewContent(
    file: File,
    onRetake: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = file,
            contentDescription = "촬영한 사진 미리보기",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit,
        )
        Surface(color = BrandSurface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onRetake, modifier = Modifier.weight(1f)) {
                    Text("다시 촬영")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                ) {
                    Text("사용하기")
                }
            }
        }
    }
}
