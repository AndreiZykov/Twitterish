package com.abaz.twitterish.screen.login

import com.abaz.twitterish.BuildConfig
import com.abaz.twitterish.R
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.mvrx.MvRxViewModel
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.DEBOUNCE_VALUE
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import com.airbnb.mvrx.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

data class LoginState(
    val username: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val loginResponse: Async<ResponseObject<User>> = Uninitialized,
    val isLoggedIn: Boolean = false,
    val loginError: LoginError? = null
) : MvRxState

enum class LoginError(val message: Int) {
    USERNAME_IS_TAKEN(R.string.username_is_taken),
    USER_NOT_FOUND(R.string.usern_not_found),
    EMPTY_USER_NAME(R.string.user_name_is_empty),
    EMPTY_PASSWORD(R.string.password_is_empty),
    EMPTY_PASSWORD_CONFIRMATION(R.string.confirm_password_is_empty),
    PASSWORD_AND_CONFIRMATION_NOT_MATCH(R.string.password_do_not_match),
    INVALID_PASSWORD(R.string.invalid_password)
}

class LoginMvRxViewModel(initialState: LoginState, private val userDataSource: UserDataSource) :
    MvRxViewModel<LoginState>(initialState = initialState, debugMode = BuildConfig.DEBUG) {

    init {
        userDataSource.onLogInStateChanged()
            .subscribe({ setState { copy(isLoggedIn = it.value) } }) {}.disposeOnClear()
    }

    fun emailChanged(email: String) {
        setState { copy(username = email, loginError = null) }
    }

    fun passwordChanged(password: String) {
        setState { copy(password = password, loginError = null) }
    }

    fun passwordConfirmationChanged(passwordConfirmation: String) {
        setState { copy(passwordConfirmation = passwordConfirmation, loginError = null) }
    }

    fun login() {
        withState { state ->

            setState { copy(loginError = null) }

            val loginError = when {
                state.username.isEmpty() -> LoginError.EMPTY_USER_NAME
                state.password.isEmpty() -> LoginError.EMPTY_PASSWORD
                else -> null
            }

            if (loginError == null) {
                userDataSource.login(Username(state.username), Password(state.password))
                    .debounce(DEBOUNCE_VALUE, TimeUnit.MILLISECONDS)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .execute { response ->
                        copy(
                            loginResponse = response,
                            isLoggedIn = userDataSource.isLoggedIn().value,
                            username = if (userDataSource.isLoggedIn().value) "" else username,
                            password = if (userDataSource.isLoggedIn().value) "" else password,
                            loginError = parseError(response)
                        )
                    }
            } else {
                setState { copy(loginError = loginError) }
            }

        }
    }

    fun signUp() {
        withState { state ->

            setState { copy(loginError = null) }

            val loginError = when {
                state.username.isEmpty() -> LoginError.EMPTY_USER_NAME
                state.password.isEmpty() -> LoginError.EMPTY_PASSWORD
                state.passwordConfirmation.isEmpty() -> LoginError.EMPTY_PASSWORD_CONFIRMATION
                state.password != state.passwordConfirmation -> LoginError.PASSWORD_AND_CONFIRMATION_NOT_MATCH
                else -> null
            }

            if (loginError == null) {
                userDataSource.signUp(Username(state.username), Password(state.password))
                    .debounce(DEBOUNCE_VALUE, TimeUnit.MILLISECONDS)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .execute {
                        copy(
                            loginResponse = it,
                            isLoggedIn = userDataSource.isLoggedIn().value,
                            username = if (userDataSource.isLoggedIn().value) "" else username,
                            password = if (userDataSource.isLoggedIn().value) "" else password,
                            passwordConfirmation = if (userDataSource.isLoggedIn().value) "" else passwordConfirmation,
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
                801 -> LoginError.USER_NOT_FOUND
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