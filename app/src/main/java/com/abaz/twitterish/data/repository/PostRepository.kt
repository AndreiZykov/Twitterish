package com.abaz.twitterish.data.repository

import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import io.reactivex.Observable
import io.reactivex.Single

class PostRepository(
    private val userDataSource: UserDataSource,
    private val api: TechTalkApi
) : PostDataSource {
    override fun feed(page: Int): Observable<PostListResponse> = api.feed(page)

    override fun new(body: String): Single<ResponseObject<Post>> =
        api.new(userDataSource.userId(), body)

}