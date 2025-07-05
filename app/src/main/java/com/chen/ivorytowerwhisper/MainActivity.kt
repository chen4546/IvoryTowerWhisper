package com.chen.ivorytowerwhisper

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chen.ivorytowerwhisper.ui.theme.IvoryTowerWhisperTheme
import com.chen.ivorytowerwhisper.viewmodels.EmotionViewModel
import com.chen.ivorytowerwhisper.viewmodels.ITWhisper
import com.chen.ivorytowerwhisper.viewmodels.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IvoryTowerWhisperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ITWhisper(innerPadding = innerPadding)
                }
            }
        }
    }
}
// 提供ViewModel工厂
@Composable
fun emotionViewModel(): EmotionViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EmotionViewModel(context.applicationContext as Application) as T
            }
        }
    )
}

@Composable
fun loginViewModel(): LoginViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(context.applicationContext as Application) as T
            }
        }
    )
}