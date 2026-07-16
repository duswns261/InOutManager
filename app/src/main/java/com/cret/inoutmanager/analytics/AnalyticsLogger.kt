package com.cret.inoutmanager.analytics

/**
 * Presentation이 의존하는 SDK 독립적인 analytics 기록 계약입니다.
 * 구현체는 [com.cret.inoutmanager.analytics.firebase] 패키지로 격리합니다.
 */
fun interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
}
