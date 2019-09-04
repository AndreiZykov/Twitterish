package com.abaz.twitterish.screen

import com.abaz.printlnDebug
import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.extensions.add
import com.abaz.twitterish.utils.extensions.copy
import com.airbnb.mvrx.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

data class HomeFeedState(
    val feedRequest: Async<PostListResponse> = Uninitialized,
    val feed: List<Post> = emptyList(),
    val selectedPostId: Long? = null,
    val selectedPostRepliesRequest: Async<PostListResponse> = Uninitialized,
    val selectedPostReplies: List<Post> = emptyList(),
    val replyRequest: Async<ResponseObject<Post>> = Uninitialized,
    val likeRequest: Async<ResponseObject<Post>> = Uninitialized,
    val newPostRequest: Async<ResponseObject<Post>> = Uninitialized,
    val isLoggedIn: Boolean = true,
    val replyBody: String = ""
) : MvRxState

/**
 * https://github.com/airbnb/MvRx/tree/master/dogs/src/main/java/com/airbnb/mvrx/dogs
 */

class HomeFeedMvRxViewModel(
    initialState: HomeFeedState,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val api: TechTalkApi
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    private val disposables = CompositeDisposable()

    private val onPostCreatedSubject: PublishSubject<Unit> = PublishSubject.create()

    val onPostCreated: Observable<Unit> = onPostCreatedSubject

    var page = 0

    init {
        logStateChanges()
        userDataSource.onLogInStateChanged()
            .subscribe({
                // hack
                if (it.value) {
                    page = 0
                    fetchFeed()
                }
                setState { copy(isLoggedIn = it.value) }
            }) {}
            .disposeOnClear()
    }

    fun fetchFeed() = withState { state ->
        if (state.feedRequest is Loading) return@withState
        postDataSource.feed(++page)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    feedRequest = it,
                    feed = feed + (it()?.responseList ?: emptyList())
                )
            }
    }

    fun updateFeed() = withState { state ->
        if (state.feedRequest is Loading) return@withState
        page = 1
        api.feed(page)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    feedRequest = it,
                    feed = it()?.responseList ?: feed
                )
            }
    }


    fun new(body: String) = withState { state ->
        printlnDebug("calling new, body=$body")
        postDataSource.new(body)
            .doOnSuccess { onPostCreatedSubject.onNext(Unit) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .execute {
                val responseObject = it.invoke()?.responseObject
                if (responseObject != null) {
                    copy(
                        newPostRequest = it,
                        feed = feed.add(value = responseObject)
                    )
                } else {
                    copy(newPostRequest = it)
                }
            }
    }

    fun repost(id: Long) {
        println("DEBUG::repost CLICKED WITH ID = $id")
        api.repost(id)
            .doOnSuccess { updateFeed() }
            .subscribeOn(Schedulers.io())
            .execute {
                val responseObject = it.invoke()?.responseObject
                if (responseObject != null) {
                    copy(
                        newPostRequest = it,
                        feed = feed.add(responseObject)
                    )
                } else {
//                    copy(newPostRequest = it,
//                        feed = feed.delete { p -> p.originalPost?.id == id})

                    copy(newPostRequest = it)
                }
            }
    }

    fun like(id: Long) = withState { state ->
        println("DEBUG::calling rating, id=$id")

        val indexOf = state.feed.indexOfFirst { p -> p.id == id }
        val post = state.feed[indexOf]
        val authorizedUserExtras = post.authorizedUserExtras
        val currentLikeValue = (authorizedUserExtras?.rating ?: 0)
        val isLikingFromNeutral = currentLikeValue == 0
        val isLikingFromDislike = currentLikeValue < 0
        val isLiking = isLikingFromNeutral || isLikingFromDislike
        val currentLikesRating = post.likesRating
        val newLikeValue = if (isLiking) 1 else 0
        val newLikesRating = when {
            isLikingFromNeutral -> currentLikesRating + 1
            isLikingFromDislike -> currentLikesRating + 2
            else -> currentLikesRating - 1
        }

        val newExtras = authorizedUserExtras?.copy(rating = newLikeValue)
        setState {
            copy(
                feed = feed.copy(
                    indexOf,
                    post.copy(
                        authorizedUserExtras = newExtras,
                        likesRating = newLikesRating
                    )
                )
            )
        }

        api.like(id)
            .subscribeOn(Schedulers.io())
            .execute {
                printlnDebug("rating execute callback")

                printlnDebug("${it()}")

                printlnDebug("${it()?.responseObject}")

                val newFeed = it()?.responseObject?.let { post ->
                    feed.copy(indexOf, post)
                } ?: feed

                copy(
                    likeRequest = it,
                    feed = newFeed
                )
            }

    }

    fun dislike(id: Long) = withState { state ->

        println("DEBUG::calling dislike, id=$id")

        val indexOf = state.feed.indexOfFirst { p -> p.id == id }
        val post = state.feed[indexOf]
        val authorizedUserExtras = post.authorizedUserExtras
        val currentLikeValue = (authorizedUserExtras?.rating ?: 0)
        val isDislikingFromNeutral = currentLikeValue == 0
        val isDislikingFromLike = currentLikeValue > 0
        val isDisliking = isDislikingFromNeutral || isDislikingFromLike
        val currentLikesRating = post.likesRating
        val newLikeValue = if (isDisliking) -1 else 0
        val newLikesRating = when {
            isDislikingFromNeutral -> currentLikesRating - 1
            isDislikingFromLike -> currentLikesRating - 2
            else -> currentLikesRating + 1
        }

        val newExtras = authorizedUserExtras?.copy(rating = newLikeValue)
        setState {
            copy(
                feed = feed.copy(
                    indexOf,
                    post.copy(
                        authorizedUserExtras = newExtras,
                        likesRating = newLikesRating
                    )
                )
            )
        }

        api.dislike(id)
            .subscribeOn(Schedulers.io())
            .execute {
                printlnDebug("rating execute callback")

                printlnDebug("${it()}")

                printlnDebug("${it()?.responseObject}")

                val newFeed = it()?.responseObject?.let { post ->
                    feed.copy(indexOf, post)
                } ?: feed

                copy(
                    likeRequest = it,
                    feed = newFeed
                )
            }
    }

    fun reply() = withState { state ->
        withState { state ->
            if (state.selectedPostId != null) {
                postDataSource.reply(state.selectedPostId, state.replyBody)
                    .subscribeOn(Schedulers.io())
                    .execute {
                        printlnDebug("reply execute callback")
                        val responseObject = it.invoke()?.responseObject
                        val replyCopy = mutableListOf<Post>().apply { addAll(selectedPostReplies) }
                        takeIf { responseObject != null }?.let { reply -> replyCopy.add(reply, 0) }
                        copy(replyRequest = it, selectedPostReplies = replyCopy, replyBody = "")
                    }
            }
        }

    }

    fun selectPost(postId: Long) = withState { state ->

        printlnDebug("selectPost postId= $postId")

        setState {
            copy(selectedPostId = postId)
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

    fun fetchReplies(id: Long) = withState { state ->
        printlnDebug("fetchReplies postId= $id")
        api.replies(id, 1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    selectedPostRepliesRequest = it,
                    selectedPostReplies = it()?.responseList ?: emptyList()
                )
            }
    }

    fun dispose() {
        disposables.dispose()
    }

    fun signOut() {
        userDataSource.signOut()
            .subscribeOn(Schedulers.io())
            .observeOn(mainThread())
            .execute { copy(isLoggedIn = false) }
    }

    fun onReplyTextChanged(replyBody: String) {
        setState { copy(replyBody = replyBody) }
    }

    fun refresh() {
        withState { state ->
            if (state.selectedPostId != null) {
                fetchReplies(state.selectedPostId)
            }
        }

    }

    // on resume for post detail screen
    fun onResume() {
        withState {
            setState { copy(replyBody = "") }
            if (it.selectedPostId != null) fetchReplies(it.selectedPostId)
        }
    }

    companion object : MvRxViewModelFactory<HomeFeedMvRxViewModel, HomeFeedState> {

        const val ITEMS_PER_PAGE = 10

        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeFeedState
        ): HomeFeedMvRxViewModel {
//            val api: TechTalkApi by (viewModelContext as? FragmentViewModelContext)
//                ?.fragment?.inject()

            val api: TechTalkApi by viewModelContext.activity.inject()

            val postDataSource: PostDataSource by viewModelContext.activity.inject()

            val userDataSource: UserDataSource by viewModelContext.activity.inject()

            return HomeFeedMvRxViewModel(state, postDataSource, userDataSource, api)
        }

        override fun initialState(viewModelContext: ViewModelContext): HomeFeedState? {
            return HomeFeedState()
        }
    }

}