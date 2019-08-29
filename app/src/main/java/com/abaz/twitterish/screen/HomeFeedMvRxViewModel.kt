package com.abaz.twitterish.screen

import com.abaz.twitterish.data.LikeDislikeStatus
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.BooleanResponse
import com.abaz.twitterish.network.response.PostListReponse
import com.airbnb.mvrx.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

data class HomeFeedState(
    val feed: Async<PostListReponse> = Uninitialized,
    val likeRequest: Async<BooleanResponse> = Uninitialized
) : MvRxState

/**
 * https://github.com/airbnb/MvRx/tree/master/dogs/src/main/java/com/airbnb/mvrx/dogs
 */

class HomeFeedMvRxViewModel(
    initialState: HomeFeedState,
    private val api: TechTalkApi
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    private val disposables = CompositeDisposable()

    init {
        fetchFeed()
    }

    fun fetchFeed() = withState { state ->
        if (state.feed is Loading) return@withState
        api.feed()
            .subscribeOn(Schedulers.io())
            .execute { copy(feed = it) }
    }

    fun like(id: Long) = withState { state ->
        println("DEBUG::calling like, id=$id")

        api.like(id)
            .subscribeOn(Schedulers.io())
            .execute {
                println("DEBUG::like execute callback")


                feed()?.responseList
                    ?.find { post -> post.id == id }
                    ?.apply { updatePostExtrasLikedByMe(true) }


                copy(
                    feed = feed,
                    likeRequest = it
                )
            }


//        api.like(id)
//            .subscribeOn(Schedulers.io())
//            .execute { copy(feed = it) }
    }


    fun dislike(id: Long) {
        println("DEBUG::dislike CLICKED WITH ID = $id")
    }

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