package com.cret.inoutmanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.cret.inoutmanger.data.database.AppDatabase
import com.cret.inoutmanger.data.repository.ProductRepository
import com.cret.inoutmanger.presentation.ui.InventoryApp
import com.cret.inoutmanger.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanger.ui.theme.InOutMangerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이 코드 구조는 DB -> Repository -> ViewModelFactory -> ViewModel 흐름이 한 파일에 보여서 의존성 연결 구조를 이해하기 쉬움(학습용)
        // 실무 관점에서의 아쉬움 존재, MainActivity의 책임이 너무 크고 재사용성이 낮으며 테스트가 불편하거나 의존성이 숨겨진다.
        // Android 공식 문서에서도 앱을 UI Layer, Data Layer로 나누고, 의존성 주입 방향으로 권장.

        // 1. 데이터베이스 생성 (이름: inventory-db)
        // 앱 전체에서 한 번만 만들 수 있도록 통로를 지정하기 위해 AppDatabase내부 getDatabase(context)함수 호출을 통해 singleton 패턴으로 디자인
        val db = Room.databaseBuilder(
            applicationContext, //ApplicationContext는 앱의 라이프 사이클과 연결되어 있으며 데이터베이스 역시 앱의 생성 기간 동안 형태를 유지해야 하기 때문에 applicationContext 활용
            AppDatabase::class.java,
            "inventory-db"
        ).build()

        // 2. Repository 생성 (DAO를 넣어줌)
        val repository = ProductRepository(db.productDao())

        // 3. ViewModel 생성 공장(Factory) 만들기
        // (ViewModel이 Dao를 필요로 하므로 수동으로 만들어줘야 함)
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return InventoryViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // 4. 뷰모델 가져오기
        val viewModel = ViewModelProvider(this, viewModelFactory)[InventoryViewModel::class.java]

        setContent {
            InOutMangerTheme {
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