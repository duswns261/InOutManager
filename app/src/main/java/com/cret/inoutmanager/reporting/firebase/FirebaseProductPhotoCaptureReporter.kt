package com.cret.inoutmanager.reporting.firebase

import com.cret.inoutmanager.reporting.CaptureFailureReason
import com.cret.inoutmanager.reporting.CaptureState
import com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * [ProductPhotoCaptureReporter]를 Crashlytics Custom Key 호출로 변환하는 유일한 adapter입니다.
 * 다른 파일에서는 [FirebaseCrashlytics]를 직접 참조하지 않습니다.
 */
class FirebaseProductPhotoCaptureReporter(
    private val crashlytics: FirebaseCrashlytics,
) : ProductPhotoCaptureReporter {

    override fun setState(state: CaptureState) {
        crashlytics.setCustomKey(KEY_STATE, state.value)
    }

    override fun setFailureReason(reason: CaptureFailureReason) {
        crashlytics.setCustomKey(KEY_FAILURE_REASON, reason.value)
    }

    override fun reset() {
        setState(CaptureState.IDLE)
        setFailureReason(CaptureFailureReason.NONE)
    }

    companion object {
        private const val KEY_STATE = "product_photo_capture_state"
        private const val KEY_FAILURE_REASON = "product_photo_failure_reason"
    }
}
