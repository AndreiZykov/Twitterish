package com.abaz.twitterish.network

import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.request.LoginRequest
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @GET("feed")
    fun feed(@Query("page") page: Int): Observable<PostListResponse>

    @POST("post/{id}/like")
    fun like(@Path("id") id: Long): Observable<ResponseObject<Post>>

    @POST("post/{id}/dislike")
    fun dislike(@Path("id") id: Long): Observable<ResponseObject<Post>>

    @POST("signIn")
    fun login(@Body user: LoginRequest): Observable<ResponseObject<User>>

    @POST("user")
    fun signUp(@Body user: LoginRequest): Observable<ResponseObject<User>>
}


class TechTalkApi(provider: RetrofitProvider) {

    private val service = provider.provide().create(TechTalkService::class.java)

    fun feed(page: Int): Observable<PostListResponse> = service.feed(page)

    fun like(id: Long): Observable<ResponseObject<Post>> = service.like(id)

    fun dislike(id: Long): Observable<ResponseObject<Post>> = service.dislike(id)

    fun login(username: Username, password: Password): Observable<ResponseObject<User>> =
        service.login(LoginRequest(username.value, password.value))

    fun signUp(username: Username, password: Password): Observable<ResponseObject<User>> =
        service.signUp(LoginRequest(username.value, password.value))

}