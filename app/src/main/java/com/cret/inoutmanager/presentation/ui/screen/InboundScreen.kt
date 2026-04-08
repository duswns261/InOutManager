package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.data.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductCard

@Composable
fun InboundScreen(products: List<Product>, onAddClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC))
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("신규 제품 등록")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products) { product ->
                ProductCard(product)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun InboundScreenPreview() {
    // 1. 프리뷰용 가짜 데이터 만들기
    val sampleProducts = listOf(
        Product(1, "프리뷰 제품 1", "A-1 창고", 50),
        Product(2, "프리뷰 제품 2", "B-2 창고", 10),
        Product(3, "프리뷰 제품 3", "C-3 창고", 0)
    )

    // 2. 실제 화면 함수 호출 (이벤트는 비워둠 {})
    InboundScreen(
        products = sampleProducts,
        onAddClick = {}
    )
}