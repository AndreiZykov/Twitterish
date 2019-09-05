package com.abaz.twitterish.data

import com.abaz.twitterish.db.model.User
import com.abaz.twitterish.network.response.ResponseObject
import com.abaz.twitterish.utils.Password
import com.abaz.twitterish.utils.Username
import io.reactivex.Observable
import io.reactivex.Single

interface UserDataSource {
    fun login(username: Username, password: Password): Observable<ResponseObject<User>>
    fun signUp(username: Username, password: Password): Observable<ResponseObject<User>>
    fun signOut(): Single<Unit>
    fun isLoggedIn(): IsLoggedIn
    fun userId(): Long
    fun onLogInStateChanged(): Observable<IsLoggedIn>
    fun userName(): Username?

    class IsLoggedIn(val value: Boolean = false)
}