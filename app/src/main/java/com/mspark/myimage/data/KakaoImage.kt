package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName

data class KakaoImage(
    @SerializedName("thumbnail_url", alternate = ["thumbnail"])
    val thumbnailUrl: String? = null,

    @SerializedName("datetime")
    val dateTime: String? = null,

    var isMyImage: Boolean = false
)