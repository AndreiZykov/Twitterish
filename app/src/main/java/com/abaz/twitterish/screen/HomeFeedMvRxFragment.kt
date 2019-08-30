package com.abaz.twitterish.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.abaz.printlnDebug
import com.abaz.twitterish.PaginationListener
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.postdetails.PostDetailsFragment
import com.abaz.twitterish.utils.extensions.showIf
import com.abaz.twitterish.utils.extensions.showOrGone
import com.airbnb.mvrx.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedMvRxFragment : BaseTechTalkFragment() {

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
            layoutManager = LinearLayoutManager(context)
            adapter = multiTypeAdapter
            addPaginationListener()
        }

        swipe_layout.addRefreshListener()
    }

    override fun invalidate() = withState(viewModel) { state ->

        printlnDebug("invalidate called: state = $state")

        swipe_layout.isRefreshing = state.feedRequest is Loading && state.feed.isEmpty()

        load_more_progress_bar.showOrGone(state.feedRequest is Loading && state.feed.isNotEmpty())

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

    override fun onBackPressed(): Boolean {
        return true
    }

    private fun SwipeRefreshLayout.addRefreshListener() {
        setOnRefreshListener {
            viewModel.updateFeed()
        }
    }

    private fun RecyclerView.addPaginationListener() {

        addOnScrollListener(object : PaginationListener(HomeFeedMvRxViewModel.ITEMS_PER_PAGE) {

            override val isLoading: Boolean
                get() = withState(viewModel) { state -> state }.feedRequest is Loading

            //TODO: We need a way to determine if this is the last page.
            //Maybe API should return totalPage, currentPage on response?
            override val isLastPage: Boolean
                get() = false

            override fun loadMoreItems() {
                viewModel.fetchFeed()
            }
        })
    }
}
