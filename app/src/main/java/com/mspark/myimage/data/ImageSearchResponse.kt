package com.mspark.myimage.data

import com.google.gson.annotations.SerializedName

class ImageSearchResponse {
    data class ImageSearchResponse(
        @SerializedName("meta")
        val metaData: MetaData?,

        @SerializedName("documents")
        var documents: MutableList<KakaoImage>?
    )
}