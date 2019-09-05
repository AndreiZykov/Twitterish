package com.abaz.twitterish.utils.extensions

import com.abaz.twitterish.db.model.Post

fun Post.likeAndCopy() : Post {
    val authorizedUserExtras = authorizedUserExtras
    val currentLikeValue = (authorizedUserExtras?.rating ?: 0)
    val isLikingFromNeutral = currentLikeValue == 0
    val isLikingFromDislike = currentLikeValue < 0
    val isLiking = isLikingFromNeutral || isLikingFromDislike
    val currentLikesRating = likesRating
    val newLikeValue = if (isLiking) 1 else 0
    val newLikesRating = when {
        isLikingFromNeutral -> currentLikesRating + 1
        isLikingFromDislike -> currentLikesRating + 2
        else -> currentLikesRating - 1
    }
    val newExtras = authorizedUserExtras?.copy(rating = newLikeValue)
    return copy(authorizedUserExtras = newExtras, likesRating = newLikesRating)
}

fun Post.dislikeAndCopy(): Post {
    val authorizedUserExtras = authorizedUserExtras
    val currentLikeValue = (authorizedUserExtras?.rating ?: 0)
    val isDislikingFromNeutral = currentLikeValue == 0
    val isDislikingFromLike = currentLikeValue > 0
    val isDisliking = isDislikingFromNeutral || isDislikingFromLike
    val currentLikesRating = likesRating
    val newLikeValue = if (isDisliking) -1 else 0

    val newLikesRating = when {
        isDislikingFromNeutral -> currentLikesRating - 1
        isDislikingFromLike -> currentLikesRating - 2
        else -> currentLikesRating + 1
    }

    val newExtras = authorizedUserExtras?.copy(rating = newLikeValue)
    return copy(authorizedUserExtras = newExtras, likesRating = newLikesRating)
}

fun Post.incrementReplyCountAndCopy(): Post {
    return copy(replyCount = replyCount + 1)
}