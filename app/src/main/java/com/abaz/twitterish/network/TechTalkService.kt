package com.abaz.twitterish.network

import com.abaz.twitterish.data.Post
import com.abaz.twitterish.network.response.BooleanResponse
import com.abaz.twitterish.network.response.PostListReponse
import com.abaz.twitterish.network.response.ResponseObject
import io.reactivex.Observable
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @GET("feed")
    fun feed(@Query("page") page: Int): Observable<PostListReponse>


    @POST("post/{id}/like")
    fun like(@Path("id") id: Long): Observable<ResponseObject<Post>>

    @POST("post/{id}/dislike")
    fun dislike(@Path("id") id: Long):  Observable<ResponseObject<Post>>
}


class TechTalkApi(provider: RetrofitProvider) {

    private val service = provider.provide().create(TechTalkService::class.java)

    fun feed(page: Int): Observable<PostListReponse> = service.feed(page)

    fun like(id: Long): Observable<ResponseObject<Post>> = service.like(id)

    fun dislike(id: Long):  Observable<ResponseObject<Post>> = service.dislike(id)
}