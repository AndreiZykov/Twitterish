package com.abaz.twitterish.screen.homefeed

import com.abaz.printlnDebug
import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.extensions.add
import com.airbnb.mvrx.*
import io.reactivex.Observable
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
    val feedRequest: Async<List<Post>> = Uninitialized,
    val feed: List<Post> = emptyList(),
    val selectedPostId: Long? = null,
    val likeRequest: Async<ResponseObject<Post>> = Uninitialized,
    val newPostRequest: Async<Post> = Uninitialized,
    val isLoggedIn: Boolean = true
) : MvRxState

/**
 * https://github.com/airbnb/MvRx/tree/master/dogs/src/main/java/com/airbnb/mvrx/dogs
 */

class HomeFeedMvRxViewModel(
    initialState: HomeFeedState,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    private val disposables = CompositeDisposable()

    private val onPostCreatedSubject: PublishSubject<Unit> = PublishSubject.create()

    val onPostCreated: Observable<Unit> = onPostCreatedSubject

    var page = 0

    init {
        logStateChanges()
        fetchFeed()
    }

    fun onResume() {
        setState {
            copy(
                feed = postDataSource.cachedFeed(),
                isLoggedIn = userDataSource.isLoggedIn().value
            )
        }
    }

    fun fetchFeed() = withState { state ->
        if (state.feedRequest is Loading) return@withState
        postDataSource.feed(++page)
            .subscribeOn(Schedulers.io())
            .observeOn(mainThread())
            .execute { copy(feedRequest = it, feed = feed + (it() ?: emptyList())) }
    }

    fun updateFeed() = withState { state ->
        if (state.feedRequest is Loading) return@withState
        page = 0
        postDataSource.feed(page)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    feedRequest = it,
                    feed = it() ?: feed
                )
            }
    }


    fun new(body: String) = withState { state ->
        printlnDebug("calling new, body=$body")
        postDataSource.new(body)
            .doOnSuccess { onPostCreatedSubject.onNext(Unit) }
            .subscribeOn(Schedulers.io())
            .observeOn(mainThread())
            .execute {
                val responseObject = it.invoke()
                if (responseObject != null) {
                    copy(newPostRequest = it, feed = postDataSource.cachedFeed())
                } else {
                    copy(newPostRequest = it)
                }
            }
    }

    fun repost(id: Long) {
        postDataSource.repost(id)
            .doOnSuccess { updateFeed() }
            .subscribeOn(Schedulers.io())
            .execute {
                val responseObject = it.invoke()
                if (responseObject != null) {
                    copy(newPostRequest = it, feed = feed.add(responseObject))
                } else {
                    copy(newPostRequest = it)
                }
            }
    }

    fun like(postId: Long) = withState { _ ->
        postDataSource.likeLocally(postId)
        setState { copy(feed = postDataSource.cachedFeed()) }
        postDataSource.like(postId)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(likeRequest = it, feed = postDataSource.cachedFeed())
            }
    }

    fun dislike(postId: Long) = withState { _ ->
        postDataSource.dislikeLocally(postId)
        setState { copy(feed = postDataSource.cachedFeed()) }
        postDataSource.dislike(postId)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(likeRequest = it, feed = postDataSource.cachedFeed())
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

    companion object : MvRxViewModelFactory<HomeFeedMvRxViewModel, HomeFeedState> {

        const val ITEMS_PER_PAGE = 10

        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeFeedState
        ): HomeFeedMvRxViewModel {
            val postDataSource: PostDataSource by viewModelContext.activity.inject()
            val userDataSource: UserDataSource by viewModelContext.activity.inject()
            return HomeFeedMvRxViewModel(
                state,
                postDataSource,
                userDataSource
            )
        }

        override fun initialState(viewModelContext: ViewModelContext): HomeFeedState? {
            val userDataSource: UserDataSource by viewModelContext.activity.inject()
            return HomeFeedState(isLoggedIn = userDataSource.isLoggedIn().value)
        }
    }

}