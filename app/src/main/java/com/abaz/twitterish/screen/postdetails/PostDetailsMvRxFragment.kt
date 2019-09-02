package com.abaz.twitterish.screen.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abaz.printlnDebug
import com.abaz.twitterish.BaseMultiTypeFragment
import com.abaz.twitterish.R
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.db.model.PostBodyParams
import com.abaz.twitterish.screen.HomeFeedItem
import com.abaz.twitterish.screen.HomeFeedMvRxViewModel
import com.abaz.twitterish.screen.PostItem
import com.abaz.twitterish.utils.extensions.add
import com.abaz.twitterish.utils.extensions.clear
import com.airbnb.mvrx.*
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*
import kotlinx.android.synthetic.main.fragment_home_feed.recycler_view
import kotlinx.android.synthetic.main.fragment_new_post.*
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.fragment_post_details.toolbar
import kotlinx.android.synthetic.main.reply_to_layout.*
import kotlinx.android.synthetic.main.reply_to_layout.send_icon

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class PostDetailsMvRxFragment : BaseMultiTypeFragment() {


    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()

    private val postId: Long by args()

    override fun onBackPressed(): Boolean {
        viewModel.resetSelectedPost()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printlnDebug("onCreate")
        viewModel.selectPost(postId)
        viewModel.fetchReplies(postId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity  = homeFeedActivity()
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        printlnDebug("onViewCreated")

        val multiTypeAdapter = adapter.apply {
            setOnItemClickListener { item, _ ->

            }
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = multiTypeAdapter
        }



        send_icon.setOnClickListener {
            viewModel.reply(postId, PostBodyParams(reply_body_view.text.toString(), 23))
        }
    }

    override fun invalidate() = withState(viewModel) { state ->

        printlnDebug("invalidate PostDetailsMvRxFragment")

        val selectedPost = state.feed.firstOrNull {
            it.id == state.selectedPostId
        }

        val list: MutableList<Group> = mutableListOf<Group>().apply {
            add(HomeFeedItem.create(selectedPost!!, viewModel))
        }

        adapter.updateAsync(list)

//        if(state.selectedPostRepliesRequest is Loading) return@withState

        send_icon.isEnabled = state.selectedPostRepliesRequest !is Loading

        if (state.selectedPostRepliesRequest !is Loading) {

            reply_body_view.clear()

            if (!state.selectedPostRepliesRequest()?.responseList.isNullOrEmpty()) {
                list.addAll(state.selectedPostReplies.map {
                    PostItem.create(it, viewModel)
                })

                adapter.updateAsync(list)
            }
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

//        private fun arg(post: Post): Bundle {
//            val args = Bundle()
//            args.putParcelable(MvRx.KEY_ARG, post)
//            return args
//        }
    }
}