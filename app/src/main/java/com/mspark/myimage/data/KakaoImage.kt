package com.mspark.myimage.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class KakaoImage(
    @SerializedName("thumbnail_url", alternate = ["thumbnail"])
    val thumbnailUrl: String? = null,

    @SerializedName("datetime")
    val dateTime: String? = null,

    var isMyImage: Boolean = false
) {

    // 잠깐! todo : 모든 버전에 맞는 시간 포맷 넣기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeStamp(): String {
        val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        val outputFormat = "yy-MM-dd HH:mm:ss"

        val formatterInput = DateTimeFormatter.ofPattern(inputFormat)
        val formatterOutput = DateTimeFormatter.ofPattern(outputFormat)

        val dateTime = LocalDateTime.parse(dateTime, formatterInput)

        return dateTime.format(formatterOutput)


//        val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
//        val outputFormat = "yy-MM-dd HH:mm:ss"
//        val inputDate = "2023-03-18T15:30:45.000+05:30"
//
//        val formatterInput = SimpleDateFormat(inputFormat, Locale.KOREA)
//        val formatterOutput = SimpleDateFormat(outputFormat, Locale.KOREA)
//
//        val date = formatterInput.parse(inputDate)
//        return formatterOutput.format(date)
    }
}