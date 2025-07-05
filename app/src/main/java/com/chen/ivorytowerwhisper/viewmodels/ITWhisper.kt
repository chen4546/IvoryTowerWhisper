package com.chen.ivorytowerwhisper.viewmodels

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chen.ivorytowerwhisper.R
import com.chen.ivorytowerwhisper.data.local.LocalStorage
import com.chen.ivorytowerwhisper.model.EmotionHistory
import com.chen.ivorytowerwhisper.model.EmotionResult
import com.chen.ivorytowerwhisper.model.EmotionState
import com.chen.ivorytowerwhisper.model.Screen
import com.chen.ivorytowerwhisper.model.UserPreferences
import com.chen.ivorytowerwhisper.ui.theme.Blue40
import com.chen.ivorytowerwhisper.ui.theme.Blue80
import com.chen.ivorytowerwhisper.ui.theme.EmotionAngry
import com.chen.ivorytowerwhisper.ui.theme.EmotionAnxious
import com.chen.ivorytowerwhisper.ui.theme.EmotionCalm
import com.chen.ivorytowerwhisper.ui.theme.EmotionHappy
import com.chen.ivorytowerwhisper.ui.theme.EmotionSad
import com.chen.ivorytowerwhisper.ui.theme.Green40
import com.chen.ivorytowerwhisper.ui.theme.Green80
import com.chen.ivorytowerwhisper.ui.theme.IvoryTowerWhisperTheme
import com.chen.ivorytowerwhisper.ui.theme.Purple40
import com.chen.ivorytowerwhisper.ui.theme.Purple80
import com.chen.ivorytowerwhisper.ui.theme.Red40
import com.chen.ivorytowerwhisper.ui.theme.Red80


@Composable
fun ITWhisper(innerPadding: androidx.compose.foundation.layout.PaddingValues) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var apiKey by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isDarkTheme by remember { mutableStateOf(false) } // 添加深色模式状态
    // 从本地加载保存的凭证
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val prefs = LocalStorage.getUserPreferences(context)
        if (prefs != null) {
            apiKey = prefs.apiKey
            username = prefs.username
            currentScreen = Screen.Analysis
        }
        // 检查系统是否处于深色模式
        val uiMode = context.resources.configuration.uiMode
        isDarkTheme = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    IvoryTowerWhisperTheme(darkTheme = isDarkTheme) {Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.Login -> LoginScreen(
                onLoginSuccess = { key, user ->
                    apiKey = key
                    username = user
                    // 保存用户凭证
                    LocalStorage.saveUserPreferences(context, UserPreferences(key, user))
                    currentScreen = Screen.Analysis
                },
                onNavigateToHistory = { currentScreen = Screen.History }
            )

            Screen.Analysis -> AnalysisScreen(
                apiKey = apiKey,
                username = username,
                onNavigateToHistory = { currentScreen = Screen.History },
                onBackToLogin = {
                    LocalStorage.clearUserPreferences(context)
                    apiKey = ""
                    username = ""
                    currentScreen = Screen.Login
                },
                toggleTheme = { isDarkTheme = !isDarkTheme } // 添加主题切换回调
            )

            Screen.History -> HistoryScreen(
                onBack = {

                    currentScreen = Screen.Analysis
                }
            )
        }
    }
}
}

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit,
                onNavigateToHistory: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    // 使用新的LoginViewModel
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    // 加载保存的凭证
    LaunchedEffect(Unit) {
        loginViewModel.loadSavedCredentials()
    }
    // 当有保存的凭证时自动填充
    LaunchedEffect(loginState) {
        if (loginState is LoginViewModel.LoginState.SavedCredentials) {
            val saved = loginState as LoginViewModel.LoginState.SavedCredentials
            username = saved.username
            apiKey = saved.apiKey
        }
    }
    // 当登录成功时，触发回调
    LaunchedEffect(loginState) {
        if (loginState is LoginViewModel.LoginState.Success) {
            //loginViewModel.resetLoginState()
            onLoginSuccess(apiKey, username)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
    ) {
        // 背景装饰
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 50.dp)
                .size(200.dp)
                .background(
                    color = Blue40.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(100.dp)
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 100.dp, start = 50.dp)
                .size(150.dp)
                .background(
                    color = Green40.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(75.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 应用图标和标题
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = "App Icon",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "IvoryTower Whisper",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "AI心理健康助手",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 用户名输入框
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("昵称") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "昵称")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // API密钥输入框
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it
                                inputError=null},
                label = { Text("API密钥") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "API密钥")
                },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = "切换可见性")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                ),
                singleLine = true,
                isError = loginState is LoginViewModel.LoginState.Error
            )

            // 显示错误信息
            when (loginState) {
                is LoginViewModel.LoginState.Error -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (loginState as LoginViewModel.LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 登录按钮
            Button(
                onClick = {
                    if (username.isBlank() || apiKey.isBlank()) {
                        inputError = "用户名和API密钥不能为空"
                        //return@Button
                    }else {
                        inputError = null
                        loginViewModel.verifyApiKey(apiKey)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = loginState !is LoginViewModel.LoginState.Loading

            ) {
                when (loginState) {
                    is LoginViewModel.LoginState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    else -> {
                        Text("登录", fontSize = 18.sp)
                    }
                }
            }
            // 显示输入错误
            inputError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // 提示信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "提示",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "需要DeepSeek API密钥才能使用服务",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            /*// 在底部添加历史记录按钮
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("查看历史记录")
            }*/
        }
    }

}

