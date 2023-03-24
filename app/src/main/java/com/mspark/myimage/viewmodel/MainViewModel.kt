package com.mspark.myimage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.util.Constants.KakaoApi.PATH_IMAGE
import com.mspark.myimage.util.Constants.KakaoApi.PATH_VIDEO
import com.mspark.myimage.util.Constants.Shared.SEPARATOR
import com.mspark.myimage.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val imageQueue: Queue<KakaoImage> = LinkedList()
    private val videoQueue: Queue<KakaoImage> = LinkedList()

    private var isLoading = false

    private var page = 1

    private var query = ""

    private val _myImageList: SingleLiveEvent<List<KakaoImage>> = SingleLiveEvent()
    val myImageList: LiveData<List<KakaoImage>> = _myImageList
    private val _isMyImageListEmpty: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isMyImageEmpty: LiveData<Boolean> = _isMyImageListEmpty


    private fun searchImage() {
        viewModelScope.launch(exceptionHandler) {
            val deferredDataFromImageApi = async {
                repository.searchImage(path = PATH_IMAGE, query = query, page = page)
            }
            val deferredDataFromVideoApi = async {
                repository.searchImage(path = PATH_VIDEO, query = query, page = page)
            }

            val (dataFromImageApi, dataFromVideoApi) = awaitAll(deferredDataFromImageApi, deferredDataFromVideoApi)

            processDataFromApi(dataFromImageApi, dataFromVideoApi)
        }
    }

    private fun processDataFromApi(dataFromImageApi: MutableList<KakaoImage>, dataFromVideoApi: MutableList<KakaoImage>) {
        val temporaryImageList = sortImageListByNewest(dataFromImageApi, dataFromVideoApi)

        Log.d("@@ MainViewModel", "sort Test| after / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")
        Log.d("@@ MainViewModel", "sort Test| after / temporaryImageList / ${temporaryImageList.size} / $temporaryImageList")

        val resultList = updateImageListWithMyImages(temporaryImageList)


        totalImageList.addAll(resultList)
        _imageList.postValue(totalImageList)

        isLoading = false
    }

    private fun sortImageListByNewest(dataFromImageApi: MutableList<KakaoImage>, dataFromVideoApi: MutableList<KakaoImage>): ArrayList<KakaoImage> {
        val temporaryImageList = ArrayList<KakaoImage>()

        if (dataFromImageApi.isNotEmpty()) {
            imageQueue.addAll(dataFromImageApi)
        } else {
            if (imageQueue.isEmpty()) {
                temporaryImageList.addAll(videoQueue)
                videoQueue.clear()
            }
        }

        if (dataFromVideoApi.isNotEmpty()) {
            videoQueue.addAll(dataFromVideoApi)
        } else {
            if (videoQueue.isEmpty()) {
                temporaryImageList.addAll(imageQueue)
                imageQueue.clear()
            }
        }


        Log.d("@@ MainViewModel", "sort Test| before / imageQueue size: ${imageQueue.size}, videoQueue size: ${videoQueue.size}")

        while (imageQueue.isNotEmpty() && videoQueue.isNotEmpty()) {
            val image = imageQueue.peek()
            val video = videoQueue.peek()

            if (image != null && video != null) {
                if (image.dateTime > video.dateTime) {
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

        return temporaryImageList
    }

    private fun updateImageListWithMyImages(imageList: ArrayList<KakaoImage>): ArrayList<KakaoImage> {
        val myImageListString = repository.getMyImageListString()
        val updatedImageList = ArrayList<KakaoImage>(imageList.size)

        imageList.forEach { image ->
            if (image.thumbnailUrl != null && myImageListString.contains(image.thumbnailUrl)) {
                updatedImageList.add(image.copy(isMyImage = true))
            } else {
                updatedImageList.add(image)
            }
        }

        return updatedImageList
    }

    fun searchNewQuery(query: String) {
        this.query = query

        isLoading = true

        _imageList.postValue(emptyList())

        totalImageList.clear()

        imageQueue.clear()
        videoQueue.clear()

        page = 1

        searchImage()
    }

    fun getMoreImage() {
        if (query.isEmpty()) return

        if (isLoading) return
        isLoading = true

        page++

        searchImage()
    }

    fun onClickLikeOnSearch(position: Int) {
        Log.d("@@ MainViewModel", "onClickLike | position : $position")

        val image = totalImageList.removeAt(position)
        val newImage = image.copy(isMyImage = !image.isMyImage)

        newImage.thumbnailUrl?.let {
            repository.updateMyImage(it)
            getMyImage()
        }

        totalImageList.add(position, newImage)
        _imageList.postValue(totalImageList)
    }


    fun getMyImage() {
        val myImageListString = repository.getMyImageListString()

        Log.d("@@ MainViewModel", "getMyImage | myImageListString : $myImageListString")

        val resultArray = myImageListString.split(SEPARATOR)

        Log.d("@@ MainViewModel", "getMyImage | resultArray.size : ${resultArray.size}")

        if (resultArray.size > 1) {
            val myImageList = ArrayList<KakaoImage>()

            resultArray.forEach {
                if (it.isEmpty()) return@forEach

                val kakaoImage = KakaoImage(thumbnailUrl = it, isMyImage = true)
                myImageList.add(kakaoImage)
            }
            _myImageList.postValue(myImageList)
            _isMyImageListEmpty.postValue(false)
        } else {
            _myImageList.postValue(emptyList())
            _isMyImageListEmpty.postValue(true)
        }
    }

    fun onClickLikeOnMyImage(position: Int) {
        Log.d("@@ MainViewModel", "onClickLikeMyImage | position : $position")

        val myList: ArrayList<KakaoImage> = (_myImageList.value?: return) as ArrayList<KakaoImage>

        Log.d("@@ MainViewModel", "onClickLikeMyImage | myList.size : ${myList.size}")

        if (myList.size <= position) return

        myList[position].thumbnailUrl?.let {
            Log.d("@@ MainViewModel", "onClickLikeMyImage | myList[position].thumbnailUrl : $it")

            totalImageList.indexOfFirst { kakaoImage ->
                kakaoImage.thumbnailUrl == it
            }.let { position ->
                if (position != NO_POSITION) {
                    onClickLikeOnSearch(position)
                } else {
                    repository.updateMyImage(it)
                    getMyImage()
                }
            }
        }
    }

    private fun onError(message: String) {
        Log.e("@@ MainViewModel", "onError | message : $message")
        isLoading = true
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.stackTrace

        onError("Exception handled: ${throwable.localizedMessage}")
    }

}