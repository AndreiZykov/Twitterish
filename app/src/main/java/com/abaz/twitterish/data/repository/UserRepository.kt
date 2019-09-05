package com.abaz.twitterish.data.repository

import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.data.UserDataSource.*
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class UserRepository(
    private val api: TechTalkApi,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : UserDataSource {

    private val isLoggedInSubscription = BehaviorSubject
        .createDefault(IsLoggedIn(!sharedPreferenceRepository.userToken().isNullOrEmpty()))

    override fun isLoggedIn(): IsLoggedIn {
        return isLoggedInSubscription.value ?: IsLoggedIn()
    }

    override fun signOut(): Single<Unit> {
        return Single.create {
            sharedPreferenceRepository.saveUserToken("")
            sharedPreferenceRepository.saveUserId(null)
            isLoggedInSubscription.onNext(IsLoggedIn())
            it.onSuccess(Unit)
        }
    }



    override fun onLogInStateChanged(): Observable<IsLoggedIn> {
        return isLoggedInSubscription
    }

    override fun login(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.login(username, password)
            .doOnNext { userResponse -> saveUser(user = userResponse.responseObject) }
            .doOnNext { isLoggedInSubscription.onNext(IsLoggedIn(true)) }
    }

    override fun signUp(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.signUp(username, password)
            .doOnNext { userResponse -> saveUser(user = userResponse.responseObject) }
            .doOnNext { isLoggedInSubscription.onNext(IsLoggedIn(true)) }
    }

    override fun userName(): Username? = sharedPreferenceRepository.getUserName()

    override fun userId(): Long = sharedPreferenceRepository.getUserId()

    private fun saveUser(user: User) {
        sharedPreferenceRepository.saveUserToken(user.jwt)
        sharedPreferenceRepository.saveUserId(user.id)
        sharedPreferenceRepository.saveUserName(Username(user.username))
    }

}