package com.cret.inoutmanger.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanger.data.model.Product

@Composable
fun OutboundScreen(products: List<Product>, onOutboundClick: (Product) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(products) { product ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "${product.quantity}개", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(text = product.location, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onOutboundClick(product) },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.Red),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("- 출고 하기")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OutboundScreenPreview() {
    val sampleProducts = listOf(
        Product(1, "프리뷰 제품 1", "A-1 창고", 82),
        Product(2, "프리뷰 제품 2", "B-2 창고", 10)
    )

    OutboundScreen(
        products = sampleProducts,
        onOutboundClick = {} // 클릭해도 아무 일 안 일어나게 설정
    )
}