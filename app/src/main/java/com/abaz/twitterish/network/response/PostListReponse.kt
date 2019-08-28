package com.abaz.twitterish.network.response


import com.google.gson.annotations.SerializedName

data class PostListReponse(
    @SerializedName("response_list")
    val responseList: List<Response>
) {
    data class Response(
        @SerializedName("body")
        val body: String,
        @SerializedName("date")
        val date: Long = 0,
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("likesRating")
        val likesRating: Int = 0,
        @SerializedName("replyCount")
        val replyCount: Int = 0,
        @SerializedName("repostCount")
        val repostCount: Int = 0,
        @SerializedName("userId")
        val userId: Int = 0
    )

    companion object {
        fun fakeData(): List<Response>{

            val list = mutableListOf<Response>()
            for (i in 1..20) {
                list.add(Response(body = "HELLO WORLD"))
            }
            return list
        }

    }
}