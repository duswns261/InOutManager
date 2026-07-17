package com.cret.inoutmanager.presentation.viewmodel

import com.cret.inoutmanager.MainDispatcherRule
import com.cret.inoutmanager.analytics.AnalyticsEvent
import com.cret.inoutmanager.analytics.AnalyticsLogger
import com.cret.inoutmanager.analytics.EntryPoint
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import com.cret.inoutmanager.domain.usecase.AddProductUseCase
import com.cret.inoutmanager.domain.usecase.CreateTemporaryProductImageUseCase
import com.cret.inoutmanager.domain.usecase.DecreaseProductQuantityUseCase
import com.cret.inoutmanager.domain.usecase.DeleteProductUseCase
import com.cret.inoutmanager.domain.usecase.DiscardProductImageUseCase
import com.cret.inoutmanager.domain.usecase.GetProductsUseCase
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.io.File

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
        override fun delete(file: File) {
            deleted += file
        }
    }

    private fun viewModel(
        repository: ProductRepository,
        logger: AnalyticsLogger,
        imageStorage: ProductImageStorage = FakeProductImageStorage(),
    ): InventoryViewModel {
        val useCases = ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            addProduct = AddProductUseCase(repository, imageStorage),
            decreaseProductQuantity = DecreaseProductQuantityUseCase(repository),
            deleteProduct = DeleteProductUseCase(repository),
            createTemporaryProductImage = CreateTemporaryProductImageUseCase(imageStorage),
            discardProductImage = DiscardProductImageUseCase(imageStorage),
        )
        return InventoryViewModel(useCases, logger)
    }

    @Test
    fun `addProduct success logs product_created with quantity range`() = runTest {
        val logger = FakeAnalyticsLogger()
        val sut = viewModel(FakeProductRepository(), logger)

        sut.addProduct(name = "펜", location = "A-1", quantityStr = "5")

        assertEquals(listOf(AnalyticsEvent.ProductCreated(quantity = 5)), logger.loggedEvents)
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
