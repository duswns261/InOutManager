package com.cret.inoutmanager

import android.app.Application
import com.cret.inoutmanager.di.AppContainer
import com.cret.inoutmanager.di.DefaultAppContainer

/**
 * 앱 전역에서 사용할 의존성 컨테이너를 애플리케이션 생명주기에 맞춰 초기화합니다.
 */
class InOutManagerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = DefaultAppContainer(this)
    }
}
