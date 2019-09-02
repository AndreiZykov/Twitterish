package com.abaz.twitterish

import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-09-02
 */
abstract class BaseMultiTypeFragment : BaseTechTalkFragment(){
    protected val adapter: GroupAdapter<*>
        get() = (recycler_view?.adapter as? GroupAdapter<*>)
            ?: GroupAdapter<ViewHolder>()

}