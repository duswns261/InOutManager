package com.cret.inoutmanger.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cret.inoutmanger.data.dao.ProductDao
import com.cret.inoutmanger.data.model.Product
import com.cret.inoutmanger.data.repository.ProductRepository
import com.cret.inoutmanger.presentation.ui.components.NewProductDialog
import com.cret.inoutmanger.presentation.ui.components.OutboundQuantityDialog
import com.cret.inoutmanger.presentation.ui.screens.InboundScreen
import com.cret.inoutmanger.presentation.ui.screens.OutboundScreen
import com.cret.inoutmanger.presentation.ui.screens.StatusScreen
import com.cret.inoutmanger.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanger.ui.theme.InOutMangerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun InventoryApp(
    viewModel: InventoryViewModel = viewModel()
) {
    val products = viewModel.products
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    // 출고 관련 상태
    var showOutboundInput by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedProductForOutbound by remember { mutableStateOf<Product?>(null) }
    var outboundQuantityInput by remember { mutableStateOf("") }

    val tabs = listOf("입고", "출고", "자재 현황")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val coroutineScope = rememberCoroutineScope()

    val skyBlueColor = Color(0xFF03A9F4)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "재고관리",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black
                )
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
//                        TabRowDefaults.Indicator(
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = skyBlueColor
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTab == index) skyBlueColor else Color.Gray,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> InboundScreen(products, onAddClick = { showAddDialog = true })
                    1 -> OutboundScreen(
                        products = products,
                        onOutboundClick = { product ->
                            selectedProductForOutbound = product
                            outboundQuantityInput = ""
                            showOutboundInput = true
                        }
                    )
                    2 -> StatusScreen(
                        products = products,
                        onDeleteRequest = { product ->
                            viewModel.deleteProduct(product)
                        }
                    )
                }
            }
        }

        // 다이얼로그 처리
        if (showAddDialog) {
            NewProductDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, location, qty ->
                    viewModel.addProduct(name, location, qty)
                    showAddDialog = false
                }
            )
        }

        if (showOutboundInput && selectedProductForOutbound != null) {
            OutboundQuantityDialog(
                productName = selectedProductForOutbound!!.name,
                currentQty = selectedProductForOutbound!!.quantity,
                onDismiss = { showOutboundInput = false },
                onNext = { qtyString ->
                    outboundQuantityInput = qtyString
                    showOutboundInput = false
                    showConfirmDialog = true
                }
            )
        }

        if (showConfirmDialog && selectedProductForOutbound != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text(text = "출고 확인") },
                text = { Text("${selectedProductForOutbound!!.name}을(를) ${outboundQuantityInput}개 출고하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val qty = outboundQuantityInput.toIntOrNull() ?: 0
                            if (qty > 0) {
                                viewModel.decreaseQuantity(selectedProductForOutbound!!, qty)
                            }
                            showConfirmDialog = false
                            selectedProductForOutbound = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor)
                    ) { Text("확인") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConfirmDialog = false }) { Text("취소") }
                },
                containerColor = Color.White
            )
        }
    }
}

// ▼▼▼ [수정된 프리뷰 코드] ▼▼▼

@Preview(
    name = "메인 화면 미리보기",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun PreviewInventoryApp() {
    // 1. 가짜 DAO 생성
    val fakeDao = object : ProductDao {
        override fun getAllProducts(): Flow<List<Product>> {
            return flowOf(
                listOf(
                    Product(1, "프리뷰 제품 1", "A-1 창고", 82),
                    Product(2, "프리뷰 제품 2", "B-2 창고", 10),
                    Product(3, "프리뷰 제품 3", "C-3 창고", 0)
                )
            )
        }
        override suspend fun insertProduct(product: Product) {}
        override suspend fun updateProduct(product: Product) {}
        override suspend fun deleteProduct(product: Product) {}
    }

    // 2. [핵심 수정] 가짜 DAO를 품은 '가짜 Repository' 생성
    val fakeRepository = ProductRepository(fakeDao)

    // 3. 뷰모델에 Repository 전달 (이제 에러가 사라집니다)
    val fakeViewModel = InventoryViewModel(fakeRepository)

    // 4. 테마 적용하여 렌더링
    InOutMangerTheme {
        InventoryApp(viewModel = fakeViewModel)
    }
}