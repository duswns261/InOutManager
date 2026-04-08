package com.cret.inoutmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cret.inoutmanager.di.AppContainer
import com.cret.inoutmanager.presentation.ui.InventoryApp
import com.cret.inoutmanager.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanager.ui.theme.InOutManagerTheme

//질문, ViewModelFactory + ViewModelOwner + ViewModelProvider 에 대한 학습 필요
//질문, appContainer 객체 생성에 대한 이해 필요

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer
        get() = (application as InOutManagerApplication).container

//    private val factory = InventoryViewModelFactory(
//        (appContainer as InOutManagerApplication).container.productRepository

    // factory 를 viewModels { ... } 에 넘기는 구조
    // InventoryViewModel class 구조 상, InventoryViewModelFactory class 는 ProductRepository 객체를 갖기 때문에
    // factory 가 필요함
    // 아무 설정이 없으면 매개변수 없는 ViewModel 만 기본 생성하려고 함.
//    private val viewModel: InventoryViewModel by viewModels {
//            InventoryViewModelFactory(
////                appContainer.productRepository
//                (application as InOutManagerApplication).container.productRepository
//            )
//    }

    // ViewModel 은 UI Layer 에서 비즈니스 로직을 처리하고, 다른 계층으로 이벤트를 위임하는 역할을 가짐
    // UI 상태를 보여주고, 이벤트를 전달하는데 집중. 그래서 viewModel 객체 생성은 Activity 에서 진행
//    private val viewModel: InventoryViewModel by viewModels { factory }

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.provideFactory(appContainer.productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 데이터베이스 생성 (이름: inventory-db)
//        val db = Room.databaseBuilder(
//            applicationContext, //ApplicationContext는 앱의 라이프 사이클과 연결되어 있으며 데이터베이스 역시 앱의 생성 기간 동안 형태를 유지해야 하기 때문에 applicationContext 활용
//            AppDatabase::class.java,
//            "inventory-db"
//        ).build()
        // MainActivity -> AppDatabase 로 이동, AppDatabase class 의 주석 참고

        // 2. Repository 생성 (DAO를 넣어줌)
//        val repository = ProductRepository(AppDatabase.getDatabase(this).productDao())
//        val repository = ProductRepository(db.productDao())

        // 3. ViewModel 생성 공장(Factory) 만들기
        // (ViewModel이 Dao를 필요로 하므로 수동으로 만들어줘야 함)

//        container = DefaultAppContainer(this)
//
//        val viewModelFactory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
//                    @Suppress("UNCHECKED_CAST")
//                    return InventoryViewModel(container.productRepository) as T
//                }
//                throw IllegalArgumentException("Unknown ViewModel class")
//            }
//        }
//
//        // 4. 뷰모델 가져오기
//        val viewModel = ViewModelProvider(this, viewModelFactory)[InventoryViewModel::class.java]

        setContent {
            InOutManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // 4. 생성한 ViewModel을 앱에 전달
                    InventoryApp(viewModel = viewModel)
                }
            }
        }
    }
}

// 정리된 구조[책임 분리]
// - AppDatabase: DB Singleton 제공
// - AppContainer, Application: Repository 생성 및 보관
// - MainActivity: ViewModel 만 획득, UI 실행