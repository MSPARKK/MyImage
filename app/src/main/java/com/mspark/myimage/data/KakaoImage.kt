package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName
import com.mspark.myimage.util.BuildVersionProviderImpl
import com.mspark.myimage.util.TimeStampUtil

data class KakaoImage(
    @SerializedName("thumbnail_url", alternate = ["thumbnail"])
    val thumbnailUrl: String? = null,

    @SerializedName("datetime")
    val dateTime: String = "",

    var isMyImage: Boolean = false,

    // @@ 잠깐! todo : test 로직 삭제
    @SerializedName("url")
    val url: String? = null,
) {
    fun getTimeStamp(): String {
        return TimeStampUtil.getTimeStamp(dateTime, BuildVersionProviderImpl())
    }
}