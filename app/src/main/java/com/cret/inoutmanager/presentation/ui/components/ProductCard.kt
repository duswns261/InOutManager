package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanager.data.model.Product

@Composable
fun ProductCard(product: Product) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = product.location, color = Color.Gray, fontSize = 14.sp)
            }
            Text(
                text = "${product.quantity}개",
                color = SkyBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCard() {
    // 프리뷰용 더미 데이터 생성
    val dummyProduct = Product(
        id = 1,
        name = "프리뷰용 제품",
        location = "A-1 창고",
        quantity = 99
    )

    // 패딩을 주어 실제 리스트에서의 모습을 흉내냄
    Box(modifier = Modifier.padding(10.dp)) {
        ProductCard(product = dummyProduct)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSummaryCard() {
    Box(modifier = Modifier.padding(10.dp)) {
        SummaryCard(
            title = "총 재고 수량",
            value = "1,234개",
            modifier = Modifier.width(150.dp) // 임의의 너비 지정
        )
    }
}