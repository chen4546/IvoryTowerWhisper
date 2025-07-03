package com.chen.ivorytowerwhisper.data.remote

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.deepseek.com/"

    private val gson = GsonBuilder().setLenient().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            var response: Response? = null
            var retryCount = 0
            val maxRetries = 3

            while (retryCount < maxRetries) {
                try {
                    response = chain.proceed(request)
                    // 检查API Key错误
                    if (response.code == 401 || response.code == 403) {
                        // 直接抛出异常，避免重试
                        return@addInterceptor response
                    }

                    if (response.isSuccessful) {
                        return@addInterceptor response
                    } else if (response.code in 500..599 && retryCount < maxRetries - 1) {
                        // 服务器错误，重试
                        retryCount++
                        Thread.sleep(1000L * retryCount) // 指数退避
                    } else {
                        return@addInterceptor response
                    }
                } catch (e: SocketTimeoutException) {
                    if (retryCount >= maxRetries - 1) throw e
                    retryCount++
                    Thread.sleep(1000L * retryCount)
                } catch (e: ConnectException) {
                    if (retryCount >= maxRetries - 1) throw e
                    retryCount++
                    Thread.sleep(1000L * retryCount)
                }
            }
            response ?: throw SocketTimeoutException("连接超时")
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val deepSeekApiService: DeepSeekService by lazy {
        retrofit.create(DeepSeekService::class.java)
    }
}