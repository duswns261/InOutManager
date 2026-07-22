package com.cret.inoutmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanager.analytics.AnalyticsEvent
import com.cret.inoutmanager.analytics.AnalyticsLogger
import com.cret.inoutmanager.analytics.EntryPoint
import com.cret.inoutmanager.analytics.PhotoCaptureFailureReason
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import com.cret.inoutmanager.reporting.CaptureFailureReason
import com.cret.inoutmanager.reporting.CaptureState
import com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * 재고 화면의 상태를 보관하고, UI 이벤트를 UseCase 작업으로 변환합니다.
 * StateFlow를 사용하여 UI 상태를 단방향(UDF)으로 관리합니다.
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val useCases: ProductUseCases,
    private val analyticsLogger: AnalyticsLogger,
    private val photoCaptureReporter: ProductPhotoCaptureReporter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState(isLoading = true))
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.getProducts()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { products ->
                    _uiState.update { it.copy(products = products, isLoading = false, errorMessage = null) }
                }
        }
    }

    // --- 비즈니스 로직 호출 ---

    fun addProduct(
        name: String,
        location: String,
        quantityStr: String,
        imageFile: File? = null,
        imageOrigin: ProductImageOrigin = ProductImageOrigin.CAMERA,
        onResult: (Boolean) -> Unit = {},
    ) {
        val qty = quantityStr.toIntOrNull() ?: 0
        viewModelScope.launch {
            try {
                useCases.addProduct(name, location, qty, imageFile)
                analyticsLogger.log(AnalyticsEvent.ProductCreated(quantity = qty, hasImage = imageFile != null))
                onResult(true)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
                if (imageFile != null && imageOrigin == ProductImageOrigin.CAMERA) {
                    logPhotoCaptureFailed(PhotoCaptureFailureReason.SAVE_ERROR)
                }
                onResult(false)
            }
        }
    }

    // --- 촬영 임시 파일 관리 ---

    fun createTemporaryImageFile(): File = useCases.createTemporaryProductImage()

    fun discardTemporaryImage(file: File) {
        viewModelScope.launch {
            useCases.discardProductImage(file)
        }
    }

    // --- 제품 이미지 선택(Photo Picker) / 기존 제품 이미지 추가·교체 ---

    /**
     * Photo Picker 등이 제공한 일회성 스트림 공급자를 열어 관리 대상 임시 파일로 가져옵니다.
     * 실패해도 UI 상태를 바꾸지 않고 [onResult]로만 결과를 돌려줍니다(기존 선택/입력 유지는 호출부 책임).
     */
    fun importProductImage(openStream: () -> InputStream, onResult: (Result<File>) -> Unit) {
        viewModelScope.launch {
            val result = try {
                Result.success(useCases.importProductImage(openStream))
            } catch (e: Exception) {
                Result.failure(e)
            }
            onResult(result)
        }
    }

    /**
     * 기존 제품에 새 이미지를 최초로 추가하거나 교체합니다.
     * 저장 실패에는 카메라 출처일 때만 기존 SAVE_ERROR 촬영 실패 reporting을 적용합니다.
     */
    fun attachProductImage(
        product: Product,
        temporaryImageFile: File,
        imageOrigin: ProductImageOrigin,
        onResult: (Boolean) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                useCases.attachProductImage(product, temporaryImageFile)
                onResult(true)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
                if (imageOrigin == ProductImageOrigin.CAMERA) {
                    logPhotoCaptureFailed(PhotoCaptureFailureReason.SAVE_ERROR)
                }
                onResult(false)
            }
        }
    }

    /**
     * 기존 제품의 이미지를 독립적으로 제거해 이미지 없는 상태로 되돌립니다.
     * DB update 실패 시 기존 이미지를 그대로 유지합니다.
     */
    fun removeProductImage(product: Product, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                useCases.removeProductImage(product)
                onResult(true)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
                onResult(false)
            }
        }
    }

    // --- 촬영 흐름 Analytics/Crashlytics ---

    fun logPhotoCaptureStarted() {
        photoCaptureReporter.setState(CaptureState.PREVIEW_ACTIVE)
        analyticsLogger.log(AnalyticsEvent.ProductPhotoCaptureStarted)
    }

    fun logPhotoCaptureCompleted() {
        photoCaptureReporter.setState(CaptureState.CAPTURED)
        analyticsLogger.log(AnalyticsEvent.ProductPhotoCaptureCompleted)
    }

    fun logPhotoCaptureFailed(reason: PhotoCaptureFailureReason) {
        photoCaptureReporter.setState(CaptureState.FAILED)
        photoCaptureReporter.setFailureReason(reason.toReportingReason())
        analyticsLogger.log(AnalyticsEvent.ProductPhotoCaptureFailed(reason))
    }

    /** 등록 다이얼로그가 닫힐 때(취소 또는 등록 성공) 촬영 상태 key를 초기화합니다. */
    fun resetPhotoCaptureReporting() {
        photoCaptureReporter.reset()
    }

    private fun PhotoCaptureFailureReason.toReportingReason(): CaptureFailureReason = when (this) {
        PhotoCaptureFailureReason.PERMISSION_DENIED -> CaptureFailureReason.PERMISSION_DENIED
        PhotoCaptureFailureReason.CAPTURE_ERROR -> CaptureFailureReason.CAPTURE_ERROR
        PhotoCaptureFailureReason.SAVE_ERROR -> CaptureFailureReason.SAVE_ERROR
    }

    fun decreaseQuantity(targetProduct: Product, amount: Int) {
        viewModelScope.launch {
            try {
                useCases.decreaseProductQuantity(targetProduct, amount)
                analyticsLogger.log(AnalyticsEvent.OutboundCompleted(quantity = amount))
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                useCases.deleteProduct(product)
                analyticsLogger.log(AnalyticsEvent.ProductDeleted)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // --- Analytics 시작/화면 이벤트 ---

    fun logProductRegistrationStarted() {
        analyticsLogger.log(AnalyticsEvent.ProductRegistrationStarted(entryPoint = EntryPoint.INBOUND_FAB))
    }

    fun logOutboundStarted() {
        analyticsLogger.log(AnalyticsEvent.OutboundStarted)
    }

    fun logInventoryScreenViewed() {
        analyticsLogger.log(AnalyticsEvent.InventoryScreenViewed)
    }
}
