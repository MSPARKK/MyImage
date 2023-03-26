package com.mspark.myimage.api

import android.util.Log
import com.mspark.myimage.BuildConfig
import com.mspark.myimage.data.ImageSearchResponse
import com.mspark.myimage.util.Constants.Api.BASE_URL
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface KakaoOpenApi {
    @GET("v2/search/{path}")
    suspend fun searchImage(
        @Path("path") path: String,
        @Header("Authorization") apiKey: String = BuildConfig.REST_API_KEY,
        @Query("query") query : String,
        @Query("sort") sort : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ImageSearchResponse>

    companion object {
        fun create(): KakaoOpenApi {
            val httpBuilder = OkHttpClient.Builder()

            val certificatePinner: CertificatePinner = CertificatePinner.Builder().build()

            val loggingInterceptor = HttpLoggingInterceptor { s ->
                Log.d("network", s)
            }
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            httpBuilder.addInterceptor(loggingInterceptor)

            val okHttpClient: OkHttpClient = httpBuilder
                .certificatePinner(certificatePinner)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(KakaoOpenApi::class.java)
        }
    }
}