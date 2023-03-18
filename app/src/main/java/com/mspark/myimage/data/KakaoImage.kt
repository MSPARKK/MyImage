package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName

data class KakaoImage(
    @SerializedName("display_sitename")
    val sitename : String,
    val collection : String,
    val image_url : String
)