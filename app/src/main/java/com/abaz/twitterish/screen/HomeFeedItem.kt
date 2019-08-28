package com.abaz.twitterish.screen

import com.abaz.twitterish.R
import com.abaz.twitterish.data.Post
import com.abaz.twitterish.network.response.PostListReponse

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_post.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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

class HomeFeedItem(private val post: Post) : Item() {

//    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val df: DateFormat = SimpleDateFormat("MMM,dd", Locale.getDefault())

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            post_body.text = post.body
            user_name.text = post.userName
            post_date_time.text = df.format(post.date!!)
            reply_count_text.text = post.replyCount.toString()
            repost_count_text.text = post.repostCount.toString()
            rating_text.text = post.likesRating.toString()
        }

    }

    override fun getId(): Long = post.id

    override fun getLayout() = R.layout.layout_post

}