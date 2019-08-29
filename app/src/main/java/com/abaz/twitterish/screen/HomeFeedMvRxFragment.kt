package com.abaz.twitterish.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abaz.printlnDebug
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.postdetails.PostDetailsFragment
import com.airbnb.mvrx.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedMvRxFragment : BaseMvRxFragment() {

    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()


    private val adapter: GroupAdapter<*>
        get() = (recycler_view?.adapter as? GroupAdapter<*>)
            ?: GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.asyncSubscribe(HomeFeedState::likeRequest, onSuccess = {
//            printlnDebug("  viewModel.asyncSubscribe--likeRequest onSuccess")
//        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val multiTypeAdapter = adapter.apply {
            setOnItemClickListener { item, _ ->
                goToDetails(item.id)
            }
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@HomeFeedMvRxFragment.context)
            adapter = multiTypeAdapter
        }

        swipe_layout.setOnRefreshListener {
            viewModel.fetchFeed()
        }

    }

    override fun invalidate() = withState(viewModel) { state ->
//        swipe_layout.isRefreshing = state.feedRequest is Loading
//        val posts = state.feedRequest()?.responseList
//        if (posts != null) {
//            adapter.update(posts.map { post ->
//                HomeFeedItem(post = post,
//                    onReply = { viewModel.reply(it) },
//                    onRepost = { viewModel.repost(it) },
//                    onLike = { viewModel.rating(it) },
//                    onDislike = { viewModel.dislike(it) }
//                )
//            })
//        }


        printlnDebug("invalidate called: state = $state")

        swipe_layout.isRefreshing = state.feedRequest is Loading

        adapter.updateAsync(state.feed.map { post ->
            HomeFeedItem(post = post,
                onReply = { viewModel.reply(it) },
                onRepost = { viewModel.repost(it) },
                onLike = { viewModel.like(it) },
                onDislike = { viewModel.dislike(it) }
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispose()
    }

    private fun goToDetails(id: Long) {
        (activity as? HomeFeedMvRxActivity)?.showFragment(
            PostDetailsFragment.newInstance(id), "PostDetailsFragment"
        )
    }
}
