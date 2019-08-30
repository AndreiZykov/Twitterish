package com.abaz.twitterish.screen

import com.airbnb.mvrx.BaseMvRxFragment

abstract class BaseTechTalkFragment : BaseMvRxFragment() {
    abstract fun onBackPressed(): Boolean
}