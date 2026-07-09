package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cret.inoutmanager.ui.theme.BrandAccentDark
import com.cret.inoutmanager.ui.theme.BrandChipBackground
import com.cret.inoutmanager.ui.theme.BrandSurface

@Composable
fun OutboundScreen(
    products: List<Product>,
    onOutboundClick: (Product) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product ->
                Card(
                    onClick = { onOutboundClick(product) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "현재 재고: ${product.quantity}개", fontSize = 12.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .background(BrandChipBackground, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = product.location, fontSize = 10.sp, color = BrandAccentDark)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun OutboundScreenPreview() {
    val sampleProducts = listOf(
        Product(1, "아이폰 15", "A-1 구역", 25),
        Product(2, "갤럭시 S24", "B-3 구역", 12)
    )
    OutboundScreen(
        products = sampleProducts,
        onOutboundClick = {}
    )
}
