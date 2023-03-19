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

    private var query = ""


    fun searchImage() {
        viewModelScope.launch {
            val deferredResponseImage = async {
                repository.searchImage(query = query, page = imagePage)
            }
            val deferredResponseVideo = async {
                repository.searchVideo(query = query, page = imagePage)
            }


            // @@ 새로운 로직 테스트 - 앞으로 불러올 이미지도 고려해서 정렬
            val (responseImage, responseVideo) = awaitAll(deferredResponseImage, deferredResponseVideo)

            Log.d("@@ MainViewModel", "sort Test2| after / responseImage.isEnd : ${ responseImage.body()?.metaData?.isEnd} / responseVideo.isEnd : ${responseVideo.body()?.metaData?.isEnd}")
            Log.d("@@ MainViewModel", "sort Test2| after / responseImage.isSuccessful : ${responseImage.isSuccessful} / responseVideo.isSuccessful : ${responseVideo.isSuccessful}")



            if (responseImage.isSuccessful) {
                val imageList = responseImage.body()?.documents
                if (imageList != null) {
                    imageQueue.addAll(imageList)
                }
            } else {
                if (imageQueue.isEmpty()) {
                    temporaryImageList.addAll(videoQueue)
                    videoQueue.clear()
                }
            }

            if (responseVideo.isSuccessful) {
                val imageList = responseVideo.body()?.documents
                if (imageList != null) {
                    videoQueue.addAll(imageList)
                }
            } else {
                if (videoQueue.isEmpty()) {
                    temporaryImageList.addAll(imageQueue)
                    imageQueue.clear()
                }
            }

            Log.d("@@ MainViewModel", "sort Test| before / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")

            @RequiresApi(Build.VERSION_CODES.O)
            while (imageQueue.isNotEmpty() && videoQueue.isNotEmpty()) {
                val image = imageQueue.peek()
                val video = videoQueue.peek()

                if (image != null && video != null) {
                    if (image.getTimeStamp() > video.getTimeStamp()) {
                        imageQueue.poll()?.let {
                            temporaryImageList.add(it)
                        }
                    } else {
                        videoQueue.poll()?.let {
                            temporaryImageList.add(it)
                        }
                    }
                }
            }

            Log.d("@@ MainViewModel", "sort Test| after / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")
            Log.d("@@ MainViewModel", "sort Test| after / temporaryImageList / ${temporaryImageList.size} / $temporaryImageList")

            totalImageList.addAll(temporaryImageList)
            temporaryImageList.clear()
            _imageList.postValue(totalImageList)

            isLoading = false
        }
    }

//    fun searchFaker() {
//        searchNewQuery("페이커")
//    }
//
//    fun searchPuppy() {
//        searchNewQuery("강아지")
//    }

    fun searchNewQuery(query: String) {
        this.query = query

        isLoading = true

        _imageList.postValue(emptyList())

        totalImageList.clear()
        temporaryImageList.clear()

        imageQueue.clear()
        videoQueue.clear()

        imagePage = 1
        videoPage = 1

        searchImage()
    }

    fun getMoreImage() {
        if (query.isEmpty()) return

        if (isLoading) return
        isLoading = true

        imagePage++
        videoPage++

        searchImage()
    }

    fun onClickLike(position: Int) {
        Log.d("@@ MainViewModel", "onClickLike | position : $position")

        val image = totalImageList.removeAt(position)
        val newImage = image.copy(isMyImage = !image.isMyImage)

        newImage.thumbnailUrl?.let { repository.updateMyImage(it) }

        totalImageList.add(position, newImage)
        _imageList.postValue(totalImageList)
    }

}