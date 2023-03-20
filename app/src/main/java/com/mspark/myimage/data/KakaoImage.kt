package com.mspark.myimage.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
            val outputFormat = "yy.MM.dd HH:mm"

            val formatterInput = DateTimeFormatter.ofPattern(inputFormat)
            val formatterOutput = DateTimeFormatter.ofPattern(outputFormat)

            val dateTime = LocalDateTime.parse(dateTime, formatterInput)

            return dateTime.format(formatterOutput)
        }

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val desiredFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())

        return try {
            val date: Date = (isoFormat.parse(dateTime))?: return ""
            val formattedDateTime: String = desiredFormat.format(date)
            formattedDateTime

        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }
}