@Composable
fun AnalysisScreen(
    apiKey: String,
    username: String,
    onNavigateToHistory: ()-> Unit,
    onBackToLogin: () -> Unit,
    toggleTheme:()-> Unit
) {
    // 创建 ViewModel 实例
    val viewModel: EmotionViewModel = viewModel()
    var inputText by remember { mutableStateOf("") }
    var inputErrorMessage by remember { mutableStateOf<String?>(null) }
    // 添加情境选择
    var selectedContext by remember { mutableStateOf("学习") }
    val contexts = listOf("学习", "社交", "宿舍", "考试", "恋爱", "家庭")
    // 初始化 ViewModel 的 API Key
    LaunchedEffect(apiKey) {
        viewModel.setApiKey(apiKey)
    }

    // 观察 ViewModel 的状态变化
    val emotionState by viewModel.emotionState.collectAsState()
    // 校园心理资源
    val campusResources = listOf(
        "心理热线: 400-161-9995",
        "校心理咨询: 021-12345678",
        "24小时援助: 010-82951332"
    )
    // 情绪类型对应的颜色
    val emotionColors = mapOf(
        "快乐" to EmotionHappy,
        "平静" to EmotionCalm,
        "悲伤" to EmotionSad,
        "愤怒" to EmotionAngry,
        "焦虑" to EmotionAnxious,
        "中性" to MaterialTheme.colorScheme.secondary,
        // 添加更多可能的情绪类型
        "兴奋" to EmotionHappy.copy(alpha = 0.8f),
        "压力" to EmotionAnxious,
        "恐惧" to Purple40,
        "失望" to MaterialTheme.colorScheme.tertiary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部栏
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Mood,
                    contentDescription = "心情分析",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "情绪分析",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                // 历史记录按钮
                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "历史记录",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onBackToLogin) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "退出登录",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
                //切换主题
                IconButton(onClick = toggleTheme){
                    Icon(
                        painter = painterResource(
                            if (isSystemInDarkTheme()) R.drawable.ic_sun else R.drawable.ic_moon
                        ),
                        contentDescription = "切换主题",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }
                Text(
                    text = username.take(8),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // 内容区域
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow {
                    items (contexts){ context ->
                        val isSelected=context ==selectedContext
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedContext = context }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ){
                            Text(
                                text = context,
                                color = if(isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 输入区域
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "分享你的感受",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            placeholder = { Text("今天我感觉...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (inputText.isBlank()) {
                                    inputErrorMessage = "请输入要分析的文本"
                                    return@Button
                                }

                                inputErrorMessage = null
                                viewModel.analyzeTextEmotion(inputText)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = emotionState !is EmotionState.Loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (emotionState is EmotionState.Loading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = "分析",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("分析情绪", fontSize = 16.sp)
                                }
                            }
                        }

                        // 显示输入错误
                        inputErrorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // 显示API错误
                        if (emotionState is EmotionState.Error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (emotionState as EmotionState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 结果展示区域
                when (emotionState) {
                    is EmotionState.Success -> {
                        val result = (emotionState as EmotionState.Success).result
                        EmotionResultCard(result, emotionColors)
                    }
                    is EmotionState.Error -> {
                        val error = (emotionState as EmotionState.Error).message
                        if (error.contains("401") || error.contains("403") ||
                            error.contains("未授权") || error.contains("无效") ||
                            error.contains("invalid_request_error") ||
                            error.contains("Authentication Fails")) {

                            // 显示API Key失效提示
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.padding(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = "API Key失效",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "API Key已失效或无效",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "请返回登录页面更新API Key",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                // 这里可以添加导航回登录页面的逻辑
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                            )
                                        ) {
                                            Text("返回登录页面")
                                        }
                                    }
                                }
                            }
                            return@item
                        }
                    }
                    else -> {}
                }
            }
        }
        if (emotionState is EmotionState.Loading){
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){  CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )
                // 呼吸动画文本
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Text(
                    "正在分析中...深呼吸放松一下",
                    modifier = Modifier.scale(scale),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        /*if (viewModel.history.isNotEmpty()){
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "近期情绪记录",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            /*LazyRow {
                items(viewModel.history) { item ->
                    EmotionHistoryItem(item, emotionColors)
                }
            }*/
        }*/

        /*if (emotionState is EmotionState.Success) {
            val result = (emotionState as EmotionState.Success).result
            EmotionResultCard(result, emotionColors)

            // 校园资源卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                /*Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "校园心理资源",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    campusResources.forEach { resource ->
                        Text(
                            "• $resource",
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontSize = 14.sp
                        )
                    }
                }*/
            }
        }*/

        // 底部信息
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "IvoryTower Whisper - 你的随身情绪分析伙伴",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmotionHistoryItem(item: EmotionHistory, emotionColors: Map<String, Color>) {
    val color = emotionColors[item.emotion] ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .width(120.dp)
            .padding(end = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                item.emotion,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "${(item.score * 100).toInt()}%",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                item.text,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun EmotionResultCard(result: EmotionResult, emotionColors: Map<String, Color>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "分析结果",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 情绪标签
            val emotionColor = emotionColors[result.emotion] ?: MaterialTheme.colorScheme.primary

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(emotionColor.copy(alpha = 0.2f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mood,
                        contentDescription = "情绪",
                        tint = emotionColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "检测到的情绪",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = result.emotion,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = emotionColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "情绪强度",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${(result.score * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 建议区域
            Text(
                text = "专业建议",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = emotionColor.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = result.advice,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 情绪描述
            val emotionDescription = when (result.emotion) {
                "快乐" -> "快乐是一种积极的情绪状态，通常伴随着满足感和愉悦感。保持快乐有助于提升整体健康水平。"
                "平静" -> "平静代表内心的安宁与平衡，是一种健康的情绪状态，有助于减少压力和提高专注力。"
                "悲伤" -> "悲伤是对失落或不如意事件的正常反应，适度的悲伤有助于情绪释放和心理调整。"
                "愤怒" -> "愤怒是一种强烈的情绪反应，通常由挫折或感到不公引起。需要适当管理以避免负面影响。"
                "焦虑" -> "焦虑是对未来不确定性的担忧，适度的焦虑有警示作用，但过度焦虑可能影响生活。"
                else -> "情绪状态稳定，是心理健康的重要标志。保持均衡的生活习惯有助于维持这种状态。"
            }

            Text(
                text = emotionDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}



@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    IvoryTowerWhisperTheme {
        LoginScreen(onLoginSuccess = { _, _ -> }, onNavigateToHistory = {->})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnalysisScreen() {
    IvoryTowerWhisperTheme {
        AnalysisScreen(apiKey = "test_key", username = "张三", onNavigateToHistory = {->}, onBackToLogin = {->}, toggleTheme = {->})
    }
}