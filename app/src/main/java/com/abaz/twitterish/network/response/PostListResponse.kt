package com.abaz.twitterish.network.response


import com.abaz.twitterish.db.model.Post
import com.google.gson.annotations.SerializedName

data class PostListResponse(
    @SerializedName("response_list")
    val responseList: List<Post>
)


data class ResponseObject<T>(
    @SerializedName("response_object")
    val responseObject: T?,
    @SerializedName("errorCode")
    val errorCode: Int
)