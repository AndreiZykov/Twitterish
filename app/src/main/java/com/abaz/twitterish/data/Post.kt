package com.abaz.twitterish.data

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
    val replyCount: Int,

    //post this was re-posted from
    @ColumnInfo(name = "ORIGINAL_POST_ID")
    var originalPost: Long? = null,

    //post this was quoted from
    @ColumnInfo(name = "QUOTED_POST_ID")
    var quotedPost: Post? = null,

    @ColumnInfo(name = "USER_POST_EXTRAS")
    var authorizedUserExtras: PostExtras? = null

//    var likeDislikeStatus: LikeDislikeStatus =  LikeDislikeStatus.Neutral,
//
//    var isRepostedByMe: Boolean = false


) {

    val likeDislikeStatus: LikeDislikeStatus
        get() = when {
            authorizedUserExtras?.like ?: 0 > 0 -> LikeDislikeStatus.Liked
            authorizedUserExtras?.dislike ?: 0 > 0 -> LikeDislikeStatus.Disliked
            else -> LikeDislikeStatus.Neutral
        }

    val isRepostedByMe: Boolean
        get() = authorizedUserExtras?.repost ?: 0 > 0

    fun updatePostExtrasLikedByMe(isLikedByMe: Boolean) {
        if(authorizedUserExtras != null) {
            authorizedUserExtras = authorizedUserExtras?.copy(
                like = if(isLikedByMe) 1 else 0,
                dislike = 0
            )
        }
    }

    fun updatePostExtrasDislikedByMe(isDislikedByMe: Boolean) {
        if(authorizedUserExtras != null) {
            authorizedUserExtras = authorizedUserExtras?.copy(
                dislike = if(isDislikedByMe) 1 else 0,
                like = 0
            )
        }
    }


//    init {
//        likeDislikeStatus = when {
//            authorizedUserExtras?.like ?: 0 > 0 -> LikeDislikeStatus.Liked
//            authorizedUserExtras?.dislike ?: 0 > 0 -> LikeDislikeStatus.Disliked
//            else -> LikeDislikeStatus.Neutral
//        }
//
//        isRepostedByMe = authorizedUserExtras?.repost ?: 0 > 0
//    }



}