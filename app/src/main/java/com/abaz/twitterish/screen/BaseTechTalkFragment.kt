package com.abaz.twitterish.screen

import com.airbnb.mvrx.BaseMvRxFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

abstract class BaseTechTalkFragment : BaseMvRxFragment() {




    abstract fun onBackPressed(): Boolean

    fun close() {
        (activity as? HomeFeedMvRxActivity)?.close()
    }
}