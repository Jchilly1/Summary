package com.rewindvideo.plex.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    /** Every Plex endpoint (plex.tv and a local server alike) wants these identity headers. */
    private fun plexHeaders(clientIdentifier: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("X-Plex-Product", "Rewind Video Club")
            .addHeader("X-Plex-Version", "1.0")
            .addHeader("X-Plex-Platform", "Android")
            .addHeader("X-Plex-Device", "Android")
            .addHeader("X-Plex-Device-Name", "Rewind Video Club")
            .addHeader("X-Plex-Client-Identifier", clientIdentifier)
            .build()
        chain.proceed(request)
    }

    private fun plexToken(token: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Plex-Token", token)
            .build()
        chain.proceed(request)
    }

    fun createAuthApi(clientIdentifier: String): PlexTvAuthApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(plexHeaders(clientIdentifier))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://plex.tv/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlexTvAuthApi::class.java)
    }

    fun createServerApi(baseUrl: String, accessToken: String, clientIdentifier: String): PlexServerApi {
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val client = OkHttpClient.Builder()
            .addInterceptor(plexHeaders(clientIdentifier))
            .addInterceptor(plexToken(accessToken))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlexServerApi::class.java)
    }
}
