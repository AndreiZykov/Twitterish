package com.abaz.twitterish.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.abaz.twitterish.data.Comment
import com.abaz.twitterish.data.Post

/**
 * @author: Anthony Busto
 * @date:   2019-07-29
 */
interface PostDao {

    @get:Query("SELECT * FROM Post")
    val all: List<Post>

    @Query("SELECT * FROM Post where id = :id")
    fun findById(id: Long): Post

    @Query("SELECT COUNT(*) from Post")
    fun countPosts(): Int

    @Insert
    fun insertAll(vararg posts: Post)

    @Delete
    fun delete(post: Post)
}