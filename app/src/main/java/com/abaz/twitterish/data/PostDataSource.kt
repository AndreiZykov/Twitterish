package com.abaz.twitterish.data

import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import io.reactivex.Observable
import io.reactivex.Single

interface PostDataSource {
    fun new(body: String): Single<ResponseObject<Post>>
    fun feed(page: Int): Observable<PostListResponse>
    fun reply(postId: Long, body: String): Single<ResponseObject<Post>>
}