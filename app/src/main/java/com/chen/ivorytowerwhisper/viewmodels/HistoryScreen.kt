package com.chen.ivorytowerwhisper.viewmodels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chen.ivorytowerwhisper.model.EmotionHistory
import com.chen.ivorytowerwhisper.ui.theme.EmotionAngry
import com.chen.ivorytowerwhisper.ui.theme.EmotionAnxious
import com.chen.ivorytowerwhisper.ui.theme.EmotionCalm
import com.chen.ivorytowerwhisper.ui.theme.EmotionHappy
import com.chen.ivorytowerwhisper.ui.theme.EmotionSad
import com.chen.ivorytowerwhisper.ui.theme.Purple40
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val viewModel: EmotionViewModel = viewModel()
    val history by remember { derivedStateOf { viewModel.history } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("情绪历史记录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        enabled = history.isNotEmpty()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "清除历史")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (history.isEmpty()) {
            /*Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无历史记录", style = MaterialTheme.typography.bodyLarge)
            }*/
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "空历史记录",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "暂无分析记录",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "完成一次情绪分析后记录将显示在这里",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
        } }else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(history) { item ->
                    HistoryItem(item)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(item: EmotionHistory) {
    val emotionColor = when (item.emotion) {
        "快乐" -> EmotionHappy
        "平静" -> EmotionCalm
        "悲伤" -> EmotionSad
        "愤怒" -> EmotionAngry
        "焦虑" -> EmotionAnxious
        else -> Purple40
    }

    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = emotionColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.emotion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = emotionColor
                )
                Text(
                    text = "${(item.score * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateString,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}