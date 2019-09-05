package com.abaz.twitterish.data.repository

import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.db.model.PostBodyParams
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.extensions.dislikeAndCopy
import com.abaz.twitterish.utils.extensions.incrementReplyCountAndCopy
import com.abaz.twitterish.utils.extensions.likeAndCopy
import io.reactivex.Observable
import io.reactivex.Single

class PostRepository(private val userDataSource: UserDataSource, private val api: TechTalkApi) :
    PostDataSource {

    private var feedCache = listOf<Post>()

    override fun feed(page: Int): Observable<List<Post>> = api.feed(page)
        .map { it.responseList }.doOnNext(::updateFeed)

    override fun likeLocally(postId: Long) {
        postById(postId)?.let { updateFeed(listOf(it.likeAndCopy())) }
    }

    override fun dislikeLocally(postId: Long) {
        postById(postId)?.let { updateFeed(listOf(it.dislikeAndCopy())) }
    }

    override fun cachedFeed(): List<Post> = feedCache

    override fun new(body: String): Observable<Post> =
        api.new(userDataSource.userId(), body)
            .doOnNext { response -> response.responseObject?.let { updateFeed(listOf(it)) } }
            .map { it.responseObject }

    override fun repost(id: Long): Single<Post> {
        return api.repost(id).map { it.responseObject }
    }

    override fun like(id: Long): Single<ResponseObject<Post>> {
        return api.like(id)
            .doOnSuccess { response -> response.responseObject?.let { updateFeed(listOf(it)) }  }
    }

    override fun likeReply(id: Long): Single<Post> = api.like(id).map { it.responseObject }

    override fun dislike(id: Long): Single<ResponseObject<Post>> {
        return api.dislike(id)
            .doOnSuccess { response -> response.responseObject?.let { updateFeed(listOf(it)) }  }
    }

    override fun dislikeReply(id: Long): Single<Post> = api.dislike(id).map { it.responseObject }

    override fun reply(postId: Long, body: String): Observable<Post> {
        return api.reply(postId, PostBodyParams(body, userDataSource.userId()))
            .map { it.responseObject }
    }

    override fun fetchReplies(postId: Long, page: Int): Observable<List<Post>> {
        return api.replies(postId, page).map { it.responseList }
    }

    override fun postById(postId: Long?): Post? = feedCache.find { post -> post.id == postId }


    override fun fetchPost(postId: Long): Single<Post> {
        return api.fetchPost(postId)
            .doOnSuccess { updateFeed(listOf(it)) }
    }

    private fun updateFeed(newPosts: List<Post>) {
        val newList = mutableListOf<Post>()
        newList.addAll(newPosts)
        newList.addAll(feedCache)
        feedCache = newList.distinctBy { post -> post.id }.sortedBy { post -> -post.id }
    }

}