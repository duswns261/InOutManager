package com.cret.inoutmanager.presentation.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandSurface
import java.io.File
import java.io.InputStream

private val DialogHeaderCloseButtonSlot = 48.dp

/** 제품명 펼침 인라인 팝오버를 찾기 위한 테스트 전용 tag. */
internal const val DialogHeaderExpandedNameTag = "dialog-header-expanded-name"

/** overflow일 때만 클릭 가능해지는 제품명 행을 찾기 위한 테스트 전용 tag. */
internal const val DialogHeaderNameRowTag = "dialog-header-name-row"

/**
 * 신규 등록/제품 요약 다이얼로그가 공유하는 헤더입니다.
 * 제목을 닫기 버튼 유무와 무관하게 가로 중앙에 배치하기 위해, 닫기 버튼과 대칭인
 * 여백을 제목 양쪽에 확보합니다.
 *
 * [expandable]이 true이면 제목을 1줄 + ellipsis로 유지하되, 실제 overflow가 발생했을 때만
 * 펼침 chevron을 노출합니다. 제목 행 전체(최소 48dp)를 눌러 펼치면 헤더 바로 아래에 전체
 * 제목을 보여주는 팝오버가 나타나며, 다시 탭·바깥 탭·뒤로가기로 닫힙니다.
 * `NewProductDialog`처럼 고정 제목에는 사용하지 않습니다([expandable] 기본값 false).
 */
@Composable
internal fun DialogHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    expandable: Boolean = false,
) {
    var hasOverflow by remember(title) { mutableStateOf(false) }
    var expanded by remember(title) { mutableStateOf(false) }
    var headerHeightPx by remember { mutableStateOf(0) }

    if (expandable) {
        BackHandler(enabled = expanded) { expanded = false }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { headerHeightPx = it.size.height },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(DialogHeaderCloseButtonSlot))

        val nameRowModifier = if (expandable && hasOverflow) {
            Modifier
                .testTag(DialogHeaderNameRowTag)
                .clip(RoundedCornerShape(8.dp))
                .heightIn(min = 48.dp)
                .clickable(onClickLabel = if (expanded) "전체 제품명 닫기" else "전체 제품명 보기") {
                    expanded = !expanded
                }
                .semantics { stateDescription = if (expanded) "펼쳐짐" else "접힘" }
        } else {
            Modifier
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .then(nameRowModifier),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result -> if (expandable) hasOverflow = result.hasVisualOverflow },
                modifier = Modifier.weight(1f, fill = false),
            )
            if (expandable && hasOverflow) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
        }

        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "닫기")
        }
    }

    if (expandable && expanded) {
        Popup(
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, headerHeightPx),
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag(DialogHeaderExpandedNameTag)
                        .semantics { contentDescription = "전체 제품명: $title" },
                )
            }
        }
    }
}

@Composable
fun NewProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, location: String, quantity: String, imageFile: File?, imageOrigin: ProductImageOrigin, onResult: (Boolean) -> Unit) -> Unit,
    createTemporaryImageFile: () -> File,
    discardTemporaryImage: (File) -> Unit,
    importImage: (openStream: () -> InputStream, onResult: (Result<File>) -> Unit) -> Unit,
    onCameraOpened: () -> Unit = {},
    onCameraCaptureCompleted: () -> Unit = {},
    onCameraCaptureFailed: (CameraCaptureFailure) -> Unit = {},
    onCameraPermissionDenied: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var confirmedImageFile by remember { mutableStateOf<File?>(null) }
    var confirmedImageOrigin by remember { mutableStateOf(ProductImageOrigin.CAMERA) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun dismissAndCleanUp() {
        if (isSubmitting || isImporting) return
        confirmedImageFile?.let(discardTemporaryImage)
        onDismiss()
    }

    fun acquireImage(file: File, origin: ProductImageOrigin) {
        confirmedImageFile?.let(discardTemporaryImage)
        confirmedImageFile = file
        confirmedImageOrigin = origin
    }

    Dialog(onDismissRequest = ::dismissAndCleanUp) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            modifier = Modifier.heightIn(max = 640.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DialogHeader(title = "신규 제품 등록", onClose = ::dismissAndCleanUp)
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ProductImageSelection(
                            currentImage = confirmedImageFile,
                            productName = name.ifBlank { "신규 제품" },
                            onImageAcquired = ::acquireImage,
                            createTemporaryImageFile = createTemporaryImageFile,
                            discardTemporaryImage = discardTemporaryImage,
                            importImage = importImage,
                            enabled = !isSubmitting && !isImporting,
                            onRemoveRequested = {
                                confirmedImageFile?.let(discardTemporaryImage)
                                confirmedImageFile = null
                            },
                            onImportStateChanged = { isImporting = it },
                            onCameraOpened = onCameraOpened,
                            onCameraCaptureCompleted = onCameraCaptureCompleted,
                            onCameraCaptureFailed = onCameraCaptureFailed,
                            onCameraPermissionDenied = onCameraPermissionDenied,
                        )
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
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = ::dismissAndCleanUp, enabled = !isSubmitting && !isImporting, modifier = Modifier.weight(1f)) { Text("취소") }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else {
                                isSubmitting = true
                                onConfirm(name, location, quantity, confirmedImageFile, confirmedImageOrigin) { success ->
                                    // 성공 시에는 호출부가 다이얼로그를 닫으므로 이 Composable이 곧 폐기됩니다.
                                    if (!success) {
                                        isSubmitting = false
                                        // 실패 시 AddProductUseCase가 확정된 파일을 이미 삭제했으므로
                                        // 더 이상 존재하지 않는 경로를 들고 재시도하지 않도록 선택을 초기화합니다.
                                        if (confirmedImageFile != null) {
                                            confirmedImageFile = null
                                            Toast.makeText(
                                                context,
                                                "등록에 실패해 첨부한 사진이 초기화되었습니다. 다시 선택해주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting && !isImporting,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                    ) { Text("등록") }
                }
            }
        }
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
        onConfirm = { _, _, _, _, _, _ -> },
        createTemporaryImageFile = { File.createTempFile("preview", ".jpg") },
        discardTemporaryImage = {},
        importImage = { _, _ -> },
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
