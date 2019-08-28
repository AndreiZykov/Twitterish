package com.abaz.twitterish.network


import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface DadJokeService {
    @Headers("Accept: application/json")
    @GET("search")
    fun search(
        @Query("query") query: String? = null,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 20
    ): Observable<String>

    @Headers("Accept: application/json")
    @GET("j/{id}")
    fun fetch(@Path("id") id: String): Observable<String>

    @Headers("Accept: application/json")
    @GET("/")
    fun random(): Observable<String>
}