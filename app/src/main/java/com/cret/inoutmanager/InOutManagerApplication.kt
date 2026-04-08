package com.cret.inoutmanager

import android.app.Application
import com.cret.inoutmanager.di.AppContainer
import com.cret.inoutmanager.di.DefaultAppContainer

class InOutManagerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = DefaultAppContainer(this)
    }
}

// Application은 Activity와 달리 앱 전체에서 사용되는 객체
// 프로세스가 실행될 때 첫 진입점인 MainActivity 보다 먼저 실행되기 때문에 안전하게 사용 가능.
// 프로세스가 살아있는 동안 비교적 오래 유지되는 상위 객체
// 이렇게 생성하게 되면 Activity는 더이상 DB, Repository 가 어떻게 생성되는지 알 필요가 없음. 책임 분리
// Hilt 적용 전 수동 DI 학습 구조에 적합 또는 작은 프로젝트에 적용하기 좋은 실무적 구조

// Application class 는 Application -> ContextWrapper -> Context 의 계층 구조를 가지며
// Activity -> ContextThemeWrapper -> ContextWrapper -> Context 계층 구조를 가진 Activity class와 달리 프로세스 단위 전역 상태를 유지하는 기반 클래스임