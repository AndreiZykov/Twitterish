package com.abaz.twitterish.mvrx

import com.abaz.twitterish.network.DadJokeService
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import io.reactivex.schedulers.Schedulers

private const val JOKES_PER_PAGE = 5

//data class DadJokeIndexState(
//    /** We use this request to store the list of all jokes. */
//    val jokes: List<Joke> = emptyList(),
//    /** We use this Async to keep track of the state of the current network request. */
//    val request: Async<JokesResponse> = Uninitialized
//) : MvRxState

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class DadJokeIndexViewModel
//class DadJokeIndexViewModel(
//    initialState: DadJokeIndexState,
//    private val dadJokeService: DadJokeService
//) : MvRxViewModel<DadJokeIndexState>(initialState) {
//
//    init {
//        fetchNextPage()
//    }
//
//    fun fetchNextPage() = withState { state ->
//        if (state.request is Loading) return@withState
//
//
//        dadJokeService
//            .search(page = state.jokes.size / JOKES_PER_PAGE + 1, limit = JOKES_PER_PAGE)
//            .subscribeOn(Schedulers.io())
//            .execute { copy(request = it, jokes = jokes + (it()?.results ?: emptyList())) }
//    }
//
//    /**
//     * If you implement MvRxViewModelFactory in your companion object, MvRx will use that to create
//     * your ViewModel. You can use this to achieve constructor dependency injection with MvRx.
//     *
//     * @see MvRxViewModelFactory
//     */
//    companion object : MvRxViewModelFactory<DadJokeIndexViewModel, DadJokeIndexState> {
//
//        override fun create(viewModelContext: ViewModelContext, state: DadJokeIndexState): DadJokeIndexViewModel {
//            val service: DadJokeService by viewModelContext.activity.inject()
//            return DadJokeIndexViewModel(state, service)
//        }
//    }
//}