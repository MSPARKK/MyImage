package com.mspark.myimage.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

object TimeStampUtil {
    fun getTimeStamp(dateTime: String, buildVersionProvider: BuildVersionProvider): String {
        if (buildVersionProvider.isOreoAndAbove()) {
            return getTimeStampOreoAndAbove(dateTime)
        }

        return getTimeStampUnderOreo(dateTime)
    }

    private fun getTimeStampOreoAndAbove(dateTime: String): String {
        return try {
            val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
            val outputFormat = "yy.MM.dd HH:mm"

            val formatterInput = DateTimeFormatter.ofPattern(inputFormat)
            val formatterOutput = DateTimeFormatter.ofPattern(outputFormat)

            val localDateTime = LocalDateTime.parse(dateTime, formatterInput)

            return localDateTime.format(formatterOutput)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            ""
        }
    }

    private fun getTimeStampUnderOreo(dateTime: String): String {
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