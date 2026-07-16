package com.cret.inoutmanager.analytics.firebase

import android.os.Bundle
import com.cret.inoutmanager.analytics.AnalyticsEvent
import com.cret.inoutmanager.analytics.AnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * [AnalyticsEvent]를 Firebase SDK 호출로 변환하는 유일한 adapter입니다.
 * 다른 파일에서는 [FirebaseAnalytics]와 [Bundle]을 직접 참조하지 않습니다.
 */
class FirebaseAnalyticsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        val bundle = Bundle()
        event.params.forEach { (key, value) -> bundle.putString(key, value) }
        firebaseAnalytics.logEvent(event.name, bundle)
    }
}
