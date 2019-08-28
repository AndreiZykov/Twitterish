package com.abaz.twitterish.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-07-27
 */
data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,

    @ColumnInfo(name = "USER_ID")
    val userId: Long,

    @ColumnInfo(name = "USER_NAME")
    val userName: String,


    @ColumnInfo(name = "BODY")
    val body: String,


//    @ColumnInfo(name = "DATE") val date: Long = DateTime.now().millis,


    @ColumnInfo(name = "DATE")
//    val date: DateTime? = DateTime.now(),
    val date: Date? = Date(),


    @ColumnInfo(name = "LIKES_RATING") val likesRating: Int = 0,


    @ColumnInfo(name = "REPOST_COUNT") val repostCount: Int = 0,


    @ColumnInfo(name = "REPLY_COUNT") val replyCount: Int,


    //post this was re-posted from
    @ColumnInfo(name = "ORIGINAL_POST_ID") var originalPost: Long? = null,
    //post this was quoted from
    @ColumnInfo(name = "QUOTED_POST_ID") var quotedPost: Post? = null
)