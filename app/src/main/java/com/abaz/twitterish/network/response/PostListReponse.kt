package com.abaz.twitterish.network.response


import com.abaz.twitterish.data.Post
import com.google.gson.annotations.SerializedName

data class PostListReponse(
    @SerializedName("response_list")
    val responseList: List<Post>
)


data class ResponseObject<T>(
    @SerializedName("response_object")
    val responseObject: T
)