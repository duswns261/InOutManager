package com.cret.inoutmanager.presentation.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cret.inoutmanager.R
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import java.io.File
import java.io.IOException
import java.io.InputStream

private val ProductImageSelectionDefaultSize = 160.dp
private val ProductImageSelectionCornerRadius = 16.dp
private val ProductImageSelectionButtonSize = 40.dp
private val ProductImageSelectionButtonIconSize = 20.dp
private val ProductImageSelectionButtonBorderColor = Color(0xFFE0E0E0)
private val ProductImageSelectionButtonIconColor = Color(0xFF3A3A3A)
private val ProductImageSelectionMenuCornerRadius = 16.dp

/** 이미지 선택 + 버튼과 앨범/카메라/제거 메뉴를 여는 진입점을 찾기 위한 테스트 전용 tag. */
internal const val ProductImageSelectionButtonTag = "product-image-selection-button"
internal const val ProductImageSelectionCameraOptionTag = "product-image-selection-camera-option"
internal const val ProductImageSelectionPickerOptionTag = "product-image-selection-picker-option"
internal const val ProductImageSelectionRemoveOptionTag = "product-image-selection-remove-option"

/**
 * 신규 등록과 기존 제품 요약이 공유하는 이미지 선택 영역입니다.
 * [ProductThumbnail]로 현재 이미지(영구 경로 `String` 또는 확정 전 임시 `File`)를 크게 표시하고,
 * 우측 아래 + 버튼에서 카메라 촬영 또는 Android Photo Picker로 새 이미지를 가져올 수 있습니다.
 * 이미지 유무와 관계없이 + 버튼을 항상 노출하며, content description은 이미지가 있으면
 * "제품 이미지 변경", 없으면 "제품 이미지 추가"로 안내합니다.
 *
 * 카메라 출처만 [onCameraOpened]/[onCameraCaptureCompleted]/[onCameraCaptureFailed] 촬영
 * reporting 콜백을 호출합니다. Picker로 가져온 이미지는 [importImage]가 감싼 일회성 스트림
 * 공급자로만 열리며, 선택한 `Uri`를 이 컴포저블이나 호출부의 장기 상태로 보관하지 않습니다.
 *
 * 새로 가져온 파일의 확정(신규 등록 임시 보관 또는 기존 제품 즉시 commit)은 [onImageAcquired]를
 * 통해 호출부가 결정합니다. [onRemoveRequested]가 주어지고 현재 이미지가 있을 때만 메뉴에
 * "사진 제거" 항목이 추가되며, 기존 제품 요약처럼 독립 삭제를 제공하지 않는 호출부는 생략합니다.
 */
@Composable
fun ProductImageSelection(
    currentImage: Any?,
    productName: String,
    onImageAcquired: (File, ProductImageOrigin) -> Unit,
    createTemporaryImageFile: () -> File,
    discardTemporaryImage: (File) -> Unit,
    importImage: (openStream: () -> InputStream, onResult: (Result<File>) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = ProductImageSelectionDefaultSize,
    enabled: Boolean = true,
    onRemoveRequested: (() -> Unit)? = null,
    onCameraOpened: () -> Unit = {},
    onCameraCaptureCompleted: () -> Unit = {},
    onCameraCaptureFailed: (CameraCaptureFailure) -> Unit = {},
    onCameraPermissionDenied: () -> Unit = {},
) {
    val context = LocalContext.current
    var showCamera by remember { mutableStateOf(false) }
    var showSourceMenu by remember { mutableStateOf(false) }

    val hasImage = when (currentImage) {
        null -> false
        is String -> currentImage.isNotBlank()
        else -> true
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCamera = true
            onCameraOpened()
        } else {
            onCameraPermissionDenied()
        }
    }

    fun openCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            showCamera = true
            onCameraOpened()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        importImage(
            { context.contentResolver.openInputStream(uri) ?: throw IOException("선택한 이미지를 열 수 없습니다") },
        ) { result ->
            result.fold(
                onSuccess = { file -> onImageAcquired(file, ProductImageOrigin.PICKER) },
                onFailure = {
                    Toast.makeText(context, "이미지를 가져오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    Box(modifier = modifier.size(imageSize)) {
        ProductThumbnail(
            imagePath = currentImage,
            productName = productName,
            size = imageSize,
            cornerRadius = ProductImageSelectionCornerRadius,
        )

        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            FilledIconButton(
                onClick = { showSourceMenu = true },
                enabled = enabled,
                modifier = Modifier
                    .size(ProductImageSelectionButtonSize)
                    .shadow(elevation = 2.dp, shape = CircleShape, clip = false)
                    .border(width = 1.dp, color = ProductImageSelectionButtonBorderColor, shape = CircleShape)
                    .testTag(ProductImageSelectionButtonTag)
                    .semantics {
                        contentDescription = if (hasImage) "제품 이미지 변경" else "제품 이미지 추가"
                    },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White,
                    contentColor = ProductImageSelectionButtonIconColor,
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_photo_camera_24),
                    contentDescription = null,
                    modifier = Modifier.size(ProductImageSelectionButtonIconSize),
                    tint = ProductImageSelectionButtonIconColor,
                )
            }

            DropdownMenu(
                expanded = showSourceMenu,
                onDismissRequest = { showSourceMenu = false },
                shape = RoundedCornerShape(ProductImageSelectionMenuCornerRadius),
                containerColor = Color.White,
            ) {
                DropdownMenuItem(
                    text = { Text("앨범에서 사진 선택") },
                    modifier = Modifier.testTag(ProductImageSelectionPickerOptionTag),
                    onClick = {
                        showSourceMenu = false
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text("카메라로 촬영") },
                    modifier = Modifier.testTag(ProductImageSelectionCameraOptionTag),
                    onClick = {
                        showSourceMenu = false
                        openCamera()
                    },
                )
                if (onRemoveRequested != null && hasImage) {
                    DropdownMenuItem(
                        text = { Text("사진 제거") },
                        modifier = Modifier.testTag(ProductImageSelectionRemoveOptionTag),
                        onClick = {
                            showSourceMenu = false
                            onRemoveRequested()
                        },
                    )
                }
            }
        }
    }

    if (showCamera) {
        ProductCameraDialog(
            createTemporaryFile = createTemporaryImageFile,
            discardTemporaryFile = discardTemporaryImage,
            onPhotoConfirmed = { file ->
                showCamera = false
                onImageAcquired(file, ProductImageOrigin.CAMERA)
            },
            onDismiss = { showCamera = false },
            onCaptureCompleted = onCameraCaptureCompleted,
            onCaptureFailed = onCameraCaptureFailed,
        )
    }
}
