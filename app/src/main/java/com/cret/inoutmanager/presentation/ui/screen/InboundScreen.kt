package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductCard

@Composable
fun InboundScreen(products: List<Product>, onProductClick: (Product) -> Unit = {}) {
    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.clickable { onProductClick(product) },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun InboundScreenPreview() {
    val sampleProducts = listOf(
        Product(1, "프리뷰 제품 1", "A-1 창고", 50),
        Product(2, "프리뷰 제품 2", "B-2 창고", 10),
        Product(3, "프리뷰 제품 3", "C-3 창고", 0)
    )

    InboundScreen(products = sampleProducts)
}