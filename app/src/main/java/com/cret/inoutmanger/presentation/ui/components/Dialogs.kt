package com.cret.inoutmanger.presentation.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

val SkyBlue = Color(0xFF03A9F4)

// 신규 등록 다이얼로그
@Composable
fun NewProductDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("신규 제품 등록", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
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
                    onValueChange = { quantity = it },
                    label = { Text("수량") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("취소") }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else {
                                onConfirm(name, location, quantity)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) { Text("등록") }
                }
            }
        }
    }
}

// 출고 수량 입력 다이얼로그
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
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("출고 수량 입력", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$productName (현재고: $currentQty)", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = inputQty,
                    onValueChange = { if (it.all { char -> char.isDigit() }) inputQty = it },
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
                            if (qty <= 0) {
                                Toast.makeText(context, "1개 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else if (qty > currentQty) {
                                Toast.makeText(context, "재고보다 많이 출고할 수 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                onNext(inputQty)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
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
        onConfirm = { _, _, _ -> }
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