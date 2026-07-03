package com.cret.inoutmanager.presentation.ui

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.*
import com.cret.inoutmanager.presentation.ui.components.NewProductDialog
import com.cret.inoutmanager.presentation.ui.components.OutboundQuantityDialog
import com.cret.inoutmanager.presentation.ui.screens.InboundScreen
import com.cret.inoutmanager.presentation.ui.screens.OutboundScreen
import com.cret.inoutmanager.presentation.ui.screens.StatusScreen
import com.cret.inoutmanager.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun InventoryApp(
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProductForOutbound by remember { mutableStateOf<Product?>(null) }
    var outboundQuantityInput by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

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
                                    color = if (pagerState.currentPage == index) skyBlueColor else Color.Gray,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
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
                    0 -> InboundScreen(
                        products = uiState.products,
                        onAddClick = { showAddDialog = true }
                    )
                    1 -> OutboundScreen(
                        products = uiState.products,
                        onOutboundClick = { product ->
                            selectedProductForOutbound = product
                            outboundQuantityInput = ""
                            showConfirmDialog = false
                        }
                    )
                    2 -> StatusScreen(
                        products = uiState.products,
                        onDeleteRequest = { product ->
                            viewModel.deleteProduct(product)
                        }
                    )
                }
            }
        }

        // --- 다이얼로그 관리 ---

        if (showAddDialog) {
            NewProductDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, location, qty ->
                    viewModel.addProduct(name, location, qty)
                    showAddDialog = false
                }
            )
        }

        if (selectedProductForOutbound != null) {
            OutboundQuantityDialog(
                productName = selectedProductForOutbound!!.name,
                currentQty = selectedProductForOutbound!!.quantity,
                onDismiss = { selectedProductForOutbound = null },
                onNext = { qtyString ->
                    outboundQuantityInput = qtyString
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
                            viewModel.decreaseQuantity(
                                selectedProductForOutbound!!,
                                outboundQuantityInput.toIntOrNull() ?: 0
                            )
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

@Preview(
    name = "메인 화면 미리보기",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun PreviewInventoryApp() {
    val fakeRepository = object : com.cret.inoutmanager.domain.repository.ProductRepository {
        override val allProducts: Flow<List<Product>> = flowOf(emptyList())
        override suspend fun insert(product: Product) {}
        override suspend fun update(product: Product) {}
        override suspend fun delete(product: Product) {}
    }
    
    val fakeUseCases = ProductUseCases(
        getProducts = GetProductsUseCase(fakeRepository),
        addProduct = AddProductUseCase(fakeRepository),
        decreaseProductQuantity = DecreaseProductQuantityUseCase(fakeRepository),
        deleteProduct = DeleteProductUseCase(fakeRepository)
    )

    val fakeViewModel = InventoryViewModel(fakeUseCases)

    InOutManagerTheme {
        InventoryApp(viewModel = fakeViewModel)
    }
}
