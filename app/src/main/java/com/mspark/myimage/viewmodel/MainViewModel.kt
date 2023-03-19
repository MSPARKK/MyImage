package com.mspark.myimage.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.util.Constants.KakaoApi.SORT_RECENCY
import com.mspark.myimage.util.Constants.KakaoApi.TEST_QUERY
import com.mspark.myimage.util.SingleLiveEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _imageList: SingleLiveEvent<List<KakaoImage>> = SingleLiveEvent()
    val imageList: LiveData<List<KakaoImage>> = _imageList
    private val totalImageList = ArrayList<KakaoImage>()

    private val temporaryImageList = ArrayList<KakaoImage>()
    private val imageQueue: Queue<KakaoImage> = LinkedList()
    private val videoQueue: Queue<KakaoImage> = LinkedList()

    private var isLoading = false

    private var imagePage = 1 // 1 ~ 50
    private var videoPage = 1 // 1 ~ 15


    fun searchImage() {
        viewModelScope.launch {
            val result1 = async {
                repository.searchImage(query = TEST_QUERY, sort = SORT_RECENCY, page = imagePage)
            }
            val result2 = async {
                repository.searchVideo(query = TEST_QUERY, sort = SORT_RECENCY, page = imagePage)
            }



            // @@ 새로운 로직 테스트 - 앞으로 불러올 이미지도 고려해서 정렬
            val (res1, res2) = awaitAll(result1, result2)

            if (res1.isSuccessful) {
                val imageList = res1.body()?.documents
                if (imageList != null) {
                    imageQueue.addAll(imageList)
                }
            }

            if (res2.isSuccessful) {
                val imageList = res2.body()?.documents
                if (imageList != null) {
                    videoQueue.addAll(imageList)
                }
            }

            Log.d("@@ MainViewModel", "sort Test| before / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")

            @RequiresApi(Build.VERSION_CODES.O)
            while (imageQueue.isNotEmpty() && videoQueue.isNotEmpty()) {
                val image = imageQueue.peek()
                val video = videoQueue.peek()

                if (image != null && video != null) {
                    if (image.getTimeStamp() > video.getTimeStamp()) {
                        imageQueue.poll()?.let { temporaryImageList.add(it) }
                    } else {
                        videoQueue.poll()?.let { temporaryImageList.add(it) }
                    }
                }
            }

            Log.d("@@ MainViewModel", "sort Test| after / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")
            Log.d("@@ MainViewModel", "sort Test| after / temporaryImageList $temporaryImageList")



            totalImageList.addAll(temporaryImageList)
            _imageList.postValue(totalImageList)


            // @@  기존 로직 - 불러온 데이터만 정렬

//            val combinedResult = awaitAll(result1, result2)
//            val newList = ArrayList<KakaoImage>()
//            combinedResult.forEach { response ->
//                if (response.isSuccessful) {
//                    Log.d("@@ MainViewModel", "searchImage: ${response.body()?.documents}")
//                    response.body()?.documents?.let {
//                        newList.addAll(it)
//                    }
//                }
//            }
//
//            // 최신 순으로 정렬
//            newList.sortBy {
//                it.dateTime
//            }
//            newList.reverse()
//
//
//            totalImageList.addAll(newList)
//            _imageList.postValue(totalImageList)

            isLoading = false
        }
    }

    fun searchVideo() {
//        viewModelScope.launch {
//            val response = repository.searchVideo(TEST_QUERY,"accuracy", videoPage)
//            if (response.isSuccessful) {
//                Log.d("@@ MainViewModel", "searchImage: ${response.body()?.documents}")
//                response.body()?.documents?.let {
////                    _imageList.postValue(it)
//                    totalImageList.addAll(it)
//                    _imageList.postValue(totalImageList)
//                }
//            } else {
//                Log.d("@@ MainViewModel", "searchImage: ${response.errorBody()}")
//            }
//        }
    }

    fun getMoreImage() {
        if (isLoading) return
        isLoading = true

        imagePage++
        videoPage++


        searchImage()

    }

}