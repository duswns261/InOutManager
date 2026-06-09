package com.cret.inoutmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cret.inoutmanager.presentation.ui.InventoryApp
import com.cret.inoutmanager.presentation.viewmodel.InventoryViewModel
import com.cret.inoutmanager.ui.theme.InOutManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as InOutManagerApplication).container
            InOutManagerTheme {
                InventoryApp(
                    viewModel = viewModel(
                        factory = InventoryViewModel.provideFactory(appContainer.productUseCases)
                    )
                )
            }
        }
    }
}
