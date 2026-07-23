package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductCard
import com.cret.inoutmanager.presentation.ui.components.ProductSearchBar
import com.cret.inoutmanager.presentation.ui.search.filterAndSortProducts

/** 검색 결과 없음 안내 문구를 찾기 위한 테스트 전용 tag. */
internal const val InboundEmptySearchResultTag = "inbound-empty-search-result"

@Composable
fun InboundScreen(products: List<Product>, onProductClick: (Product) -> Unit = {}) {
    var query by rememberSaveable { mutableStateOf("") }
    val displayProducts = filterAndSortProducts(products, query)

    Column(modifier = Modifier.padding(16.dp)) {
        ProductSearchBar(query = query, onQueryChange = { query = it })
        Spacer(modifier = Modifier.height(12.dp))

        if (displayProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "검색 결과가 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag(InboundEmptySearchResultTag),
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(displayProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.clickable { onProductClick(product) },
                    )
                }
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