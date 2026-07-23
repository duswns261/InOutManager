package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.presentation.ui.components.ProductSearchBar
import com.cret.inoutmanager.presentation.ui.components.ProductThumbnail
import com.cret.inoutmanager.presentation.ui.search.filterAndSortProducts
import com.cret.inoutmanager.ui.theme.BrandAccentDark
import com.cret.inoutmanager.ui.theme.BrandChipBackground
import com.cret.inoutmanager.ui.theme.BrandSurface

/** 검색 결과 없음 안내 문구를 찾기 위한 테스트 전용 tag. */
internal const val OutboundEmptySearchResultTag = "outbound-empty-search-result"

@Composable
fun OutboundScreen(
    products: List<Product>,
    onOutboundClick: (Product) -> Unit
) {
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
                    modifier = Modifier.testTag(OutboundEmptySearchResultTag),
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(displayProducts, key = { it.id }) { product ->
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductThumbnail(imagePath = product.imagePath, productName = product.name)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "현재 재고: ${product.quantity}개",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 96.dp)
                                    .background(BrandChipBackground, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = product.location,
                                    fontSize = 10.sp,
                                    color = BrandAccentDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
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
