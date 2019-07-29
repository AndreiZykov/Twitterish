package com.abaz.twitterish.db

import androidx.room.Insert
import androidx.room.Query
import com.abaz.twitterish.data.Comment
import com.abaz.twitterish.data.Post
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-07-29
 */

interface CommentDao {

    @Query("SELECT * FROM Comment c INNER JOIN Post p on p.ID = c.POST_ID ORDER BY c.DATE DESC")
    fun fetchCommentsForPost(postId: Long): List<Comment>

    @Query("INSERT INTO Comment (ID, USER_ID, POST_ID, BODY, DATE, LIKES_COUNT) VALUES (:id, :userId, :postId, :body, :date, :likesCount)")
    fun insert(id: Long, userId: Long, postId: Long, body: String, date: Date, likesCount: Int)

    @Insert
    fun insertAll(vararg comment: Comment)



}