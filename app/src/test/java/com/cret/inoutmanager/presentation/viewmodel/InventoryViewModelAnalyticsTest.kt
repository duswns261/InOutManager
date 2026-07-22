package com.cret.inoutmanager.presentation.viewmodel

import com.cret.inoutmanager.MainDispatcherRule
import com.cret.inoutmanager.analytics.AnalyticsEvent
import com.cret.inoutmanager.analytics.AnalyticsLogger
import com.cret.inoutmanager.analytics.EntryPoint
import com.cret.inoutmanager.analytics.PhotoCaptureFailureReason
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import com.cret.inoutmanager.domain.usecase.AddProductUseCase
import com.cret.inoutmanager.domain.usecase.AttachProductImageUseCase
import com.cret.inoutmanager.domain.usecase.CreateTemporaryProductImageUseCase
import com.cret.inoutmanager.domain.usecase.DecreaseProductQuantityUseCase
import com.cret.inoutmanager.domain.usecase.DeleteProductUseCase
import com.cret.inoutmanager.domain.usecase.DiscardProductImageUseCase
import com.cret.inoutmanager.domain.usecase.GetProductsUseCase
import com.cret.inoutmanager.domain.usecase.ImportProductImageUseCase
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import com.cret.inoutmanager.domain.usecase.RemoveProductImageUseCase
import com.cret.inoutmanager.presentation.model.ProductImageOrigin
import com.cret.inoutmanager.reporting.CaptureFailureReason
import com.cret.inoutmanager.reporting.CaptureState
import com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.InputStream

class InventoryViewModelAnalyticsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeAnalyticsLogger : AnalyticsLogger {
        val loggedEvents = mutableListOf<AnalyticsEvent>()
        override fun log(event: AnalyticsEvent) {
            loggedEvents += event
        }
    }

    private class FakeProductRepository(
        private val insertError: Throwable? = null,
        private val updateError: Throwable? = null,
        private val deleteError: Throwable? = null,
    ) : ProductRepository {
        override val allProducts: Flow<List<Product>> = flowOf(emptyList())

        override suspend fun insert(product: Product) {
            insertError?.let { throw it }
        }

        override suspend fun update(product: Product) {
            updateError?.let { throw it }
        }

        override suspend fun delete(product: Product) {
            deleteError?.let { throw it }
        }
    }

    private class FakeProductImageStorage : ProductImageStorage {
        val deleted = mutableListOf<File>()
        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")
        override fun commit(temporaryFile: File): File = temporaryFile
        override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
        override fun isUsableManagedImage(file: File): Boolean = file.exists()
        override fun delete(file: File) {
            deleted += file
        }
    }

    private class FakePhotoCaptureReporter : ProductPhotoCaptureReporter {
        val states = mutableListOf<CaptureState>()
        val failureReasons = mutableListOf<CaptureFailureReason>()
        var resetCount = 0
            private set

        override fun setState(state: CaptureState) {
            states += state
        }

        override fun setFailureReason(reason: CaptureFailureReason) {
            failureReasons += reason
        }

        override fun reset() {
            resetCount += 1
        }
    }

    private fun viewModel(
        repository: ProductRepository,
        logger: AnalyticsLogger,
        imageStorage: ProductImageStorage = FakeProductImageStorage(),
        reporter: ProductPhotoCaptureReporter = FakePhotoCaptureReporter(),
    ): InventoryViewModel {
        val useCases = ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            addProduct = AddProductUseCase(repository, imageStorage),
            decreaseProductQuantity = DecreaseProductQuantityUseCase(repository),
            deleteProduct = DeleteProductUseCase(repository, imageStorage),
            createTemporaryProductImage = CreateTemporaryProductImageUseCase(imageStorage),
            discardProductImage = DiscardProductImageUseCase(imageStorage),
            importProductImage = ImportProductImageUseCase(imageStorage),
            attachProductImage = AttachProductImageUseCase(repository, imageStorage),
            removeProductImage = RemoveProductImageUseCase(repository, imageStorage),
        )
        return InventoryViewModel(useCases, logger, reporter)
    }

    @Test
    fun `addProduct success without image logs product_created with has_image false`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5")

        assertEquals(listOf(AnalyticsEvent.ProductCreated(quantity = 5, hasImage = false)), logger.loggedEvents)
    }

    @Test
    fun `addProduct failure does not log product_created`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(insertError = RuntimeException("insert failed")), logger)

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5")

        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `addProduct success with image logs product_created with has_image true`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)
        val imageFile = File.createTempFile("test", ".jpg")

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5", imageFile = imageFile)

        assertEquals(listOf(AnalyticsEvent.ProductCreated(quantity = 5, hasImage = true)), logger.loggedEvents)
    }

    @Test
    fun `addProduct success calls onResult with true`() = runTest {
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger())
        var result: Boolean? = null

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5", onResult = { result = it })

        assertEquals(true, result)
    }

    @Test
    fun `addProduct failure with image logs SAVE_ERROR to reporter and analytics`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(
            FakeProductRepository(insertError = RuntimeException("insert failed")),
            logger,
            reporter = reporter,
        )
        val imageFile = File.createTempFile("test", ".jpg")
        var result: Boolean? = null

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5", imageFile = imageFile, onResult = { result = it })

        assertEquals(false, result)
        assertEquals(
            listOf(AnalyticsEvent.ProductPhotoCaptureFailed(reason = PhotoCaptureFailureReason.SAVE_ERROR)),
            logger.loggedEvents,
        )
        assertEquals(listOf(CaptureState.FAILED), reporter.states)
        assertEquals(listOf(CaptureFailureReason.SAVE_ERROR), reporter.failureReasons)
    }

    @Test
    fun `addProduct failure without image does not log photo capture failure`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(
            FakeProductRepository(insertError = RuntimeException("insert failed")),
            logger,
            reporter = reporter,
        )

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5")

        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertTrue(reporter.states.isEmpty())
    }

    @Test
    fun `addProduct failure with picker image does not log photo capture failure`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(
            FakeProductRepository(insertError = RuntimeException("insert failed")),
            logger,
            reporter = reporter,
        )
        val imageFile = File.createTempFile("test", ".jpg")
        var result: Boolean? = null

        sut.addProduct(
            name = "펜",
            location = "A-1",
            quantityStr = "5",
            imageFile = imageFile,
            imageOrigin = ProductImageOrigin.PICKER,
            onResult = { result = it },
        )

        assertEquals(false, result)
        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertTrue(reporter.states.isEmpty())
    }

    @Test
    fun `importProductImage delegates to useCases and returns success`() = runTest {
        val imageStorage = FakeProductImageStorage()
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger(), imageStorage = imageStorage)
        val source = File.createTempFile("source", ".jpg").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        val latch = java.util.concurrent.CountDownLatch(1)
        var result: Result<File>? = null

        sut.importProductImage(
            openStream = { source.inputStream() },
            onResult = { result = it; latch.countDown() },
        )

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS))
        assertTrue(result?.isSuccess == true)
    }

    @Test
    fun `importProductImage failure returns failure result without changing ui state`() = runTest {
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger())
        val latch = java.util.concurrent.CountDownLatch(1)
        var result: Result<File>? = null

        sut.importProductImage(
            openStream = { throw java.io.IOException("open failed") },
            onResult = { result = it; latch.countDown() },
        )

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS))
        assertTrue(result?.isFailure == true)
        assertNotNull(sut.uiState.value)
    }

    @Test
    fun `attachProductImage success calls onResult with true and does not log photo capture events`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(FakeProductRepository(), logger, reporter = reporter)
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 5)
        val imageFile = File.createTempFile("test", ".jpg")
        var result: Boolean? = null

        sut.attachProductImage(
            product = product,
            temporaryImageFile = imageFile,
            imageOrigin = ProductImageOrigin.PICKER,
            onResult = { result = it },
        )

        assertEquals(true, result)
        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertTrue(reporter.states.isEmpty())
    }

    @Test
    fun `attachProductImage failure with camera origin logs SAVE_ERROR`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(
            FakeProductRepository(updateError = RuntimeException("update failed")),
            logger,
            reporter = reporter,
        )
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 5)
        val imageFile = File.createTempFile("test", ".jpg")
        var result: Boolean? = null

        sut.attachProductImage(
            product = product,
            temporaryImageFile = imageFile,
            imageOrigin = ProductImageOrigin.CAMERA,
            onResult = { result = it },
        )

        assertEquals(false, result)
        assertEquals(
            listOf(AnalyticsEvent.ProductPhotoCaptureFailed(reason = PhotoCaptureFailureReason.SAVE_ERROR)),
            logger.loggedEvents,
        )
        assertEquals(listOf(CaptureState.FAILED), reporter.states)
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `attachProductImage failure with picker origin does not log photo capture failure`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(
            FakeProductRepository(updateError = RuntimeException("update failed")),
            logger,
            reporter = reporter,
        )
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 5)
        val imageFile = File.createTempFile("test", ".jpg")
        var result: Boolean? = null

        sut.attachProductImage(
            product = product,
            temporaryImageFile = imageFile,
            imageOrigin = ProductImageOrigin.PICKER,
            onResult = { result = it },
        )

        assertEquals(false, result)
        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertTrue(reporter.states.isEmpty())
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `removeProductImage success calls onResult with true`() = runTest {
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger())
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 5, imagePath = "/data/old.jpg")
        var result: Boolean? = null

        sut.removeProductImage(product, onResult = { result = it })

        assertEquals(true, result)
    }

    @Test
    fun `removeProductImage failure calls onResult with false and sets error message`() = runTest {
        val sut = viewModel(FakeProductRepository(updateError = RuntimeException("update failed")), FakeAnalyticsLogger())
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 5, imagePath = "/data/old.jpg")
        var result: Boolean? = null

        sut.removeProductImage(product, onResult = { result = it })

        assertEquals(false, result)
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `createTemporaryImageFile delegates to useCases and discardTemporaryImage deletes it`() = runTest {
        val imageStorage = FakeProductImageStorage()
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger(), imageStorage = imageStorage)

        val file = sut.createTemporaryImageFile()
        sut.discardTemporaryImage(file)

        assertEquals(listOf(file), imageStorage.deleted)
    }

    @Test
    fun `logPhotoCaptureStarted sets preview_active state and logs event`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(FakeProductRepository(), logger, reporter = reporter)

        sut.logPhotoCaptureStarted()

        assertEquals(listOf(CaptureState.PREVIEW_ACTIVE), reporter.states)
        assertEquals(listOf(AnalyticsEvent.ProductPhotoCaptureStarted), logger.loggedEvents)
    }

    @Test
    fun `logPhotoCaptureCompleted sets captured state and logs event`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(FakeProductRepository(), logger, reporter = reporter)

        sut.logPhotoCaptureCompleted()

        assertEquals(listOf(CaptureState.CAPTURED), reporter.states)
        assertEquals(listOf(AnalyticsEvent.ProductPhotoCaptureCompleted), logger.loggedEvents)
    }

    @Test
    fun `logPhotoCaptureFailed with permission denied sets failed state and reason`() = runTest {
        val logger = FakeAnalyticsLogger()
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(FakeProductRepository(), logger, reporter = reporter)

        sut.logPhotoCaptureFailed(PhotoCaptureFailureReason.PERMISSION_DENIED)

        assertEquals(listOf(CaptureState.FAILED), reporter.states)
        assertEquals(listOf(CaptureFailureReason.PERMISSION_DENIED), reporter.failureReasons)
        assertEquals(
            listOf(AnalyticsEvent.ProductPhotoCaptureFailed(reason = PhotoCaptureFailureReason.PERMISSION_DENIED)),
            logger.loggedEvents,
        )
    }

    @Test
    fun `resetPhotoCaptureReporting resets the reporter`() = runTest {
        val reporter = FakePhotoCaptureReporter()
        val sut = viewModel(FakeProductRepository(), FakeAnalyticsLogger(), reporter = reporter)

        sut.resetPhotoCaptureReporting()

        assertEquals(1, reporter.resetCount)
    }

    @Test
    fun `decreaseQuantity success logs outbound_completed with quantity range`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 20)

        sut.decreaseQuantity(product, amount = 15)

        assertEquals(listOf(AnalyticsEvent.OutboundCompleted(quantity = 15)), logger.loggedEvents)
    }

    @Test
    fun `decreaseQuantity failure does not log outbound_completed`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(updateError = RuntimeException("update failed")), logger)
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 20)

        sut.decreaseQuantity(product, amount = 15)

        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `deleteProduct success logs product_deleted`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 20)

        sut.deleteProduct(product)

        assertEquals(listOf(AnalyticsEvent.ProductDeleted), logger.loggedEvents)
    }

    @Test
    fun `deleteProduct failure does not log product_deleted`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(deleteError = RuntimeException("delete failed")), logger)
        val product = Product(id = 1, name = "펜", location = "A-1", quantity = 20)

        sut.deleteProduct(product)

        assertEquals(emptyList<AnalyticsEvent>(), logger.loggedEvents)
        assertNotNull(sut.uiState.value.errorMessage)
    }

    @Test
    fun `logProductRegistrationStarted logs event once with inbound fab entry point`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)

        sut.logProductRegistrationStarted()

        assertEquals(
            listOf(AnalyticsEvent.ProductRegistrationStarted(entryPoint = EntryPoint.INBOUND_FAB)),
            logger.loggedEvents,
        )
    }

    @Test
    fun `logOutboundStarted logs event once`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)

        sut.logOutboundStarted()

        assertEquals(listOf(AnalyticsEvent.OutboundStarted), logger.loggedEvents)
    }

    @Test
    fun `logInventoryScreenViewed logs event once`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)

        sut.logInventoryScreenViewed()

        assertEquals(listOf(AnalyticsEvent.InventoryScreenViewed), logger.loggedEvents)
    }
}
