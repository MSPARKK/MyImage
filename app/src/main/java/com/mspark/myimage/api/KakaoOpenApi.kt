package com.mspark.myimage.api

import android.util.Log
import com.mspark.myimage.data.ImageSearchResponse
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoOpenApi {

    @GET("v2/search/image")
    suspend fun searchImage(
        @Header("Authorization") apiKey: String = AUTH_HEADER,
        @Query("query") query : String,
        @Query("sort") sort : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ImageSearchResponse>


    @GET("v2/search/vclip")
    suspend fun searchVideo(
        @Header("Authorization") apiKey: String = AUTH_HEADER,
        @Query("query") query : String,
        @Query("sort") sort : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ImageSearchResponse>

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val AUTH_HEADER = "KakaoAK 7c2f21969879468dc06033d4812f3fe7" // 잠깐! todo : 키 숨기기

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