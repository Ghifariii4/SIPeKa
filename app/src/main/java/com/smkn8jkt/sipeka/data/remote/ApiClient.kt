package com.smkn8jkt.sipeka.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://47.129.118.194/api/v1/"

    private var tokenManager: TokenManager? = null

    fun init(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager?.getTokenSync()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            val authHeaderValue = if (token.startsWith("Bearer ", ignoreCase = true)) {
                token
            } else {
                "Bearer $token"
            }
            requestBuilder.header("Authorization", authHeaderValue)
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
