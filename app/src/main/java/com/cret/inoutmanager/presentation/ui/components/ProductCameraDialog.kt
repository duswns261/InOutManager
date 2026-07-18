package com.cret.inoutmanager.presentation.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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
    val imageCapture = remember { ImageCapture.Builder().build() }
    var cameraReady by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    // Compose가 이 컴포저블을 폐기(dismiss/재촬영 전환)한 뒤에도 비동기 촬영 콜백이 늦게 도착할 수 있어,
    // 그 시점엔 파일을 상위 상태에 채택시키지 않고 바로 폐기하기 위한 활성 플래그입니다.
    var isActive by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose { isActive = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener(
                    {
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageCapture,
                            )
                            cameraReady = true
                        } catch (e: Exception) {
                            if (isActive) {
                                onCameraUnavailable()
                            }
                        }
                    },
                    ContextCompat.getMainExecutor(ctx),
                )
                previewView
            },
        )

        DisposableEffect(Unit) {
            onDispose {
                runCatching { ProcessCameraProvider.getInstance(context).get().unbindAll() }
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        ) {
            Icon(Icons.Default.Close, contentDescription = "닫기", tint = Color.White)
        }

        Button(
            onClick = {
                if (isCapturing) return@Button
                isCapturing = true
                val outputFile = prepareOutputFile()
                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                imageCapture.takePicture(
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
            enabled = cameraReady && !isCapturing,
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
