package com.abaz.twitterish.screen

import com.abaz.twitterish.mvrx.MvRxViewModel
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
    private val techTalkService: TechTalkService
) : MvRxViewModel<HomeFeedState>(initialState, debugMode = true) {


    init {
        fetchFeed()
    }

    fun fetchFeed() = withState { state ->
        if (state.feed is Loading) return@withState
//        techTalkService
//            .feed()
//            .subscribeOn(Schedulers.io())
//            .execute { copy(feed = it) }


        Observable.create<PostListReponse> {
            it.onNext(PostListReponse(PostListReponse.fakeData()))
        }
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .execute { copy(feed = it) }
    }


    companion object : MvRxViewModelFactory<HomeFeedViewModel, HomeFeedState> {

        override fun create(viewModelContext: ViewModelContext, state: HomeFeedState): HomeFeedViewModel {
            val service: TechTalkService by viewModelContext.activity.inject()

//            val service: TechTalkService by (viewModelContext as? FragmentViewModelContext)
//                ?.fragment?.inject()
//
            return HomeFeedViewModel(state, service)
        }

        override fun initialState(viewModelContext: ViewModelContext): HomeFeedState? {
//            val foo = viewModelContext.activity.inject()
            return HomeFeedState()
        }
    }

}