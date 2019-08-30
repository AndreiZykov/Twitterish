package com.abaz.twitterish.screen.login

import com.abaz.twitterish.BuildConfig
import com.abaz.twitterish.data.User
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.koin.android.ext.android.inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val loginResponse: Async<ResponseObject<User>> = Uninitialized
) : MvRxState

class LoginMvRxViewModel(initialState: LoginState, private val api: TechTalkApi) :
    MvRxViewModel<LoginState>(initialState = initialState, debugMode = BuildConfig.DEBUG) {

    fun emailChanged(email: String) {
        setState { copy(email = email) }
    }

    fun passwordChanged(password: String) {
        setState { copy(password = password) }
    }

    fun passwordConfirmationChanged(passwordConfirmation: String){
        setState { copy(passwordConfirmation = passwordConfirmation) }
    }

    fun login() {
        withState { state ->
            api.login(state.email, state.password)
                .subscribeOn(io())
                .observeOn(mainThread())
                .execute { copy(loginResponse = it) }
        }
    }

    fun signUp() {
        withState { state ->
            api.signUp(state.email, state.password)
                .subscribeOn(io())
                .observeOn(mainThread())
                .execute { copy(loginResponse = it) }
        }
    }

    companion object : MvRxViewModelFactory<LoginMvRxViewModel, LoginState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: LoginState
        ): LoginMvRxViewModel {
            val api: TechTalkApi by viewModelContext.activity.inject()
            return LoginMvRxViewModel(state, api)
        }

        override fun initialState(viewModelContext: ViewModelContext): LoginState? {
            return LoginState()
        }
    }

}