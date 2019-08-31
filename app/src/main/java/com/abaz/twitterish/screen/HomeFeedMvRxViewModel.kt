package com.abaz.twitterish.screen

import com.abaz.printlnDebug
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.db.model.Posts
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.PostListResponse
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.extensions.copy
import com.abaz.twitterish.utils.extensions.upsert
import com.airbnb.mvrx.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

data class HomeFeedState(
    val feedRequest: Async<PostListResponse> = Uninitialized,
    val feed: Posts = emptyList(),
    val selectedPostId: Long? = null,
    val likeRequest: Async<ResponseObject<Post>> = Uninitialized,
    val newPostRequest: Async<ResponseObject<Post>> = Uninitialized
) : MvRxState

/**
 * https://github.com/airbnb/MvRx/tree/master/dogs/src/main/java/com/airbnb/mvrx/dogs
 */

class HomeFeedMvRxViewModel(
    initialState: HomeFeedState,
    private val api: TechTalkApi
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    private val disposables = CompositeDisposable()

    var page = 0

    init {
        fetchFeed()
    }

    fun fetchFeed() = withState { state ->
        if (state.feedRequest is Loading) return@withState
        api.feed(++page)
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

        api.new(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .execute {
                if(it.invoke()?.responseObject != null) {
                    copy(
                        newPostRequest = it,
//                    feed = feed + it.invoke()?.responseObject!!
                        feed = feed.copy(0, it.invoke()?.responseObject!!)
                    )
                } else {
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

//    fun dislike(id: Long) = withState { state ->
//
//        println("DEBUG::calling dislike, id=$id")
//
//        val indexOf = state.feed.indexOfFirst { p -> p.id == id }
//        val post = state.feed[indexOf]
//        val authorizedUserExtras = post.authorizedUserExtras
//        val currentDislikeValue = (authorizedUserExtras?.dislike ?: 0)
//        val isDisliking = currentDislikeValue <= 0
//        val currentLikesRating = post.likesRating
//        val newDislikeValue = if (isDisliking) 1 else 0
//        val newLikesRating = if (isDisliking) currentLikesRating - 1 else currentLikesRating + 1
//        val newExtras = authorizedUserExtras?.copy(dislike = newDislikeValue, rating = 0)
//        setState {
//            copy(
//                feed = feed.copy(
//                    indexOf,
//                    post.copy(
//                        authorizedUserExtras = newExtras,
//                        likesRating = newLikesRating
//                    )
//                )
//            )
//        }
//
//        api.dislike(id)
//            .subscribeOn(Schedulers.io())
//            .execute {
//                printlnDebug("rating execute callback")
//
//                printlnDebug("${it()}")
//
//                printlnDebug("${it()?.responseObject}")
//
//                val newFeed = it()?.responseObject?.let { post ->
//                    feed.copy(indexOf, post)
//                } ?: feed
//
//                copy(
//                    likeRequest = it,
//                    feed = newFeed
//                )
//            }
//    }

    fun reply(id: Long) {
        println("DEBUG::reply CLICKED WITH ID = $id")
    }

    fun repost(id: Long) {
        println("DEBUG::repost CLICKED WITH ID = $id")
    }

    fun handleClicks(clicks: Observable<PostExtrasIntent>) {
        disposables.add(
            clicks.subscribe(
                {
                    when (it) {
                        is PostExtrasIntent.Like -> {
                            println("Like Clicked")
                        }
                        is PostExtrasIntent.Dislike -> {
                            println("Dislike Clicked")
                        }
                        is PostExtrasIntent.Reply -> {
                            println("Reply Clicked")
                        }
                        is PostExtrasIntent.Repost -> {
                            println("Repost Clicked")
                        }
                    }
                },
                { it.printStackTrace() }
            )
        )
    }

    fun dispose() {
        disposables.dispose()
    }


    companion object : MvRxViewModelFactory<HomeFeedMvRxViewModel, HomeFeedState> {

        const val ITEMS_PER_PAGE = 10

        override fun create(viewModelContext: ViewModelContext, state: HomeFeedState): HomeFeedMvRxViewModel {
//            val api: TechTalkApi by (viewModelContext as? FragmentViewModelContext)
//                ?.fragment?.inject()

            val api: TechTalkApi by viewModelContext.activity.inject()

            return HomeFeedMvRxViewModel(state, api)
        }

        override fun initialState(viewModelContext: ViewModelContext): HomeFeedState? {
            return HomeFeedState()
        }
    }

}