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

        // 1. 데이터베이스 생성 (이름: inventory-db)
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