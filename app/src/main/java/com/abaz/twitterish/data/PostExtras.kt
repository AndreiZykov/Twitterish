package com.abaz.twitterish.data

/**
 * @author: Anthony Busto
 * @date:   2019-08-29
 */
data class PostExtras(
    val id: Long = 0,
    val userId: Long,
    val postId: Long,
    val like: Int = 0,
    val dislike: Int = 0,
    val repost: Int = 0
)