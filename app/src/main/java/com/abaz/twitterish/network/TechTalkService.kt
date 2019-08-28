package com.abaz.twitterish.network

import com.abaz.twitterish.network.response.PostListReponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @Headers("Accept: application/json")
    @GET("feed")
    fun feed(): Observable<PostListReponse>
}


class TechTalkApi(provider: RetrofitProvider) : TechTalkService {

    private val api = provider.provide().create(TechTalkService::class.java)

    override fun feed(): Observable<PostListReponse> = api.feed()
}