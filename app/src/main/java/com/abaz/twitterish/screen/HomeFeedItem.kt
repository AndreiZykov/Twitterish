package com.abaz.twitterish.screen

import android.graphics.PorterDuff
import com.abaz.printlnDebug
import com.abaz.twitterish.ColorInt
import com.abaz.twitterish.R
import com.abaz.twitterish.db.model.LikeDislikeStatus
import com.abaz.twitterish.db.model.Post
import com.abaz.twitterish.screen.homefeed.HomeFeedMvRxViewModel
import com.abaz.twitterish.screen.postdetails.PostDetailsRxViewModel
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_post_2.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.abaz.twitterish.utils.extensions.colorById


/**
 * import kotlinx.android.synthetic.main.layout_post.*
 *
 *
 * import com.xwray.groupie.kotlinandroidextensions.ViewHolder
 *
 * import com.xwray.groupie.ViewHolder
 */

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

sealed class PostExtrasIntent(open val postId: Long) {
    data class Reply(override val postId: Long) : PostExtrasIntent(postId)
    data class Repost(override val postId: Long) : PostExtrasIntent(postId)
    data class Like(override val postId: Long) : PostExtrasIntent(postId)
    data class Dislike(override val postId: Long) : PostExtrasIntent(postId)
}

class HomeFeedItem(
    private val post: Post,
    private val onReply: (postId: Long) -> Unit,
    private val onRepost: (postId: Long) -> Unit,
    private val onLike: (postId: Long) -> Unit,
    private val onDislike: (postId: Long) -> Unit
) : Item() {

//    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val df: DateFormat = SimpleDateFormat("MMM,dd", Locale.getDefault())

//    private val intents: PublishSubject<PostExtrasIntent> = PublishSubject.create()

    private val postId = post.id

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            post_body.text = post.body
            user_name.text = post.userName
            user_circle_icon.apply {
                letter = post.userName.substring(0,1)
                colorById(post.userId)
            }
            post_date_time.text = df.format(post.date!!)
            reply_count_text.text = post.replyCount.toString()
//            repost_count_text.text = post.repostCount.toString()
            rating_text.text = post.likesRating.toString()

//            Observable.merge(
//                reply_layout.clicks().map { Reply(postId) },
//                repost_layout.clicks().map { Repost(postId) },
//                thumbs_up_icon.clicks().map { Like(postId) },
//                thumbs_down_icon.clicks().map { Dislike(postId) }
//            ).subscribe(intents)


            reply_layout.setOnClickListener {
                onReply(postId)
            }

//            repost_layout.setOnClickListener {
//                onRepost(postId)
//            }

            thumbs_up_icon.setOnClickListener {
                onLike(postId)
            }

            thumbs_down_icon.setOnClickListener {
                onDislike(postId)
            }



//            share_text.text = "SHARE"

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

//            val repostIconColor = if (post.isRepostedByMe) {
//                ColorInt.COLOR_ACCENT
//            } else {
//                ColorInt.GRAY
//            }

//            repost_count_icon.setColorFilter(repostIconColor, PorterDuff.Mode.SRC_ATOP)

        }

    }

    override fun getChangePayload(newItem: com.xwray.groupie.Item<*>?): Any? = post

    override fun getId(): Long = post.id

    override fun getLayout() = R.layout.layout_post_2

    companion object {
        fun create(post: Post, vm: HomeFeedMvRxViewModel) = HomeFeedItem (
            post = post,
            onReply = {  },
            onRepost = { vm.repost(it) },
            onLike = { vm.like(it) },
            onDislike = { vm.dislike(it) }
        )
        fun create(post: Post, vm: PostDetailsRxViewModel) = HomeFeedItem (
            post = post,
            onReply = {  },
            onRepost = { vm.repost(it) },
            onLike = { vm.like(it) },
            onDislike = { vm.dislike(it) }
        )
    }
}