package com.abaz.twitterish.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: Anthony Busto
 * @date:   2019-07-27
 */
@Entity(tableName = "Post")
data class Post(@PrimaryKey(autoGenerate = false) val id: Long,
                @ColumnInfo(name = "USER_ID") val userId: Long,
                @ColumnInfo(name = "BODY") val body: String,
                @ColumnInfo(name = "LIKES_COUNT") val likesCount: Int,
                @ColumnInfo(name = "COMMENTS_COUNT") val commentsCount: Int)