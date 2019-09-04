package com.abaz.twitterish.data.repository

import android.content.Context

// temporary solution
class SharedPreferenceRepository(private val ctx: Context) {

    private var barearToker: String? = null

    private var userId: Long? = null

    fun userToken(): String? {
        if (barearToker == null) {
            barearToker = getStringFromSharedPreference(BEARER_TOKEN_STRING_IDENTIFIER)
        }
        return barearToker
    }

    fun saveUserToken(token: String?) {
        saveStringToSharedPreferences(BEARER_TOKEN_STRING_IDENTIFIER, token)
        barearToker = token
    }

    fun saveUserId(id: Long?) {
        saveStringToSharedPreferences(USER_ID_STRING_IDENTIFIER, "$id")
        userId = id
    }

    fun getUserId(): Long {
        if (userId == null) {
            userId = runCatching {
                getStringFromSharedPreference(USER_ID_STRING_IDENTIFIER)?.toLong()
            }.getOrNull()
        }
        return userId ?: INVALID_USER_ID
    }


    private fun saveStringToSharedPreferences(key: String, value: String?) {
        ctx.getSharedPreferences(TWITTER_ISH_SHARED_PREFERENCE_NAME, 0)
            .edit().putString(key, value).apply()
    }

    private fun getStringFromSharedPreference(key: String): String? {
        return ctx.getSharedPreferences(TWITTER_ISH_SHARED_PREFERENCE_NAME, 0)
            .getString(key, null)
    }

    companion object {
        private const val TWITTER_ISH_SHARED_PREFERENCE_NAME = "TWITTER_ISH_SHARED_PREFERENCE_NAME"
        private const val BEARER_TOKEN_STRING_IDENTIFIER = "BEARER_TOKEN_STRING_IDENTIFIER"
        private const val USER_ID_STRING_IDENTIFIER = "USER_ID_STRING_IDENTIFIER"
        private const val INVALID_USER_ID = -1L
    }

}