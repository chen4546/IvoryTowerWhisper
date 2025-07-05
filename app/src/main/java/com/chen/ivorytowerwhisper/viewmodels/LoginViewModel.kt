package com.chen.ivorytowerwhisper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chen.ivorytowerwhisper.data.local.LocalStorage
import com.chen.ivorytowerwhisper.data.remote.RetrofitClient
import com.chen.ivorytowerwhisper.model.EmotionAnalysisRequest
import com.chen.ivorytowerwhisper.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(application: Application): AndroidViewModel(application ) {
    private val service = RetrofitClient.deepSeekApiService

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    // 加载保存的用户凭证
    fun loadSavedCredentials() {
        val context = getApplication<Application>().applicationContext
        val prefs = LocalStorage.getUserPreferences(context)
        if (prefs != null) {
            _loginState.value = LoginState.SavedCredentials(prefs.apiKey, prefs.username)
        }
    }
    fun verifyApiKey(apiKey: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // 发送一个简单的测试请求验证API Key
                val testRequest = EmotionAnalysisRequest(
                    messages = listOf(
                        Message("user", "Hello")
                    ),
                    max_tokens = 10
                )

                val response = service.analyzeEmotion(
                    token = "Bearer $apiKey",
                    request = testRequest
                )

                if (response.isSuccessful) {
                    if (response.body()?.choices?.firstOrNull()?.message?.content?.contains("Hello") == true) {
                        _loginState.value = LoginState.Success

                    } else {
                        // 尝试解析错误信息
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null && errorBody.contains("invalid_request_error")) {
                            _loginState.value = LoginState.Error("API Key无效")
                        } else {
                            _loginState.value = LoginState.Error("验证失败: ${response.body()?.toString()}")
                        }
                    }
                } else {
                    // 解析错误响应
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null && errorBody.contains("invalid_request_error")) {
                        "API Key无效"
                    } else {
                        "API错误: ${response.code()} - ${errorBody ?: "未知错误"}"
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is HttpException -> "网络错误: ${e.code()}"
                    else -> "连接失败: ${e.message}"
                }
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }
    // 清除保存的用户凭证
    fun clearCredentials() {
        val context = getApplication<Application>().applicationContext
        LocalStorage.clearUserPreferences(context)
    }
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        data class SavedCredentials(val apiKey: String, val username: String) : LoginState()

    }
}