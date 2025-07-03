package com.chen.ivorytowerwhisper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chen.ivorytowerwhisper.ui.theme.IvoryTowerWhisperTheme
import com.chen.ivorytowerwhisper.viewmodels.ITWhisper

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
