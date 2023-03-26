package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName
import com.mspark.myimage.util.BuildVersionProviderImpl
import com.mspark.myimage.util.TimeStampUtil

data class ImageData(
    @SerializedName("thumbnail_url", alternate = ["thumbnail"])
    val thumbnailUrl: String? = null,

    @SerializedName("datetime")
    val dateTime: String = "",

    var isMyImage: Boolean = false
) {
    fun getTimeStamp(): String {
        return TimeStampUtil.getTimeStamp(dateTime, BuildVersionProviderImpl())
    }
}