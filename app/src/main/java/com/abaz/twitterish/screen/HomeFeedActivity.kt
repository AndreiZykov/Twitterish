package com.abaz.twitterish.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.MvRxViewModelStore
import com.airbnb.mvrx.MvRxViewModelStoreOwner

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedActivity: BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(HomeFeedFragment(), "HomeFeedFragment")
    }

    fun showFragment(frag: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(android.R.id.content, frag, tag)
            .commit()
    }
}