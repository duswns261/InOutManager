package com.cret.inoutmanager.presentation.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cret.inoutmanager.analytics.PhotoCaptureFailureReason
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.*
import com.cret.inoutmanager.presentation.ui.components.InventoryTopAppBar
import com.cret.inoutmanager.presentation.ui.components.NewProductDialog
import com.cret.inoutmanager.presentation.ui.components.OutboundQuantityDialog
import com.cret.inoutmanager.presentation.ui.components.ProductSummaryDialog
import com.cret.inoutmanager.presentation.ui.navigation.InventoryNavGraph
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import com.cret.inoutmanager.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandBackground
import com.cret.inoutmanager.ui.theme.BrandSurface
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
    var selectedProductIdForSummary by remember { mutableStateOf<Int?>(null) }
    val selectedProductForSummary = uiState.products.find { it.id == selectedProductIdForSummary }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isHome = currentRoute == null || currentRoute == InventoryRoute.Home.route

    LaunchedEffect(currentRoute) {
        if (currentRoute == InventoryRoute.Status.route) {
            viewModel.logInventoryScreenViewed()
        }
    }


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
    LaunchedEffect(uiState.imageCleanupWarning) {
        if (uiState.imageCleanupWarning) {
            Toast.makeText(context, "이미지는 저장됐지만 이전 파일 정리에 실패했습니다.", Toast.LENGTH_SHORT).show()
            viewModel.consumeImageCleanupWarning()
        }
    }

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
            if (!isHome) {
                InventoryTopAppBar(
                    currentRoute = currentRoute,
                    onRouteSelected = ::navigateToFeature,
                    onAddProductClick = {
                        showAddDialog = true
                        viewModel.logProductRegistrationStarted()
                    },
                )
            }
        },
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
                onProductClick = { product ->
                    selectedProductIdForSummary = product.id
                },
                onOutboundClick = { product ->
                    selectedProductForOutbound = product
                    outboundQuantityInput = ""
                    showConfirmDialog = false
                    viewModel.logOutboundStarted()
                },
                onDeleteRequest = { product ->
                    viewModel.deleteProduct(product)
                }
            )
        }

        // --- 다이얼로그 관리 ---

        if (showAddDialog) {
            NewProductDialog(
                onDismiss = {
                    viewModel.resetPhotoCaptureReporting()
                    showAddDialog = false
                },
                onConfirm = { name, location, qty, imageFile, imageOrigin, onResult ->
                    viewModel.addProduct(name, location, qty, imageFile, imageOrigin) { success ->
                        onResult(success)
                        if (success) {
                            viewModel.resetPhotoCaptureReporting()
                            showAddDialog = false
                        }
                    }
                },
                createTemporaryImageFile = viewModel::createTemporaryImageFile,
                discardTemporaryImage = viewModel::discardTemporaryImage,
                importImage = viewModel::importProductImage,
                onCameraOpened = viewModel::logPhotoCaptureStarted,
                onCameraCaptureCompleted = viewModel::logPhotoCaptureCompleted,
                onCameraCaptureFailed = { viewModel.logPhotoCaptureFailed(PhotoCaptureFailureReason.CAPTURE_ERROR) },
                onCameraPermissionDenied = { viewModel.logPhotoCaptureFailed(PhotoCaptureFailureReason.PERMISSION_DENIED) },
            )
        }

        if (selectedProductForSummary != null) {
            ProductSummaryDialog(
                product = selectedProductForSummary,
                onDismiss = {
                    viewModel.resetPhotoCaptureReporting()
                    selectedProductIdForSummary = null
                },
                attachImage = { temporaryImageFile, imageOrigin, onResult ->
                    viewModel.attachProductImage(selectedProductForSummary, temporaryImageFile, imageOrigin, onResult)
                },
                removeImage = { onResult ->
                    viewModel.removeProductImage(selectedProductForSummary, onResult)
                },
                createTemporaryImageFile = viewModel::createTemporaryImageFile,
                discardTemporaryImage = viewModel::discardTemporaryImage,
                importImage = viewModel::importProductImage,
                onCameraOpened = viewModel::logPhotoCaptureStarted,
                onCameraCaptureCompleted = viewModel::logPhotoCaptureCompleted,
                onCameraCaptureFailed = { viewModel.logPhotoCaptureFailed(PhotoCaptureFailureReason.CAPTURE_ERROR) },
                onCameraPermissionDenied = { viewModel.logPhotoCaptureFailed(PhotoCaptureFailureReason.PERMISSION_DENIED) },
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

    val fakeImageStorage = object : com.cret.inoutmanager.domain.repository.ProductImageStorage {
        override fun createTemporaryFile() = java.io.File.createTempFile("preview", ".jpg")
        override fun commit(temporaryFile: java.io.File) = temporaryFile
        override fun importTemporaryFile(input: java.io.InputStream) = java.io.File.createTempFile("preview", ".jpg")
        override fun isUsableManagedImage(file: java.io.File) = file.exists()
        override fun delete(file: java.io.File) = true
    }

    val fakeUseCases = ProductUseCases(
        getProducts = GetProductsUseCase(fakeRepository),
        addProduct = AddProductUseCase(fakeRepository, fakeImageStorage),
        decreaseProductQuantity = DecreaseProductQuantityUseCase(fakeRepository),
        deleteProduct = DeleteProductUseCase(fakeRepository, fakeImageStorage),
        createTemporaryProductImage = CreateTemporaryProductImageUseCase(fakeImageStorage),
        discardProductImage = DiscardProductImageUseCase(fakeImageStorage),
        importProductImage = com.cret.inoutmanager.domain.usecase.ImportProductImageUseCase(fakeImageStorage),
        attachProductImage = com.cret.inoutmanager.domain.usecase.AttachProductImageUseCase(fakeRepository, fakeImageStorage),
        removeProductImage = com.cret.inoutmanager.domain.usecase.RemoveProductImageUseCase(fakeRepository, fakeImageStorage),
    )

    val noOpAnalyticsLogger = com.cret.inoutmanager.analytics.AnalyticsLogger { }

    val noOpPhotoCaptureReporter = object : com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter {
        override fun setState(state: com.cret.inoutmanager.reporting.CaptureState) {}
        override fun setFailureReason(reason: com.cret.inoutmanager.reporting.CaptureFailureReason) {}
        override fun reset() {}
    }

    val fakeViewModel = InventoryViewModel(fakeUseCases, noOpAnalyticsLogger, noOpPhotoCaptureReporter)

    InOutManagerTheme {
        InventoryApp(viewModel = fakeViewModel)
    }
}
