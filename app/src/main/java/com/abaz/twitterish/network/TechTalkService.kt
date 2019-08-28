package com.abaz.twitterish.network

import com.abaz.twitterish.network.response.PostListReponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @Headers("Accept: application/json")
    @GET("feed")
    fun feed(): Observable<PostListReponse>
}