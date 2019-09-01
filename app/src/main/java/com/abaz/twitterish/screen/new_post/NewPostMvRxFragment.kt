package com.abaz.twitterish.screen.new_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.abaz.printlnDebug
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.screen.HomeFeedMvRxViewModel
import com.abaz.twitterish.screen.HomeFeedState
import com.abaz.twitterish.utils.extensions.clear
import com.abaz.twitterish.utils.extensions.showOrGone
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_new_post.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-30
 */
class NewPostMvRxFragment : BaseMvRxFragment() {

    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.asyncSubscribe(HomeFeedState::newPostRequest, onSuccess = {
            printlnDebug("  viewModel.asyncSubscribe--newPostRequest onSuccess")
            post_body_view.apply {
                isEnabled = true
                clear()
            }
            onnBackPressed()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        send_icon.setOnClickListener {
            viewModel.new(post_body_view.let {
                it.isEnabled = false
                it.text.toString()
            })
        }
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

    override fun invalidate() = withState(viewModel) { state ->
        val isLoading = state.newPostRequest is Loading
        new_post_progress_bar.showOrGone(isLoading)
        post_body_view.isEnabled = !isLoading
    }

    private fun onnBackPressed() {
        (activity as? HomeFeedMvRxActivity)?.onBackPressed()
    }

}


fun Toolbar.navClicks(): Observable<Unit> = Observable.create { emitter ->
    setNavigationOnClickListener {
        emitter.onNext(Unit)
    }
}