package com.abaz.twitterish.network

import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.db.model.PostBodyParams
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.request.LoginRequest
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
interface TechTalkService {

    @GET("feed")
    fun feed(@Query("page") page: Int): Observable<PostListResponse>

    @GET("post/{id}/replies")
    fun replies(@Path("id") id: Long,
                @Query("page") page: Int): Observable<PostListResponse>

    @POST("post/{id}/like")
    fun like(@Path("id") id: Long): Single<ResponseObject<Post>>

    @POST("post/{id}/dislike")
    fun dislike(@Path("id") id: Long): Single<ResponseObject<Post>>

    @POST("post/{id}/reply")
    fun reply(@Path("id") id: Long,
              @Body reply: PostBodyParams): Observable<ResponseObject<Post>>

    @POST("signIn")
    fun login(@Body user: LoginRequest): Observable<ResponseObject<User>>

    @POST("user")
    fun signUp(@Body user: LoginRequest): Observable<ResponseObject<User>>

    @POST("post")
    fun new(@Body post: PostBodyParams): Observable<ResponseObject<Post>>

    @POST("post/{id}/repost")
    fun repost(@Path("id") id: Long): Single<ResponseObject<Post>>

    @GET("post/{id}")
    fun fetchPost(@Path("id") postId: Long): Single<Post>
}


class TechTalkApi(provider: RetrofitProvider) {

    private val service = provider.provide().create(TechTalkService::class.java)

    fun feed(page: Int): Observable<PostListResponse> = service.feed(page)

    fun replies(postId: Long, page: Int): Observable<PostListResponse> = service.replies(postId,page)

    fun like(id: Long): Single<ResponseObject<Post>> = service.like(id)

    fun dislike(id: Long): Single<ResponseObject<Post>> = service.dislike(id)

    fun reply(id: Long, @Body reply: PostBodyParams): Observable<ResponseObject<Post>> = service.reply(id, reply)

    fun new(post: PostBodyParams): Observable<ResponseObject<Post>> = service.new(post)

    fun new(userId: Long, body: String): Observable<ResponseObject<Post>> = service.new(
        PostBodyParams(
            userId = userId,
            body = body
        )
    )

    fun repost(id: Long): Single<ResponseObject<Post>> = service.repost(id)

    fun login(username: Username, password: Password): Observable<ResponseObject<User>> =
        service.login(LoginRequest(username.value, password.value))

    fun signUp(username: Username, password: Password): Observable<ResponseObject<User>> =
        service.signUp(LoginRequest(username.value, password.value))

    fun fetchPost(postId: Long): Single<Post> = service.fetchPost(postId)

}