package com.abaz.twitterish.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abaz.twitterish.R
import com.abaz.twitterish.network.response.PostListReponse
import com.abaz.twitterish.screen.postdetails.PostDetailsFragment
import com.airbnb.mvrx.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class HomeFeedFragment: BaseMvRxFragment() {

    private val viewModel: HomeFeedViewModel by activityViewModel()

    private val adapter:  GroupAdapter<*>
        get() = (recycler_view?.adapter as? GroupAdapter<*>)
            ?: GroupAdapter<ViewHolder>()

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
            layoutManager = LinearLayoutManager(this@HomeFeedFragment.context)
            adapter  = multiTypeAdapter
        }

        swipe_layout.setOnRefreshListener {
            viewModel.fetchFeed()
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        swipe_layout.isRefreshing = state.feed is Loading
        val posts = state.feed()?.responseList
        if(posts != null){
            adapter.update(posts.map { HomeFeedItem(it) })
        }
    }

    private fun goToDetails(id: Long) {
        (activity as? HomeFeedActivity)?.showFragment(
            PostDetailsFragment.newInstance(id), "PostDetailsFragment"
        )
    }
}
