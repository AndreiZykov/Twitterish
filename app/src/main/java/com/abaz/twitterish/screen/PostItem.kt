package com.abaz.twitterish.screen

import android.graphics.PorterDuff
import com.abaz.printlnDebug
import com.abaz.twitterish.ColorInt
import com.abaz.twitterish.R
import com.abaz.twitterish.db.model.LikeDislikeStatus
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.utils.extensions.hide
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_reply.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-09-02
 */

class PostItem(
    private val post: Post,
    private val onReply: (postId: Long) -> Unit,
    private val onRepost: (postId: Long) -> Unit,
    private val onLike: (postId: Long) -> Unit,
    private val onDislike: (postId: Long) -> Unit
) : Item() {

    private val df: DateFormat = SimpleDateFormat("MMM,dd", Locale.getDefault())

    private val postId = post.id

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            post_body.text = post.body
            user_name.text = post.userName
            post_date_time.text = df.format(post.date!!)
            reply_count_text.text = post.replyCount.toString()
            rating_text.text = post.likesRating.toString()

            reply_layout.setOnClickListener {
                onReply(postId)
            }

            thumbs_up_icon.setOnClickListener {
                onLike(postId)
            }

            thumbs_down_icon.setOnClickListener {
                onDislike(postId)
            }

            val thumbsIconsPair: Pair<Int, Int> = when {
                post.likeDislikeStatus == LikeDislikeStatus.Liked -> Pair(
                    ColorInt.COLOR_ACCENT,
                    ColorInt.GRAY
                )
                post.likeDislikeStatus == LikeDislikeStatus.Disliked -> Pair(
                    ColorInt.GRAY,
                    ColorInt.COLOR_ACCENT
                )
                else -> Pair(ColorInt.GRAY, ColorInt.GRAY)
            }

            if (post.id == 28L) {
                printlnDebug("post.likeDislikeStatus= ${post.likeDislikeStatus}, thumbsIconsPair= $thumbsIconsPair")
            }

            thumbs_up_icon.setColorFilter(thumbsIconsPair.first, PorterDuff.Mode.SRC_ATOP)
            thumbs_down_icon.setColorFilter(thumbsIconsPair.second, PorterDuff.Mode.SRC_ATOP)

            reply_layout.hide()
            share_layout.hide()
        }

    }

    override fun getChangePayload(newItem: com.xwray.groupie.Item<*>?): Any? = post

    override fun getId(): Long = post.id

    override fun getLayout() = R.layout.layout_reply


    companion object {
        fun create(post: Post, vm: HomeFeedMvRxViewModel) = PostItem(
            post = post,
            onReply = { },
            onRepost = { vm.repost(it) },
            onLike = { vm.like(it) },
            onDislike = { vm.dislike(it) }
        )
    }
}