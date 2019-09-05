package com.abaz.twitterish.screen.homefeed

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
import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.abaz.twitterish.screen.HomeFeedItem
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.screen.new_post.NewPostMvRxFragment
import com.abaz.twitterish.screen.postdetails.PostDetailsMvRxFragment
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_out_button.setOnClickListener {
            viewModel.signOut()
        }

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

        new_post_fab.setOnClickListener(::goToNewPostView)

    }

    override fun invalidate() = withState(viewModel) { state ->

        printlnDebug("invalidate called: state = $state")

        if(!state.isLoggedIn){
            // hack :)
            homeFeedActivity().supportFragmentManager.popBackStack()
        }

        swipe_layout.isRefreshing = state.feedRequest is Loading && state.feed.isEmpty()

        load_more_progress_bar.showOrGone(state.feedRequest is Loading && state.feed.isNotEmpty())

        val postsItems = state.feed.map { post -> HomeFeedItem.create(post, viewModel) }

        adapter.updateAsync(postsItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispose()
    }

    private fun goToDetails(id: Long) {
        (activity as? HomeFeedMvRxActivity)?.showFragment(
            PostDetailsMvRxFragment.newInstance(id), "PostDetailsMvRxFragment"
        )
    }


//    private fun goToDetails(item: Item) {
//        (activity as? HomeFeedMvRxActivity)?.showFragment(
//            PostDetailsMvRxFragment.newInstance(id), "PostDetailsMvRxFragment"
//        )
//    }

    override fun onBackPressed(): Boolean {
        return true
    }

    private fun goToNewPostView(v: View) {
        (activity as? HomeFeedMvRxActivity)
            ?.showFragment(NewPostMvRxFragment(), "NewPostMvRxFragment")
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
