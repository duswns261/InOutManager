package com.cret.inoutmanager.presentation.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandSurface
import java.io.File

@Composable
fun NewProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, location: String, quantity: String, imageFile: File?, onResult: (Boolean) -> Unit) -> Unit,
    createTemporaryImageFile: () -> File,
    discardTemporaryImage: (File) -> Unit,
    onCameraOpened: () -> Unit = {},
    onCameraCaptureCompleted: () -> Unit = {},
    onCameraCaptureFailed: (CameraCaptureFailure) -> Unit = {},
    onCameraPermissionDenied: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var confirmedImageFile by remember { mutableStateOf<File?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var permissionDeniedOnce by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun dismissAndCleanUp() {
        if (isSubmitting) return
        confirmedImageFile?.let(discardTemporaryImage)
        onDismiss()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCamera = true
            onCameraOpened()
        } else {
            permissionDeniedOnce = true
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

    Dialog(onDismissRequest = ::dismissAndCleanUp) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("신규 제품 등록", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = ::dismissAndCleanUp) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("제품명") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("위치") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { input ->
                        if (input.all { char -> char.isDigit() }) {
                            quantity = input
                        }
                    },
                    label = { Text("수량") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (confirmedImageFile != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = confirmedImageFile,
                            contentDescription = "촬영한 제품 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            TextButton(onClick = { if (!isSubmitting) openCamera() }) { Text("다시 촬영") }
                            TextButton(
                                onClick = {
                                    if (isSubmitting) return@TextButton
                                    confirmedImageFile?.let(discardTemporaryImage)
                                    confirmedImageFile = null
                                }
                            ) { Text("사진 제거") }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { openCamera() },
                        enabled = !isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("사진 촬영 (선택)")
                    }
                    if (permissionDeniedOnce) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "카메라 권한이 거부되어 사진 없이 등록할 수 있습니다.",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = ::dismissAndCleanUp, enabled = !isSubmitting, modifier = Modifier.weight(1f)) { Text("취소") }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else {
                                isSubmitting = true
                                onConfirm(name, location, quantity, confirmedImageFile) { success ->
                                    // 성공 시에는 호출부가 다이얼로그를 닫으므로 이 Composable이 곧 폐기됩니다.
                                    if (!success) {
                                        isSubmitting = false
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                    ) { Text("등록") }
                }
            }
        }
    }

    if (showCamera) {
        ProductCameraDialog(
            createTemporaryFile = createTemporaryImageFile,
            discardTemporaryFile = discardTemporaryImage,
            onPhotoConfirmed = { file ->
                confirmedImageFile?.let(discardTemporaryImage)
                confirmedImageFile = file
                showCamera = false
            },
            onDismiss = { showCamera = false },
            onCaptureCompleted = onCameraCaptureCompleted,
            onCaptureFailed = onCameraCaptureFailed,
        )
    }
}

@Composable
fun OutboundQuantityDialog(
    productName: String,
    currentQty: Int,
    onDismiss: () -> Unit,
    onNext: (String) -> Unit
) {
    var inputQty by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("출고 수량 입력", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$productName (현재고: $currentQty)", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = inputQty,
                    onValueChange = { input ->
                        if (input.all { char -> char.isDigit() }) {
                            inputQty = input
                        }
                    },
                    label = { Text("출고 수량") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("취소") }
                    Button(
                        onClick = {
                            val qty = inputQty.toIntOrNull() ?: 0
                            // 출고 수량은 재고 차감 정책에 직접 영향을 주므로 다이얼로그에서 먼저 검증합니다.
                            if (qty <= 0) {
                                Toast.makeText(context, "1개 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else if (qty > currentQty) {
                                Toast.makeText(context, "재고보다 많이 출고할 수 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                onNext(inputQty)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                    ) { Text("출고 완료") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewProductDialog() {
    NewProductDialog(
        onDismiss = {},
        onConfirm = { _, _, _, _, _ -> },
        createTemporaryImageFile = { File.createTempFile("preview", ".jpg") },
        discardTemporaryImage = {},
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOutboundQuantityDialog() {
    OutboundQuantityDialog(
        productName = "테스트 제품",
        currentQty = 50,
        onDismiss = {},
        onNext = {}
    )
}
