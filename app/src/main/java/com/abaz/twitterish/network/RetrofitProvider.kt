package com.abaz.twitterish.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class RetrofitProvider(private val okHttpClientProvider: OkHttpClientProvider) {

    fun provide(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(
            Date::class.java,
            DateTimeDeserializer()).create()))

        .client(okHttpClientProvider.provide())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl("https://busto-tech-talk.herokuapp.com/")
        .build()

}


class OkHttpClientProvider {

    private val logging = HttpLoggingInterceptor().apply {
        HttpLoggingInterceptor.Level.BODY
    }

    fun provide() =   OkHttpClient()
        .newBuilder()
        .addInterceptor(logging)
        .addInterceptor {
            val token = "8015C77DC3D0A76074EC21380963E596429316E06FFB5B7F0E9A37F9A5E9ABDB"
            val newRequest = it.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()
            it.proceed(newRequest)
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()
}