package com.cret.inoutmanager.presentation.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandBackground
import com.cret.inoutmanager.ui.theme.BrandSurface
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val EXIT_CONFIRM_WINDOW_MS = 600L

@OptIn(ExperimentalMaterial3Api::class)
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
    val currentRoute = currentBackStackEntry?.destination?.route
    val isHome = currentRoute == null || currentRoute == InventoryRoute.Home.route

    fun navigateToFeature(route: String) {
        if (route == currentRoute) return
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    val context = LocalContext.current
    var lastBackPressTime by remember { mutableStateOf(0L) }
    BackHandler(enabled = isHome) {
        val now = System.currentTimeMillis()
        if (now - lastBackPressTime <= EXIT_CONFIRM_WINDOW_MS) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = now
            Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = BrandBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .background(BrandBackground)
                    .statusBarsPadding()
            ) {
                if (!isHome) {
                    var showFeatureSheet by remember { mutableStateOf(false) }
                    val sheetState = rememberModalBottomSheetState()
                    val scope = rememberCoroutineScope()
                    val currentFeature = InventoryRoute.featureRoutes[InventoryRoute.indexOfFeature(currentRoute)]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AssistChip(
                            onClick = { showFeatureSheet = true },
                            label = { Text(text = currentFeature.title, fontWeight = FontWeight.Bold) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = BrandSurface,
                                labelColor = BrandAccent,
                                trailingIconContentColor = BrandAccent
                            ),
                            border = BorderStroke(1.dp, BrandAccent)
                        )
                    }

                    if (showFeatureSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showFeatureSheet = false },
                            sheetState = sheetState,
                            containerColor = BrandSurface
                        ) {
                            InventoryRoute.featureRoutes.forEach { route ->
                                val isSelected = route == currentFeature
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = route.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    trailingContent = {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = BrandAccent)
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = BrandSurface),
                                    modifier = Modifier.clickable {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            showFeatureSheet = false
                                            navigateToFeature(route.route)
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == InventoryRoute.Inbound.route) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = BrandAccent
                ) {
                    Icon(Icons.Default.Add, contentDescription = "신규 제품 등록")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BrandBackground)
        ) {
            InventoryNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                products = uiState.products,
                onNavigateToFeature = ::navigateToFeature,
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
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                    ) { Text("확인") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConfirmDialog = false }) { Text("취소") }
                },
                containerColor = BrandSurface
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
