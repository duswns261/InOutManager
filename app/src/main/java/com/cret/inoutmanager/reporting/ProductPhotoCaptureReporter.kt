package com.cret.inoutmanager.reporting

/**
 * 제품 촬영 흐름 상태를 SDK 독립적으로 기록하는 계약입니다.
 * 구현체는 [com.cret.inoutmanager.reporting.firebase] 패키지로 격리합니다.
 */
interface ProductPhotoCaptureReporter {
    fun setState(state: CaptureState)
    fun setFailureReason(reason: CaptureFailureReason)

    /** 촬영 흐름 종료 시 상태/실패 사유를 초기값으로 되돌립니다. */
    fun reset()
}

/** allowlist 기반 촬영 상태 값입니다. */
enum class CaptureState(val value: String) {
    IDLE("idle"),
    PREVIEW_ACTIVE("preview_active"),
    CAPTURED("captured"),
    FAILED("failed"),
}

/** allowlist 기반 촬영 실패 사유 값입니다. */
enum class CaptureFailureReason(val value: String) {
    NONE("none"),
    PERMISSION_DENIED("permission_denied"),
    CAPTURE_ERROR("capture_error"),
    SAVE_ERROR("save_error"),
}
