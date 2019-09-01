package com.abaz.twitterish.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abaz.twitterish.R
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.screen.login.LoginFragment
import com.airbnb.mvrx.BaseMvRxActivity
import org.koin.android.ext.android.inject

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedMvRxActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(LoginFragment(), "LoginFragment", false)
    }

    fun goToFeedFragment() {
        showFragment(HomeFeedMvRxFragment(), "HomeFeedMvRxFragment")
    }

    override fun onBackPressed() {
        val fragment = (this.supportFragmentManager.findFragmentById(android.R.id.content)
                as? BaseTechTalkFragment)
        if (fragment?.onBackPressed() == true) return
        super.onBackPressed()
    }

    fun close() {
        super.onBackPressed()
    }

    fun showFragment(frag: Fragment, tag: String, animate: Boolean = true) {
        val t = supportFragmentManager.beginTransaction()
        if (animate) {
            t.setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right
            )
        }
        t.addToBackStack(tag)
            .replace(android.R.id.content, frag)
            .commit()
    }

}