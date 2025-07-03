package com.chen.ivorytowerwhisper.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chen.ivorytowerwhisper.data.remote.DeepSeekService
import com.chen.ivorytowerwhisper.data.remote.RetrofitClient
import com.chen.ivorytowerwhisper.model.EmotionAnalysisRequest
import com.chen.ivorytowerwhisper.model.EmotionAnalysisResponse
import com.chen.ivorytowerwhisper.model.EmotionResult
import com.chen.ivorytowerwhisper.model.EmotionState
import com.chen.ivorytowerwhisper.model.Message

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmotionViewModel : ViewModel() {

    private val service = RetrofitClient.deepSeekApiService


    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    private val _emotionState = MutableStateFlow<EmotionState>(EmotionState.Idle)
    val emotionState: StateFlow<EmotionState> = _emotionState

    fun setApiKey(key: String) {
        _apiKey.value = key
    }

    fun analyzeTextEmotion(text: String) {
        if (_apiKey.value.isEmpty()) {
            _emotionState.value = EmotionState.Error("请先设置API密钥")
            return
        }

        viewModelScope.launch {
            _emotionState.value = EmotionState.Loading
            try {
                val messages = listOf(
                    Message("system","""
                                    你作为大学心理健康专家，请分析学生文本，特别注意：
                                    1. 学业压力表现：考试焦虑、拖延症、GPA压力
                                    2. 社交挑战：室友矛盾、社团压力
                                    3. 身份认同：专业选择困惑、职业迷茫
                                    4. 经济压力：兼职负担、学费担忧
                                    请用JSON格式返回：{emotion(情绪名称):String, score(情绪强度):Float, advice(中文建议):String}
                                    """),
                    Message("user", text)
                )

                val response = service.analyzeEmotion(
                    token = "Bearer ${_apiKey.value}",
                    request = EmotionAnalysisRequest(
                        model = "deepseek-chat",
                        messages = messages,
                        temperature = 1.0,
                        max_tokens = 200)
                )

                if (response.isSuccessful) {
                    val result = parseEmotionResponse(response.body())
                    _emotionState.value = EmotionState.Success(result)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "未知错误"
                    _emotionState.value = EmotionState.Error("API错误: ${response.code()} - $errorBody\"")
                }
            } catch (e: Exception) {
                _emotionState.value = EmotionState.Error(e.localizedMessage ?: "未知错误")
            }
        }
    }

    private fun parseEmotionResponse(response: EmotionAnalysisResponse?): EmotionResult {
        response?.choices?.firstOrNull()?.let { choice ->
            try {
                val jsonResponse = choice.message.content.substringAfter("```json").substringBefore("```")
                Log.d("jsonResponse",jsonResponse)
                val jsonResult= JSONObject(jsonResponse)
                // 简化解析 - 实际应用中应使用JSON解析器
                val emotion = jsonResult.getString("emotion")//jsonResponse.substringAfter("\"emotion\":\"").substringBefore("\"score\"")
                Log.d("emotion",emotion)
                val score = jsonResult.getString("score").toFloat()//jsonResponse.substringAfter("\"score\":").substringBefore(",").toFloat()
                Log.d("score",score.toString())
                val advice = jsonResult.getString("advice")//jsonResponse.substringAfter("\"advice\":\"").substringBefore("\"")
                Log.d("advice",advice)
                return EmotionResult(emotion, score, advice)
            } catch (e: Exception) {
                throw Exception("解析响应失败: ${e.message}")
            }
        }
        throw Exception("空响应")
    }
}

