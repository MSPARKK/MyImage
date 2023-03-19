package com.mspark.myimage.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mspark.myimage.api.KakaoOpenApi
import com.mspark.myimage.data.ImageSearchResponse
import com.mspark.myimage.util.Constants.KakaoApi.IMAGE_SIZE
import com.mspark.myimage.util.Constants.KakaoApi.SORT_RECENCY
import com.mspark.myimage.util.Constants.KakaoApi.VIDEO_SIZE
import com.mspark.myimage.util.Constants.Shared.KEY_MY_IMAGE_LIST
import com.mspark.myimage.util.Constants.Shared.SAVE_NAME
import com.mspark.myimage.util.Constants.Shared.SEPARATOR
import retrofit2.Response

interface MainRepository {
    suspend fun searchImage(query : String, page: Int): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, page: Int): Response<ImageSearchResponse>

    fun updateMyImage(imageUrl: String)
    fun getMyImageListString(): String
}

class MainRepositoryImpl(
    private val remoteDataSource: MainRemoteDataSource,
    private val localDataSource: MainLocalDataSource
): MainRepository {

    override suspend fun searchImage(query: String, page: Int): Response<ImageSearchResponse> {
        return remoteDataSource.searchImage(query, page)
    }

    override suspend fun searchVideo(query: String, page: Int): Response<ImageSearchResponse> {
        return remoteDataSource.searchVideo(query, page)
    }

    override fun updateMyImage(imageUrl: String) {
        localDataSource.updateMyImage(imageUrl)
    }

    override fun getMyImageListString(): String {
        return localDataSource.getMyImageListString()
    }

    companion object {
        @JvmStatic
        fun getRepository(context: Context): MainRepositoryImpl {

            val kakaoOpenApi = KakaoOpenApi.create()

            val remoteDataSource: MainRemoteDataSource = MainRemoteDataSourceImpl(kakaoOpenApi)

            val sharedPreferences = context.getSharedPreferences(SAVE_NAME, AppCompatActivity.MODE_PRIVATE)
            val localDataSource: MainLocalDataSource = MainLocalDataSourceImpl(sharedPreferences)

            return MainRepositoryImpl(remoteDataSource, localDataSource)
        }
    }
}

interface MainRemoteDataSource {
    suspend fun searchImage(query : String, page: Int): Response<ImageSearchResponse>
    suspend fun searchVideo(query : String, page: Int): Response<ImageSearchResponse>
}

class MainRemoteDataSourceImpl(
    private val kakaoOpenApi: KakaoOpenApi
): MainRemoteDataSource {

    override suspend fun searchImage(query: String, page: Int): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchImage(query = query, sort = SORT_RECENCY, page = page, size = IMAGE_SIZE)
    }

    override suspend fun searchVideo(query: String, page: Int): Response<ImageSearchResponse> {
        return kakaoOpenApi.searchVideo(query = query, sort = SORT_RECENCY, page = page, size = VIDEO_SIZE)
    }
}

interface MainLocalDataSource {
    fun updateMyImage(imageUrl: String)
    fun getMyImageListString(): String
}

class MainLocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): MainLocalDataSource {
    private fun saveStringShared(key: String, value: String): Boolean = sharedPreferences.edit().putString(key, value).commit()
    private fun getStringShared(key: String, default: String = "") = sharedPreferences.getString(key, default) ?: default


    override fun updateMyImage(imageUrl: String) {

        val myImageList = getStringShared(KEY_MY_IMAGE_LIST)
        myImageList.contains(imageUrl).let {
            if (it) {
                // 이미 저장 되어 있는 경우 -> 삭제
                val newMyImageList = myImageList.replace("$SEPARATOR$imageUrl", "")
                saveStringShared(KEY_MY_IMAGE_LIST, newMyImageList)
            } else {
                // 저장 되어 있지 않은 경우 -> 저장
                saveStringShared(KEY_MY_IMAGE_LIST, "$myImageList$SEPARATOR$imageUrl")
            }
        }

        // @@ test
        val testMyImageList = getStringShared(KEY_MY_IMAGE_LIST)
        Log.d("@@ MainLocalDataSourceImpl", "testMyImageList : $testMyImageList")
    }

    override fun getMyImageListString(): String {
        return getStringShared(KEY_MY_IMAGE_LIST)
    }
}