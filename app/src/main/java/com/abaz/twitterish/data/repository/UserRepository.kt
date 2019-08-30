package com.abaz.twitterish.data.repository

import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable

class UserRepository(
    private val api: TechTalkApi,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : UserDataSource {

    override fun isLoggedIn(): Boolean {
        return !sharedPreferenceRepository.userToken().isNullOrEmpty()
    }

    override fun login(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.login(username, password)
            .doOnNext { sharedPreferenceRepository.saveUserToken(it?.responseObject?.jwt) }
    }

    override fun signUp(username: Username, password: Password): Observable<ResponseObject<User>> {
        return api.signUp(username, password)
            .doOnNext { sharedPreferenceRepository.saveUserToken(it?.responseObject?.jwt) }
    }
}