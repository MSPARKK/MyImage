package com.mspark.myimage.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.mspark.myimage.constants.StringConstants.*

interface MainRepository {

}

class MainRepositoryImpl(
    private val remoteDataSource: MainRemoteDataSource,
    private val localDataSource: MainLocalDataSource
): MainRepository {


    companion object {
        @JvmStatic
        fun getRepository(context: Context): MainRepositoryImpl {

            val remoteDataSource: MainRemoteDataSource = MainRemoteDataSourceImpl()

            val sharedPreferences = context.getSharedPreferences(Shared.SAVE_NAME, AppCompatActivity.MODE_PRIVATE)
            val localDataSource: MainLocalDataSource = MainLocalDataSourceImpl(sharedPreferences)

            return MainRepositoryImpl(remoteDataSource, localDataSource)
        }
    }
}

interface MainRemoteDataSource {

}

class MainRemoteDataSourceImpl: MainRemoteDataSource {

}

interface MainLocalDataSource {

}

class MainLocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): MainLocalDataSource {

}