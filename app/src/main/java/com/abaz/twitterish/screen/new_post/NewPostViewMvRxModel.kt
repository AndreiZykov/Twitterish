package com.abaz.twitterish.screen.new_post

import com.abaz.twitterish.BuildConfig
import com.abaz.twitterish.data.PostDataSource
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.utils.DEBOUNCE_VALUE
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


data class NewPostState(
    val userId: Long = 0,
    val userName: String = "A",
    val newPostBody: String = "",
    val newPostRequest: Async<Post> = Uninitialized
) : MvRxState

class NewPostViewMvRxModel(initialState: NewPostState, private val postDataSource: PostDataSource) :
    MvRxViewModel<NewPostState>(initialState = initialState, debugMode = BuildConfig.DEBUG) {


    fun onNewPostBodyChanged(newPostBody: String) = withState {
        setState { copy(newPostBody = newPostBody) }
    }

    fun new() = withState { state ->
        if (state.newPostBody.isEmpty()) return@withState
        postDataSource.new(state.newPostBody)
            .debounce(DEBOUNCE_VALUE, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .execute {
                copy(newPostRequest = it, newPostBody = "")
            }
    }


    companion object : MvRxViewModelFactory<NewPostViewMvRxModel, NewPostState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: NewPostState
        ): NewPostViewMvRxModel {
            val postDataSource: PostDataSource by viewModelContext.activity.inject()
            return NewPostViewMvRxModel(state, postDataSource)
        }

        override fun initialState(viewModelContext: ViewModelContext): NewPostState? {
            val userDataSource: UserDataSource by viewModelContext.activity.inject()
            return NewPostState(
                userName = userDataSource.userName()?.value.orEmpty().ifEmpty { "A" },
                userId = userDataSource.userId()
            )
        }

    }

}