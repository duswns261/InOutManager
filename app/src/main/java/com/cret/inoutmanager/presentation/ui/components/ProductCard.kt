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
import com.cret.inoutmanager.domain.model.Product

@Composable
fun ProductCard(product: Product) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
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

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    val sampleProduct = Product(1, "프리뷰 제품 1", "A-1 창고", 82)
    ProductCard(product = sampleProduct)
}