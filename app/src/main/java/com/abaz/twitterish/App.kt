package com.abaz.twitterish

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid



/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}