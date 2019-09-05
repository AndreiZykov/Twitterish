package com.abaz.twitterish.screen.postdetails

import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.utils.extensions.add
import com.abaz.twitterish.utils.extensions.dislikeAndCopy
import com.abaz.twitterish.utils.extensions.likeAndCopy
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.koin.android.ext.android.inject

data class PostDetailsState(
    val selectedPostId: Long? = null,
    val replyBody: String = "",
    val selectedPostRepliesRequest: Async<List<Post>> = Uninitialized,
    val selectedPostReplies: List<Post> = emptyList(),
    val replyRequest: Async<Post> = Uninitialized,
    val selectedPost: Post? = null
) : MvRxState

class PostDetailsRxViewModel(
    initialState: PostDetailsState,
    private val postDataSource: PostDataSource
) : MvRxViewModel<PostDetailsState>(initialState, debugMode = BuildConfig.DEBUG) {

    private fun fetchReplies() = withState { state ->
        postDataSource.fetchReplies(state.selectedPostId!!, 1)
            .subscribeOn(io())
            .observeOn(mainThread())
            .execute {
                val newReplies = mutableListOf<Post>()
                if (it.complete) {
                    val replies = it.invoke()
                    replies?.let { posts -> newReplies.addAll(posts) }
                }
                newReplies.addAll(selectedPostReplies)
                copy(
                    selectedPostRepliesRequest = it,
                    selectedPostReplies = newReplies.distinctBy { post -> post.id }.sortedBy { post -> -post.id }
                )
            }
    }

    fun reply() = withState { state ->
        if (state.selectedPostId != null) {
            postDataSource.reply(state.selectedPostId, state.replyBody)
                .subscribeOn(io())
                .observeOn(mainThread())
                .execute {
                    refreshMainPost()
                    fetchReplies()
                    val selectedPost = postDataSource.postById(selectedPostId)
                    val newPost = it.invoke()
                    val replies = newPost
                        ?.let { selectedPostReplies.add(newPost, 0) }
                        ?: selectedPostReplies
                    copy(
                        selectedPost = selectedPost,
                        replyRequest = it,
                        selectedPostReplies = replies,
                        replyBody = ""
                    )
                }
        }
    }

    fun resetSelectedPost() = withState { state ->
        setState {
            copy(
                selectedPostRepliesRequest = Uninitialized,
                selectedPostReplies = emptyList(),
                replyRequest = Uninitialized,
                selectedPostId = null
            )
        }
    }

    fun onReplyTextChanged(replyBody: String) {
        setState { copy(replyBody = replyBody) }
    }

    fun refresh() {
        withState { state ->
            if (state.selectedPostId != null) {
                fetchReplies()
                refreshMainPost()
            }
        }
    }

    private fun refreshMainPost() {
        withState { state ->
            state.selectedPostId?.let {
                postDataSource.fetchPost(it)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .execute {
                        copy(selectedPost = it.invoke() ?: selectedPost)
                    }
            }
        }
    }

    fun selectPost(postId: Long) = withState {
        setState {
            val post = postDataSource.postById(postId)
            copy(selectedPostId = postId, selectedPost = post, replyBody = "")
        }
        fetchReplies()
        refreshMainPost()
    }

    fun repost(id: Long) {}

    fun like(postId: Long) = withState { state ->

        if (postId == state.selectedPostId) postDataSource.likeLocally(postId)
        setState {
            copy(selectedPost = postDataSource.postById(postId) ?: selectedPost,
                selectedPostReplies = selectedPostReplies.map { post -> if (post.id == postId) post.likeAndCopy() else post })
        }

        postDataSource.likeReply(postId)
            .subscribeOn(io())
            .observeOn(mainThread())
            .execute {
                val likedPost = it.invoke()
                copy(
                    selectedPost = if (selectedPostId == likedPost?.id) likedPost else selectedPost,
                    selectedPostReplies = selectedPostReplies.map { post -> if (post.id == likedPost?.id) likedPost else post }
                )
            }
    }

    fun dislike(postId: Long) = withState { state ->

        if (postId == state.selectedPostId) postDataSource.dislikeLocally(postId)
        setState {
            copy(selectedPost = postDataSource.postById(postId) ?: selectedPost,
                selectedPostReplies = selectedPostReplies.map { post -> if (post.id == postId) post.dislikeAndCopy() else post })
        }

        postDataSource.dislikeReply(postId)
            .subscribeOn(io())
            .observeOn(mainThread())
            .execute {
                val dislikedPost = it.invoke()
                copy(
                    selectedPost = if (selectedPostId == dislikedPost?.id) dislikedPost else selectedPost,
                    selectedPostReplies = selectedPostReplies.map { post -> if (post.id == dislikedPost?.id) dislikedPost else post }
                )
            }
    }

    companion object : MvRxViewModelFactory<PostDetailsRxViewModel, PostDetailsState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: PostDetailsState
        ): PostDetailsRxViewModel {
            val postDataSource: PostDataSource by viewModelContext.activity.inject()
            return PostDetailsRxViewModel(state, postDataSource)
        }

        override fun initialState(viewModelContext: ViewModelContext): PostDetailsState? {
            return PostDetailsState()
        }
    }

}