package com.chen.ivorytowerwhisper.data.remote

import com.chen.ivorytowerwhisper.model.EmotionAnalysisRequest
import com.chen.ivorytowerwhisper.model.EmotionAnalysisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
    @POST("v1/chat/completions")
    suspend fun analyzeEmotion(
        @Header("Authorization") token: String,
        @Body request: EmotionAnalysisRequest
    ): Response<EmotionAnalysisResponse>
}

