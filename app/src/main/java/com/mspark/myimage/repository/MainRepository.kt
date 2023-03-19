package com.mspark.myimage.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.mspark.myimage.api.KakaoOpenApi
import com.mspark.myimage.util.Constants.*
import com.mspark.myimage.data.ImageSearchResponse
import com.mspark.myimage.util.Constants.KakaoApi.IMAGE_SIZE
import com.mspark.myimage.util.Constants.KakaoApi.VIDEO_SIZE
import retrofit2.Response

interface MainRepository {
    suspend fun searchImage(query : String, sort : String, page: Int): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, sort : String, page: Int): Response<ImageSearchResponse>

//    suspend fun getImages(query : String, sort : String): Response<ImageSearchResponse>
}

class MainRepositoryImpl(
    private val remoteDataSource: MainRemoteDataSource,
    private val localDataSource: MainLocalDataSource
): MainRepository {

    override suspend fun searchImage(query: String, sort: String, page: Int): Response<ImageSearchResponse> {
        return remoteDataSource.searchImage(query, sort, page)
    }

    override suspend fun searchVideo(query: String, sort: String, page: Int): Response<ImageSearchResponse> {
        return remoteDataSource.searchVideo(query, sort, page)
    }

//    override suspend fun getImages(query: String, sort: String): Response<ImageSearchResponse> {
//        return remoteDataSource.getImages(query, sort)
//    }

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
    suspend fun searchImage(query : String, sort : String, page: Int): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, sort : String, page: Int): Response<ImageSearchResponse>
//    suspend fun getImages(query : String, sort : String): Response<ImageSearchResponse>
}

class MainRemoteDataSourceImpl(
    private val kakaoOpenApi: KakaoOpenApi
): MainRemoteDataSource {


    override suspend fun searchImage(query: String, sort: String, page: Int): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchImage(query = query, sort = sort, page = page, size = IMAGE_SIZE)
    }


    override suspend fun searchVideo(query: String, sort: String, page: Int): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchVideo(query = query, sort = sort, page = page, size = VIDEO_SIZE)
    }

//    override suspend fun getImages(query: String, sort: String): Response<ImageSearchResponse> {
//
//        val result1 = async { apiService.getFirstApi() }
//        val result2 = async { apiService.getSecondApi() }
//    }
}

interface MainLocalDataSource {

}

class MainLocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): MainLocalDataSource {

}