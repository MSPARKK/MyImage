package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName

data class MetaData(
    @SerializedName("total_count")
    val totalCount: Int?,

    @SerializedName("is_end")
    val isEnd: Boolean?
)