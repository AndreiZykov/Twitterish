package com.abaz.twitterish.screen.postdetails

import android.os.Bundle
import com.abaz.twitterish.screen.HomeFeedMvRxViewModel
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.args

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class PostDetailsFragment: BaseMvRxFragment() {


    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()

    private val postId: Long by args()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun invalidate() {

    }

    companion object {
        fun newInstance(postId: Long) = PostDetailsFragment().apply {
            this.arguments = arg(postId)
        }
        private fun arg(postId: Long): Bundle {
            val args = Bundle()
            args.putLong(MvRx.KEY_ARG, postId)
            return args
        }
    }
}