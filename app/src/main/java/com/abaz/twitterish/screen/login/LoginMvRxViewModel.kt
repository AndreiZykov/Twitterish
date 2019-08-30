package com.abaz.twitterish.screen.login

import com.abaz.twitterish.BuildConfig
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.koin.android.ext.android.inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val loginResponse: Async<ResponseObject<User>> = Uninitialized,
    val isLoggedIn : Boolean = false
) : MvRxState

class LoginMvRxViewModel(initialState: LoginState, val userDataSource: UserDataSource) :
    MvRxViewModel<LoginState>(initialState = initialState, debugMode = BuildConfig.DEBUG) {

    init {
        setState { copy(isLoggedIn = userDataSource.isLoggedIn()) }
    }

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
            userDataSource.login(Username(state.email), Password(state.password))
                .subscribeOn(io())
                .observeOn(mainThread())
                .execute {
                    copy(loginResponse = it, isLoggedIn = userDataSource.isLoggedIn())
                }
        }
    }

    fun signUp() {
        withState { state ->
            userDataSource.signUp(Username(state.email), Password(state.password))
                .subscribeOn(io())
                .observeOn(mainThread())
                .execute { copy(loginResponse = it, isLoggedIn = userDataSource.isLoggedIn()) }
        }
    }

    companion object : MvRxViewModelFactory<LoginMvRxViewModel, LoginState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: LoginState
        ): LoginMvRxViewModel {
            val userDataSource: UserDataSource by viewModelContext.activity.inject()
            return LoginMvRxViewModel(state, userDataSource)
        }

        override fun initialState(viewModelContext: ViewModelContext): LoginState? {
            return LoginState()
        }
    }

}