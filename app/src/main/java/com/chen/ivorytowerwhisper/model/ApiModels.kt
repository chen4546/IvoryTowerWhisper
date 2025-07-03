package com.chen.ivorytowerwhisper.model

import java.util.Dictionary

data class EmotionAnalysisRequest(
    val model: String = "deepseek-chat",
    val frequency_penalty: Double=0.0,
    val presence_penalty: Double=0.0,
    val response_format: Map<String, String> = mapOf(Pair("type","text")),
    val stop: Nothing? = null,
    val stream_options: Nothing? =null,
    val messages: List<Message>,
    val temperature: Double = 1.0,
    val max_tokens: Int = 200,
    val stream: Boolean = false // 添加stream字段
)

data class Message(
    val role: String, // "user" or "system"
    val content: String
)

// 响应体结构
data class EmotionAnalysisResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class EmotionResult(
    val emotion: String,
    val score: Float,
    val advice: String,

)

sealed class EmotionState {
    object Idle : EmotionState()
    object Loading : EmotionState()
    data class Success(val result: EmotionResult) : EmotionState()
    data class Error(val message: String) : EmotionState()
}
sealed class Screen {
    object Login : Screen()
    object Analysis : Screen()
}
