package com.abaz.twitterish.screen

import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedFragment: BaseMvRxFragment() {

    private val viewModel: HomeFeedViewModel by fragmentViewModel()

    override fun invalidate() = withState(viewModel) { state ->
        progress_bar.visibility = if(state.feed is Loading) View.VISIBLE else View.GONE
    }
}