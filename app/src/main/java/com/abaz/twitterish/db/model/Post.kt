package com.abaz.twitterish.db.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-07-27
 */

enum class LikeDislikeStatus {
    Liked,
    Disliked,
    Neutral
}


typealias Posts = List<Post>
fun Posts.findPost(id: Long) = firstOrNull { it.id == id }

data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,

    @ColumnInfo(name = "USER_ID")
    val userId: Long,

    @ColumnInfo(name = "USER_NAME")
    val userName: String,

    @ColumnInfo(name = "BODY")
    val body: String,

    @ColumnInfo(name = "DATE")
    val date: Date? = Date(),

    @ColumnInfo(name = "LIKES_RATING")
    val likesRating: Int = 0,

    @ColumnInfo(name = "REPOST_COUNT")
    val repostCount: Int = 0,

    @ColumnInfo(name = "REPLY_COUNT")
    val replyCount: Int = 0,

    //post this was re-posted from
    @ColumnInfo(name = "ORIGINAL_POST_ID")
    var originalPost: Long? = null,

    //post this was quoted from
    @ColumnInfo(name = "QUOTED_POST_ID")
    var quotedPost: Post? = null,

    @ColumnInfo(name = "USER_POST_EXTRAS")
    var authorizedUserExtras: PostExtras? = null


) {
    val likeDislikeStatus: LikeDislikeStatus
        get() = when {
            authorizedUserExtras?.rating ?: 0 > 0 -> LikeDislikeStatus.Liked
            authorizedUserExtras?.rating ?: 0 < 0 -> LikeDislikeStatus.Disliked
            else -> LikeDislikeStatus.Neutral
        }

    val isRepostedByMe: Boolean
        get() = authorizedUserExtras?.repost ?: 0 > 0


}