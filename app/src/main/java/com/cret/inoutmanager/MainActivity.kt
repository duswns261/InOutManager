package com.cret.inoutmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cret.inoutmanager.presentation.ui.InventoryApp
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InOutManagerTheme {
                InventoryApp()
            }
        }
    }
}
