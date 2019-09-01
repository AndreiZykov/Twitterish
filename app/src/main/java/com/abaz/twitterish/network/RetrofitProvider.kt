package com.abaz.twitterish.network

import com.abaz.twitterish.data.repository.SharedPreferenceRepository
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
class RetrofitProvider(private val okHttpClientProvider: OkHttpClientProvider
) {

    fun provide(): Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(
                    Date::class.java,
                    DateTimeDeserializer()
                ).create()
            )
        )

        .client(okHttpClientProvider.provide())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl("https://busto-tech-talk.herokuapp.com/")
//        .baseUrl("https://localhost:4567/")
        .build()

}


class OkHttpClientProvider(private val sharedPrefs: SharedPreferenceRepository) {

    companion object {
        //for testing early on
        const val TOKEN = "BA11606C57548ACEDE699ACB47EE105933AD5FC5E3BFB2DD6B4D6E7AF1F397E1"
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun provide(): OkHttpClient = OkHttpClient.Builder()
//       OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .addInterceptor {
            val newRequest = it.request().newBuilder()
                .addHeader("Authorization", "Bearer ${sharedPrefs.userToken()}")
                .addHeader("Content-Type", "application/json")
                .build()
            it.proceed(newRequest)
        }
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
}