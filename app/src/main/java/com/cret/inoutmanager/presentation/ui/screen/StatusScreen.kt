package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductCard
import com.cret.inoutmanager.presentation.ui.components.ProductSearchBar
import com.cret.inoutmanager.presentation.ui.components.SummaryCard
import com.cret.inoutmanager.presentation.ui.search.filterAndSortProducts
import com.cret.inoutmanager.ui.theme.BrandSurface

import androidx.compose.foundation.ExperimentalFoundationApi

/** 검색 결과 없음 안내 문구를 찾기 위한 테스트 전용 tag. */
internal const val StatusEmptySearchResultTag = "status-empty-search-result"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatusScreen(
    products: List<Product>,
    onDeleteRequest: (Product) -> Unit) {
    // 요약 수치는 검색과 무관하게 항상 전체 제품 기준을 유지합니다.
    val totalTypes = products.size
    val totalQuantity = products.sumOf { it.quantity }

    var query by rememberSaveable { mutableStateOf("") }
    val displayProducts = filterAndSortProducts(products, query)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(title = "총 제품 종류", value = "${totalTypes}개", modifier = Modifier.weight(1f))
            SummaryCard(title = "총 재고 수량", value = "${totalQuantity}개", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProductSearchBar(query = query, onQueryChange = { query = it })
        Spacer(modifier = Modifier.height(16.dp))
        Text("전체 제품 현황", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        if (displayProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "검색 결과가 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag(StatusEmptySearchResultTag),
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(displayProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                // 삭제는 실수 방지를 위해 길게 누른 뒤 확인 다이얼로그를 거치도록 합니다.
                                productToDelete = product
                                showDeleteDialog = true
                            }
                        )
                    )
                }
            }
        }
    }
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            },
            containerColor = BrandSurface
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
