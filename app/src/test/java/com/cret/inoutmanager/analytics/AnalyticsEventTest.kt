package com.cret.inoutmanager.analytics

import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsEventTest {

    @Test
    fun `product registration started has inbound app bar entry point`() {
        val event = AnalyticsEvent.ProductRegistrationStarted(entryPoint = EntryPoint.INBOUND_APP_BAR)

        assertEquals("product_registration_started", event.name)
        assertEquals(mapOf("entry_point" to "inbound_app_bar"), event.params)
    }

    @Test
    fun `product created without image has has_image false and quantity range`() {
        val event = AnalyticsEvent.ProductCreated(quantity = 5, hasImage = false)

        assertEquals("product_created", event.name)
        assertEquals(
            mapOf("has_image" to "false", "quantity_range" to "1_10"),
            event.params,
        )
    }

    @Test
    fun `product created with image has has_image true and quantity range`() {
        val event = AnalyticsEvent.ProductCreated(quantity = 5, hasImage = true)

        assertEquals("product_created", event.name)
        assertEquals(
            mapOf("has_image" to "true", "quantity_range" to "1_10"),
            event.params,
        )
    }

    @Test
    fun `product photo capture started has no parameters`() {
        val event = AnalyticsEvent.ProductPhotoCaptureStarted

        assertEquals("product_photo_capture_started", event.name)
        assertEquals(emptyMap<String, String>(), event.params)
    }

    @Test
    fun `product photo capture completed has no parameters`() {
        val event = AnalyticsEvent.ProductPhotoCaptureCompleted

        assertEquals("product_photo_capture_completed", event.name)
        assertEquals(emptyMap<String, String>(), event.params)
    }

    @Test
    fun `product photo capture failed has allowlisted failure reason`() {
        val event = AnalyticsEvent.ProductPhotoCaptureFailed(reason = PhotoCaptureFailureReason.PERMISSION_DENIED)

        assertEquals("product_photo_capture_failed", event.name)
        assertEquals(mapOf("failure_reason" to "permission_denied"), event.params)
    }

    @Test
    fun `outbound started has no parameters`() {
        val event = AnalyticsEvent.OutboundStarted

        assertEquals("outbound_started", event.name)
        assertEquals(emptyMap<String, String>(), event.params)
    }

    @Test
    fun `outbound completed has quantity range`() {
        val event = AnalyticsEvent.OutboundCompleted(quantity = 30)

        assertEquals("outbound_completed", event.name)
        assertEquals(mapOf("quantity_range" to "11_50"), event.params)
    }

    @Test
    fun `product deleted has no parameters`() {
        val event = AnalyticsEvent.ProductDeleted

        assertEquals("product_deleted", event.name)
        assertEquals(emptyMap<String, String>(), event.params)
    }

    @Test
    fun `inventory screen viewed has no parameters`() {
        val event = AnalyticsEvent.InventoryScreenViewed

        assertEquals("inventory_screen_viewed", event.name)
        assertEquals(emptyMap<String, String>(), event.params)
    }

    @Test
    fun `quantity range boundaries match spec`() {
        assertEquals(QuantityRange.ZERO, QuantityRange.from(0))
        assertEquals(QuantityRange.SMALL, QuantityRange.from(1))
        assertEquals(QuantityRange.SMALL, QuantityRange.from(10))
        assertEquals(QuantityRange.MEDIUM, QuantityRange.from(11))
        assertEquals(QuantityRange.MEDIUM, QuantityRange.from(50))
        assertEquals(QuantityRange.LARGE, QuantityRange.from(51))
    }
}
