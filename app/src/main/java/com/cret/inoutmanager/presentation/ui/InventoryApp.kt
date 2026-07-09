package com.cret.inoutmanager.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.*
import com.cret.inoutmanager.presentation.ui.components.NewProductDialog
import com.cret.inoutmanager.presentation.ui.components.OutboundQuantityDialog
import com.cret.inoutmanager.presentation.ui.navigation.InventoryNavGraph
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import com.cret.inoutmanager.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun InventoryApp(
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProductForOutbound by remember { mutableStateOf<Product?>(null) }
    var outboundQuantityInput by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedTabIndex = InventoryRoute.indexOf(currentBackStackEntry?.destination?.route)

    fun navigateToTab(index: Int) {
        val target = InventoryRoute.ordered[index]
        if (target.route == currentBackStackEntry?.destination?.route) return
        navController.navigate(target.route) {
            // saveState/restoreState는 탭을 오갈 때 각 화면(LazyColumn)의 스크롤 위치를 보존하기 위함이다.
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 80.dp.toPx() }

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
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = skyBlueColor
                        )
                    }
                ) {
                    InventoryRoute.ordered.forEachIndexed { index, route ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { navigateToTab(index) },
                            text = {
                                Text(
                                    route.title,
                                    color = if (selectedTabIndex == index) skyBlueColor else Color.Gray,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .pointerInput(selectedTabIndex) {
                    // 컴포지션에서 읽히지 않는 순수 제스처 누적값이므로 Compose state가 아닌
                    // pointerInput 코루틴 스코프에 갇힌 지역 변수로 충분하다.
                    var dragAccumulatedPx = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { dragAccumulatedPx = 0f },
                        onDragCancel = { dragAccumulatedPx = 0f },
                        onDragEnd = {
                            when {
                                dragAccumulatedPx <= -swipeThresholdPx &&
                                    selectedTabIndex < InventoryRoute.ordered.lastIndex ->
                                    navigateToTab(selectedTabIndex + 1)
                                dragAccumulatedPx >= swipeThresholdPx && selectedTabIndex > 0 ->
                                    navigateToTab(selectedTabIndex - 1)
                            }
                            dragAccumulatedPx = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        dragAccumulatedPx += dragAmount
                    }
                }
        ) {
            InventoryNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                products = uiState.products,
                onAddClick = { showAddDialog = true },
                onOutboundClick = { product ->
                    selectedProductForOutbound = product
                    outboundQuantityInput = ""
                    showConfirmDialog = false
                },
                onDeleteRequest = { product ->
                    viewModel.deleteProduct(product)
                }
            )
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
