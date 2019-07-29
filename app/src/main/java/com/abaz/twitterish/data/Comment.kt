package com.abaz.twitterish.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-07-29
 */
@Entity(tableName = "Comment")
data class Comment(@PrimaryKey(autoGenerate = false) val id: Long,
                   @ColumnInfo(name = "USER_ID") val userId: Long,
                   @ColumnInfo(name = "POST_ID") val postId: Long,
                   @ColumnInfo(name = "BODY") val body: String,
                   @ColumnInfo(name = "DATE") val date: Date,
                   @ColumnInfo(name = "LIKES_COUNT") val likesCount: Int)