package com.cret.inoutmanager.analytics

/**
 * SDK 독립적인 typed analytics event 모델입니다.
 * Presentation은 이 sealed interface만 참조하며, event name/parameter 문자열을 직접 다루지 않습니다.
 */
sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, String>

    data class ProductRegistrationStarted(val entryPoint: EntryPoint) : AnalyticsEvent {
        override val name: String = NAME
        override val params: Map<String, String> = mapOf(PARAM_ENTRY_POINT to entryPoint.value)

        companion object {
            const val NAME = "product_registration_started"
        }
    }

    data class ProductCreated(val quantity: Int, val hasImage: Boolean) : AnalyticsEvent {
        override val name: String = NAME
        override val params: Map<String, String> = mapOf(
            PARAM_HAS_IMAGE to hasImage.toString(),
            PARAM_QUANTITY_RANGE to QuantityRange.from(quantity).value,
        )

        companion object {
            const val NAME = "product_created"
        }
    }

    data object OutboundStarted : AnalyticsEvent {
        override val name: String = "outbound_started"
        override val params: Map<String, String> = emptyMap()
    }

    data class OutboundCompleted(val quantity: Int) : AnalyticsEvent {
        override val name: String = NAME
        override val params: Map<String, String> = mapOf(PARAM_QUANTITY_RANGE to QuantityRange.from(quantity).value)

        companion object {
            const val NAME = "outbound_completed"
        }
    }

    data object ProductDeleted : AnalyticsEvent {
        override val name: String = "product_deleted"
        override val params: Map<String, String> = emptyMap()
    }

    data object InventoryScreenViewed : AnalyticsEvent {
        override val name: String = "inventory_screen_viewed"
        override val params: Map<String, String> = emptyMap()
    }

    data object ProductPhotoCaptureStarted : AnalyticsEvent {
        override val name: String = "product_photo_capture_started"
        override val params: Map<String, String> = emptyMap()
    }

    data object ProductPhotoCaptureCompleted : AnalyticsEvent {
        override val name: String = "product_photo_capture_completed"
        override val params: Map<String, String> = emptyMap()
    }

    data class ProductPhotoCaptureFailed(val reason: PhotoCaptureFailureReason) : AnalyticsEvent {
        override val name: String = NAME
        override val params: Map<String, String> = mapOf(PARAM_FAILURE_REASON to reason.value)

        companion object {
            const val NAME = "product_photo_capture_failed"
        }
    }

    companion object {
        const val PARAM_ENTRY_POINT = "entry_point"
        const val PARAM_HAS_IMAGE = "has_image"
        const val PARAM_QUANTITY_RANGE = "quantity_range"
        const val PARAM_FAILURE_REASON = "failure_reason"
    }
}

/** `product_photo_capture_failed`의 `failure_reason` 허용 값입니다. */
enum class PhotoCaptureFailureReason(val value: String) {
    PERMISSION_DENIED("permission_denied"),
    CAPTURE_ERROR("capture_error"),
    SAVE_ERROR("save_error"),
}

/** `product_registration_started`의 `entry_point` 허용 값입니다. */
enum class EntryPoint(val value: String) {
    INBOUND_APP_BAR("inbound_app_bar"),
}

/** 개인정보 없이 수량을 구간화해 전달하기 위한 `quantity_range` 허용 값입니다. */
enum class QuantityRange(val value: String) {
    ZERO("zero"),
    SMALL("1_10"),
    MEDIUM("11_50"),
    LARGE("51_plus");

    companion object {
        fun from(quantity: Int): QuantityRange = when {
            quantity <= 0 -> ZERO
            quantity <= 10 -> SMALL
            quantity <= 50 -> MEDIUM
            else -> LARGE
        }
    }
}
