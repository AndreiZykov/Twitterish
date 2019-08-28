package com.abaz.twitterish.screen

import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.TechTalkService
import com.abaz.twitterish.network.response.PostListReponse
import com.airbnb.mvrx.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

data class HomeFeedState(val feed: Async<PostListReponse> = Uninitialized) : MvRxState

//data class HomeFeedState(val feed: List<Post> = Uninitialized) : MvRxState

/**
 * https://github.com/airbnb/MvRx/tree/master/dogs/src/main/java/com/airbnb/mvrx/dogs
 */

class HomeFeedViewModel(
    initialState: HomeFeedState,
    private val api: TechTalkApi
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    init {
        fetchFeed()
    }

    fun fetchFeed() = withState { state ->
        if (state.feed is Loading) return@withState
        api.feed()
            .subscribeOn(Schedulers.io())
            .execute { copy(feed = it) }
    }


    companion object : MvRxViewModelFactory<HomeFeedViewModel, HomeFeedState> {

        override fun create(viewModelContext: ViewModelContext, state: HomeFeedState): HomeFeedViewModel {
//            val api: TechTalkApi by (viewModelContext as? FragmentViewModelContext)
//                ?.fragment?.inject()

            val api: TechTalkApi by viewModelContext.activity.inject()

            return HomeFeedViewModel(state, api)
        }

        override fun initialState(viewModelContext: ViewModelContext): HomeFeedState? {
            return HomeFeedState()
        }
    }

}