package com.mspark.myimage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.util.SingleLiveEvent

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {


}