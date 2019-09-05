package com.abaz.twitterish.screen.new_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.utils.extensions.colorById
import com.abaz.twitterish.utils.extensions.onTextChange
import com.abaz.twitterish.utils.extensions.showOrGone
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.fragment_new_post.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-30
 */
class NewPostMvRxFragment : BaseTechTalkFragment() {

    private val viewModel: NewPostViewMvRxModel by fragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_post, c, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? HomeFeedMvRxActivity)?.let { act ->
            act.setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                act.onBackPressed()
            }
            act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            act.supportActionBar?.setHomeButtonEnabled(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        post_body_view.onTextChange(viewModel::onNewPostBodyChanged)
        send_icon.setOnClickListener { viewModel.new() }
    }

    override fun invalidate() = withState(viewModel) { state ->
        user_circle_icon.apply {
            letter = state.userName.substring(0, 1)
            colorById(state.userId)
        }
        val isLoading = state.newPostRequest is Loading
        new_post_progress_bar.showOrGone(isLoading)
        post_body_view.isEnabled = !isLoading
        send_icon.alpha = if(state.newPostBody.isEmpty()) 0.4f else 1F
        if (state.newPostRequest is Success) homeFeedActivity().onBackPressed()

    }

    override fun onBackPressed(): Boolean = false

}