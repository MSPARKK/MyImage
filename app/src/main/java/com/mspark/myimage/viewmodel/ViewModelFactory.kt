package com.mspark.myimage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.repository.MainRepositoryImpl

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository: MainRepository = MainRepositoryImpl.getRepository(context)

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}