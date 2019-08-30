package com.abaz.twitterish.data.repository

import android.content.Context

// temporary solution
class SharedPreferenceRepository(private val ctx: Context) {

    private var bareadToker: String? = null

    fun userToken(): String? {
        if (bareadToker == null) {
            bareadToker = getStringFromSharedPreference(BEARER_TOKEN_STRING_IDENTIFIER)
        }
        return bareadToker
    }

    fun saveUserToken(token: String?) {
        saveStringToSharedPreferences(BEARER_TOKEN_STRING_IDENTIFIER, token)
        bareadToker = token
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
    }

}