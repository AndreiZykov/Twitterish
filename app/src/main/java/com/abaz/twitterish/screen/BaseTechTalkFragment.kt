package com.abaz.twitterish.screen

import com.airbnb.mvrx.BaseMvRxFragment

abstract class BaseTechTalkFragment : BaseMvRxFragment() {


    abstract fun onBackPressed(): Boolean

    fun close() {
        (activity as? HomeFeedMvRxActivity)?.close()
    }

    fun homeFeedActivity(): HomeFeedMvRxActivity = (activity as HomeFeedMvRxActivity)
}