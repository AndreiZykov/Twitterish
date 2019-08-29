package com.abaz.twitterish.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.BaseMvRxActivity

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedMvRxActivity: BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(HomeFeedMvRxFragment(), "HomeFeedMvRxFragment")
    }

    fun showFragment(frag: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(android.R.id.content, frag, tag)
            .commit()
    }
}