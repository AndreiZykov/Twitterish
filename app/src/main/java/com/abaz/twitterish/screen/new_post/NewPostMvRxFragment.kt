package com.abaz.twitterish.screen.new_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.abaz.printlnDebug
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.screen.homefeed.HomeFeedMvRxViewModel
import com.abaz.twitterish.utils.extensions.addTo
import com.abaz.twitterish.utils.extensions.clear
import com.abaz.twitterish.utils.extensions.showOrGone
import com.airbnb.mvrx.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_new_post.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-30
 */
class NewPostMvRxFragment : BaseTechTalkFragment() {

    private val disposables = CompositeDisposable()

    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        withState(viewModel) {
            printlnDebug("newPostRequest initial state")
            printlnDebug("it.complete::${it.newPostRequest.complete}")
            printlnDebug("it.shouldLoad::${it.newPostRequest.shouldLoad}")
        }

        viewModel.subscribe {
            if (it.newPostRequest is Success) {
                printlnDebug("newPostRequest is Success")

                printlnDebug("it.complete::${it.newPostRequest.complete}")
                printlnDebug("it.shouldLoad::${it.newPostRequest.shouldLoad}")
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        send_icon.setOnClickListener {
            post_body_view.isEnabled = false
            val body = post_body_view.text.toString()
            viewModel.new(body)
        }

        viewModel.onPostCreated
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            { onNewPostSuccess() },
            { it.printStackTrace() })
            .addTo(disposables)
    }


    private fun onNewPostSuccess() {
        post_body_view.clear()
        (activity as? HomeFeedMvRxActivity)?.close()
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

//    private fun onBackPressed() {
//        (activity as? HomeFeedMvRxActivity)?.onBackPressed()
//    }

    override fun onBackPressed(): Boolean {
//        (activity as? HomeFeedMvRxActivity)?.onBackPressed()
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
        //viewModel.dispose()
    }

}


fun Toolbar.navClicks(): Observable<Unit> = Observable.create { emitter ->
    setNavigationOnClickListener {
        emitter.onNext(Unit)
    }
}