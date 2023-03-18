package com.mspark.myimage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.util.SingleLiveEvent
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _imageList: SingleLiveEvent<List<KakaoImage>> = SingleLiveEvent()
    val imageList: LiveData<List<KakaoImage>> = _imageList


    fun searchImage() {
        viewModelScope.launch {
            val response = repository.searchImage("강아지","accuracy")
            if (response.isSuccessful) {
                Log.d("@@ MainViewModel", "searchImage: ${response.body()?.documents}")
                response.body()?.documents?.let {
                    _imageList.postValue(it)
                }
            } else {
                Log.d("@@ MainViewModel", "searchImage: ${response.errorBody()}")
            }
        }
    }

    fun searchVideo() {
        viewModelScope.launch {
            val response = repository.searchVideo("강아지","accuracy")
            if (response.isSuccessful) {
                Log.d("@@ MainViewModel", "searchImage: ${response.body()?.documents}")
                response.body()?.documents?.let {
                    _imageList.postValue(it)
                }
            } else {
                Log.d("@@ MainViewModel", "searchImage: ${response.errorBody()}")
            }
        }
    }
}