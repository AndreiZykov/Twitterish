package com.abaz.twitterish.data.repository

import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable
import io.reactivex.functions.Consumer

class UserRepository(
    private val api: TechTalkApi,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : UserDataSource {

    override fun isLoggedIn(): Boolean {
        return !sharedPreferenceRepository.userToken().isNullOrEmpty()
    }

    override fun login(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.login(username, password)
            .doOnNext { userResponse -> saveUser(user = userResponse.responseObject) }
    }

    override fun signUp(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.signUp(username, password)
            .doOnNext { userResponse -> saveUser(user = userResponse.responseObject) }
    }

    override fun userId(): Long = sharedPreferenceRepository.getUserId()

    private fun saveUser(user: User) {
        sharedPreferenceRepository.saveUserToken(user.jwt)
        sharedPreferenceRepository.saveUserId(user.id)
    }

}