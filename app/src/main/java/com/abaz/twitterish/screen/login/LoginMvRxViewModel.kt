package com.abaz.twitterish.screen.login

import com.abaz.twitterish.BuildConfig
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.koin.android.ext.android.inject

data class LoginState(
    val username: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val loginResponse: Async<ResponseObject<User>> = Uninitialized,
    val isLoggedIn: Boolean = false,
    val loginError: LoginError? = null
) : MvRxState

enum class LoginError {
    USERNAME_IS_TAKEN,
    USERNAME_NOTE_FOUND,
    EMPTY_USER_NAME,
    EMPTY_PASSWORD,
    EMPTY_PASSWORD_CONFIRMATION,
    PASSWORD_AND_CONFIRMATION_NOT_MATCH,
    INVALID_PASSWORD
}

class LoginMvRxViewModel(initialState: LoginState, private val userDataSource: UserDataSource) :
    MvRxViewModel<LoginState>(initialState = initialState, debugMode = BuildConfig.DEBUG) {

    init {
        userDataSource.onLogInStateChanged()
            .subscribe({ setState { copy(isLoggedIn = it.value) } }) {}.disposeOnClear()
    }

    fun emailChanged(email: String) {
        setState { copy(username = email) }
    }

    fun passwordChanged(password: String) {
        setState { copy(password = password) }
    }

    fun passwordConfirmationChanged(passwordConfirmation: String) {
        setState { copy(passwordConfirmation = passwordConfirmation) }
    }

    fun login() {
        withState { state ->

            val loginError = when {
                state.username.isEmpty() -> LoginError.EMPTY_USER_NAME
                state.password.isEmpty() -> LoginError.EMPTY_PASSWORD
                else -> null
            }

            if (loginError == null) {
                userDataSource.login(Username(state.username), Password(state.password))
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .execute {
                        copy(
                            loginResponse = it,
                            isLoggedIn = userDataSource.isLoggedIn().value,
                            loginError = parseError(it)
                        )
                    }
            } else {
                setState { copy(loginError = loginError) }
            }

        }
    }

    fun signUp() {
        withState { state ->

            val loginError = when {
                state.username.isEmpty() -> LoginError.EMPTY_USER_NAME
                state.password.isEmpty() -> LoginError.EMPTY_PASSWORD
                state.passwordConfirmation.isEmpty() -> LoginError.EMPTY_PASSWORD_CONFIRMATION
                state.password != state.passwordConfirmation -> LoginError.PASSWORD_AND_CONFIRMATION_NOT_MATCH
                else -> null
            }

            if (loginError == null) {
                userDataSource.signUp(Username(state.username), Password(state.password))
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .execute {
                        copy(
                            loginResponse = it,
                            isLoggedIn = userDataSource.isLoggedIn().value,
                            loginError = parseError(it)
                        )
                    }
            } else {
                setState { copy(loginError = loginError) }
            }

        }
    }

    private fun parseError(response: Async<ResponseObject<User>>): LoginError? {
        return if (response is Success) {
            when (response.invoke().errorCode) {
                800 -> LoginError.USERNAME_IS_TAKEN
                801 -> LoginError.USERNAME_NOTE_FOUND
                802 -> LoginError.INVALID_PASSWORD
                else -> null
            }
        } else {
            null
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