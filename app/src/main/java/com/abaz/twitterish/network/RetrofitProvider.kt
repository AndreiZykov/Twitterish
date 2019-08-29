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

    companion object {
        //for testing early on
        const val TOKEN = "836DDE54ED2698D501981F4A602B86AD51C4A3A83A0FB7453C2BB083ED22DD3D"
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun provide() =   OkHttpClient.Builder()
//       OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .addInterceptor {
            val newRequest = it.request().newBuilder()
                .addHeader("Authorization", "Bearer $TOKEN")
                .addHeader("Content-Type", "application/json")
                .build()
            it.proceed(newRequest)
        }
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
}