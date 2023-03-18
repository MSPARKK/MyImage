package com.mspark.myimage.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mspark.myimage.api.KakaoOpenApi
import com.mspark.myimage.constants.StringConstants.*
import com.mspark.myimage.data.ImageSearchResponse
import retrofit2.Callback
import retrofit2.Response

interface MainRepository {
    suspend fun searchImage(query : String, sort : String): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, sort : String): Response<ImageSearchResponse>
}

class MainRepositoryImpl(
    private val remoteDataSource: MainRemoteDataSource,
    private val localDataSource: MainLocalDataSource
): MainRepository {

    override suspend fun searchImage(query: String, sort: String): Response<ImageSearchResponse> {
        return remoteDataSource.searchImage(query, sort)
    }

    override suspend fun searchVideo(query: String, sort: String): Response<ImageSearchResponse> {
        return remoteDataSource.searchVideo(query, sort)
    }

    companion object {
        @JvmStatic
        fun getRepository(context: Context): MainRepositoryImpl {

            val kakaoOpenApi = KakaoOpenApi.create()

            val remoteDataSource: MainRemoteDataSource = MainRemoteDataSourceImpl(kakaoOpenApi)

            val sharedPreferences = context.getSharedPreferences(Shared.SAVE_NAME, AppCompatActivity.MODE_PRIVATE)
            val localDataSource: MainLocalDataSource = MainLocalDataSourceImpl(sharedPreferences)

            return MainRepositoryImpl(remoteDataSource, localDataSource)
        }
    }
}

interface MainRemoteDataSource {
    suspend fun searchImage(query : String, sort : String): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, sort : String): Response<ImageSearchResponse>
}

class MainRemoteDataSourceImpl(
    private val kakaoOpenApi: KakaoOpenApi
): MainRemoteDataSource {
    override suspend fun searchImage(query: String, sort: String): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchImage(query = query, sort = sort, page = 1, size = 80)
//            .enqueue(object: Callback<ImageSearchResponse> {
//                override fun onResponse(
//                    call: retrofit2.Call<ImageSearchResponse>,
//                    response: retrofit2.Response<ImageSearchResponse>
//                ) {
//                    if (response.isSuccessful) {
//
//                        val imageSearchResponse = response.body()
//                        if (imageSearchResponse != null) {
//                            Log.d("@@ MainRepositoryImpl", "searchImage / onResponse, imageSearchResponse : $imageSearchResponse")
//                        }
//                    }
//                }
//
//                override fun onFailure(call: retrofit2.Call<ImageSearchResponse>, t: Throwable) {
//                    Log.d("@@ MainRepositoryImpl", "searchImage / onFailure, $t")
//                }
//            })
    }


    override suspend fun searchVideo(query: String, sort: String): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchVideo(query = query, sort = sort, page = 1, size = 15)
//            .enqueue(object: Callback<ImageSearchResponse> {
//                override fun onResponse(
//                    call: retrofit2.Call<ImageSearchResponse>,
//                    response: retrofit2.Response<ImageSearchResponse>
//                ) {
//                    if (response.isSuccessful) {
//
//                        val imageSearchResponse = response.body()
//                        if (imageSearchResponse != null) {
//                            Log.d("@@ MainRepositoryImpl", "searchVideo / onResponse, imageSearchResponse : $imageSearchResponse")
//                        }
//                    }
//                }
//
//                override fun onFailure(call: retrofit2.Call<ImageSearchResponse>, t: Throwable) {
//                    Log.d("@@ MainRepositoryImpl", "searchVideo / onFailure, $t")
//                }
//            })
    }
}

interface MainLocalDataSource {

}

class MainLocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): MainLocalDataSource {

}