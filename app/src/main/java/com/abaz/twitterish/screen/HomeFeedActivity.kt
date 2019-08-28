package com.abaz.twitterish.screen

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .add(HomeFeedFragment(), "HomeFeedFragment")
            .commit()
    }
}