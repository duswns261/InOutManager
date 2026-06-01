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

/**
 * 앱의 Compose UI를 띄우고, Application에 보관된 의존성으로 ViewModel을 생성합니다.
 */
class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer
        get() = (application as InOutManagerApplication).container

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.provideFactory(appContainer.productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InOutManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    InventoryApp(viewModel = viewModel)
                }
            }
        }
    }
}
