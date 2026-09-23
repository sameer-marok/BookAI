package com.sameer.bookai.data.remote

import com.sameer.bookai.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitInstance {
    private val interceptor = HttpLoggingInterceptor().apply {
        // Set logs only in debug mode for security reasons
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        redactHeader("Authorization") // Redact the Authorization header for security
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
    // Kotlinx.serialization JSON instance
    private val json = Json {
        ignoreUnknownKeys = true
    }
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // Use the local server URL for Android emulator
        .client(client)
        .addConverterFactory(
            // Use kotlinx.serialization converter factory for Retrofit
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()

    // Create an instance of the BookAiApi interface
    val api: BookAiApi = retrofit.create(BookAiApi::class.java)
}