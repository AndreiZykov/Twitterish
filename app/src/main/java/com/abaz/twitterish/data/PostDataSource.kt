package com.abaz.twitterish.data

import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import io.reactivex.Observable
import io.reactivex.Single

interface PostDataSource {
    fun new(body: String): Observable<Post>
    fun feed(page: Int): Observable<List<Post>>
    fun reply(postId: Long, body: String): Observable<Post>
    fun fetchReplies(postId: Long, page: Int): Observable<List<Post>>
    fun like(id: Long): Single<ResponseObject<Post>>
    fun likeReply(id: Long): Single<Post>
    fun dislike(id: Long): Single<ResponseObject<Post>>
    fun dislikeReply(id: Long): Single<Post>
    fun postById(postId: Long?): Post?
    fun repost(id: Long): Single<Post>
    fun likeLocally(postId: Long)
    fun cachedFeed(): List<Post>
    fun dislikeLocally(postId: Long)
    fun fetchPost(postId: Long): Single<Post>
}