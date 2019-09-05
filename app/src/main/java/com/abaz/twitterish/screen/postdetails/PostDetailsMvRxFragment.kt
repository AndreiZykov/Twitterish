package com.abaz.twitterish.screen.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abaz.printlnDebug
import com.abaz.twitterish.BaseMultiTypeFragment
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.HomeFeedItem
import com.abaz.twitterish.screen.PostItem
import com.abaz.twitterish.utils.extensions.onTextChange
import com.airbnb.mvrx.*
import com.xwray.groupie.Group
import kotlinx.android.synthetic.main.fragment_home_feed.recycler_view
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.fragment_post_details.toolbar
import kotlinx.android.synthetic.main.reply_to_layout.*


/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class PostDetailsMvRxFragment : BaseMultiTypeFragment() {


    private val viewModel: PostDetailsRxViewModel by fragmentViewModel()

    private val postId: Long by args()

    override fun onBackPressed(): Boolean {
        viewModel.resetSelectedPost()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printlnDebug("onCreate")
        viewModel.selectPost(postId)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_details, c, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = homeFeedActivity()
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        printlnDebug("onViewCreated")

        val multiTypeAdapter = adapter

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = multiTypeAdapter
        }

        reply_body_view.onTextChange(viewModel::onReplyTextChanged)

        send_icon.setOnClickListener { viewModel.reply() }

        post_details_swipe_layout.setOnRefreshListener { viewModel.refresh() }

    }

    override fun invalidate() = withState(viewModel) { state ->

        send_icon.isEnabled = state.selectedPostRepliesRequest !is Loading
        post_details_swipe_layout.isRefreshing = state.selectedPostRepliesRequest is Loading

        val list: MutableList<Group> = mutableListOf()
        if (state.selectedPost != null) list.add(HomeFeedItem.create(state.selectedPost, viewModel))
        list.addAll(state.selectedPostReplies.map { post -> PostItem.create(post, viewModel) })
        adapter.updateAsync(list)

        if (reply_body_view.text.toString() != state.replyBody) {
            reply_body_view.setText(state.replyBody)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                homeFeedActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(postId: Long) = PostDetailsMvRxFragment().apply {
            this.arguments = arg(postId)
        }

        private fun arg(postId: Long): Bundle {
            val args = Bundle()
            args.putLong(MvRx.KEY_ARG, postId)
            return args
        }
    }
}