package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billing.BillingManager
import com.example.ui.HairStyleApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.HairstyleViewModel

class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        // Subscriptions can be canceled, renewed or refunded while the app is in the background
        BillingManager.refreshPurchases()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HairstyleViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBackground
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = 640.dp)
                        ) {
                            HairStyleApp(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

