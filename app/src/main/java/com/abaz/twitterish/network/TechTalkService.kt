package com.abaz.twitterish.network

import com.abaz.twitterish.network.response.BooleanResponse
import com.abaz.twitterish.network.response.PostListReponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @GET("feed")
    fun feed(): Observable<PostListReponse>


    @POST("post/{id}/like")
    fun like(@Path("id") id: Long): Observable<BooleanResponse>

    @POST("post/{id}/dislike")
    fun dislike(@Path("id") id: Long): Observable<BooleanResponse>
}


class TechTalkApi(provider: RetrofitProvider) {

    private val api = provider.provide().create(TechTalkService::class.java)

    fun feed(): Observable<PostListReponse> = api.feed()

    fun like(id: Long): Observable<BooleanResponse> = api.like(id)

    fun dislike(id: Long): Observable<BooleanResponse> = api.dislike(id)
}