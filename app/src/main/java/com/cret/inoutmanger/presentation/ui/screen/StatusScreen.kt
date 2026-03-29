package com.cret.inoutmanger.presentation.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanger.data.model.Product
import com.cret.inoutmanger.presentation.ui.components.SummaryCard

import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatusScreen(
    products: List<Product>,
    onDeleteRequest: (Product) -> Unit) {
    val totalTypes = products.size
    val totalQuantity = products.sumOf { it.quantity }

    // 삭제 확인 다이얼로그 상태
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(title = "총 제품 종류", value = "${totalTypes}개", modifier = Modifier.weight(1f))
            SummaryCard(title = "총 재고 수량", value = "${totalQuantity}개", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("전체 제품 현황", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products) { product ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { /* 짧게 누르면 상세정보 보기 등을 넣을 수 있음 */ },
                            onLongClick = {
                                productToDelete = product
                                showDeleteDialog = true
                            }
                        )) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = product.name, fontWeight = FontWeight.Bold)
                            Text(text = product.location, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(text = "${product.quantity}개", color = Color(0xFF03A9F4), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    // 삭제 확인 다이얼로그
    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("제품 삭제") },
            text = { Text("'${productToDelete?.name}'을(를) 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { onDeleteRequest(it) }
                        showDeleteDialog = false
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // 삭제는 위험하므로 빨간색
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            },
            containerColor = Color.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun StatusScreenPreview() {
    val sampleProducts = listOf(
        Product(1, "갤럭시 S24", "창고 A", 100),
        Product(2, "아이폰 15", "창고 B", 50)
    )

    StatusScreen(
        products = sampleProducts,
        onDeleteRequest = {}
        )
}