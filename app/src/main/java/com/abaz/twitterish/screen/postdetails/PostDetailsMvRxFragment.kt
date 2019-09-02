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
import com.abaz.twitterish.screen.HomeFeedItem
import com.abaz.twitterish.screen.HomeFeedMvRxViewModel
import com.abaz.twitterish.screen.PostItem
import com.abaz.twitterish.utils.extensions.add
import com.airbnb.mvrx.*
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home_feed.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class PostDetailsMvRxFragment : BaseMultiTypeFragment() {


    private val viewModel: HomeFeedMvRxViewModel by activityViewModel()

    private val postId: Long by args()

    override fun onBackPressed(): Boolean {
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


        if (state.selectedPostRepliesRequest !is Loading) {

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