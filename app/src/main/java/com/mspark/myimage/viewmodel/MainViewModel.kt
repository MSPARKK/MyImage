package com.mspark.myimage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.mspark.myimage.data.ImageData
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.util.Constants.Api.PATH_IMAGE
import com.mspark.myimage.util.Constants.Api.PATH_VIDEO
import com.mspark.myimage.util.Constants.Shared.SEPARATOR
import com.mspark.myimage.util.SingleLiveEvent
import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _imageDataList: SingleLiveEvent<List<ImageData>> = SingleLiveEvent()
    val imageDataList: LiveData<List<ImageData>> = _imageDataList

    private val totalImageList = ArrayList<ImageData>()

    private val imageDataQueue: Queue<ImageData> = LinkedList()
    private val videoQueue: Queue<ImageData> = LinkedList()

    private var isLoading = false

    private var page = 1
    private var query = ""

    private val _myImageDataList: SingleLiveEvent<List<ImageData>> = SingleLiveEvent()
    val myImageDataList: LiveData<List<ImageData>> = _myImageDataList

    private val _isMyImageListEmpty: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isMyImageEmpty: LiveData<Boolean> = _isMyImageListEmpty

    private val _isSearchEmpty: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isSearchEmpty: LiveData<Boolean> = _isSearchEmpty

    init {
        _isSearchEmpty.postValue(totalImageList.isEmpty())
    }

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

    private fun processDataFromApi(dataFromImageDataApi: MutableList<ImageData>, dataFromVideoApi: MutableList<ImageData>) {
        val temporaryImageList = sortImageListByNewest(dataFromImageDataApi, dataFromVideoApi)

        Log.d("@@ MainViewModel", "sort Test| after / imageQueue size: ${imageDataQueue.size}, videoQueue size: ${videoQueue.size}")
        Log.d("@@ MainViewModel", "sort Test| after / temporaryImageList / ${temporaryImageList.size} / $temporaryImageList")

        val resultList = updateImageListWithMyImages(temporaryImageList)


        totalImageList.addAll(resultList)
        _imageDataList.postValue(totalImageList)
        _isSearchEmpty.postValue(totalImageList.isEmpty())

        isLoading = false
    }

    private fun sortImageListByNewest(dataFromImageDataApi: MutableList<ImageData>, dataFromVideoApi: MutableList<ImageData>): ArrayList<ImageData> {
        val temporaryImageList = ArrayList<ImageData>()

        if (dataFromImageDataApi.isNotEmpty()) {
            imageDataQueue.addAll(dataFromImageDataApi)
        } else {
            if (imageDataQueue.isEmpty()) {
                temporaryImageList.addAll(videoQueue)
                videoQueue.clear()
            }
        }

        if (dataFromVideoApi.isNotEmpty()) {
            videoQueue.addAll(dataFromVideoApi)
        } else {
            if (videoQueue.isEmpty()) {
                temporaryImageList.addAll(imageDataQueue)
                imageDataQueue.clear()
            }
        }

        Log.d("@@ MainViewModel", "sort Test| before / imageQueue size: ${imageDataQueue.size}, videoQueue size: ${videoQueue.size}")

        while (imageDataQueue.isNotEmpty() && videoQueue.isNotEmpty()) {
            val image = imageDataQueue.peek()
            val video = videoQueue.peek()

            if (image != null && video != null) {
                if (image.dateTime > video.dateTime) {
                    imageDataQueue.poll()?.let {
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

    private fun updateImageListWithMyImages(imageDataList: ArrayList<ImageData>): ArrayList<ImageData> {
        val myImageListString = repository.getMyImageListString()
        val updatedImageList = ArrayList<ImageData>(imageDataList.size)

        imageDataList.forEach { image ->
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

        totalImageList.clear()

        imageDataQueue.clear()
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
        _imageDataList.postValue(totalImageList)
    }


    fun getMyImage() {
        val myImageListString = repository.getMyImageListString()

        Log.d("@@ MainViewModel", "getMyImage | myImageListString : $myImageListString")

        val resultArray = myImageListString.split(SEPARATOR)

        Log.d("@@ MainViewModel", "getMyImage | resultArray.size : ${resultArray.size}")

        if (resultArray.size > 1) {
            val myImageList = ArrayList<ImageData>()

            resultArray.forEach {
                if (it.isEmpty()) return@forEach

                val imageData = ImageData(thumbnailUrl = it, isMyImage = true)
                myImageList.add(imageData)
            }
            _myImageDataList.postValue(myImageList)
            _isMyImageListEmpty.postValue(false)
        } else {
            _myImageDataList.postValue(emptyList())
            _isMyImageListEmpty.postValue(true)
        }
    }

    fun onClickLikeOnMyImage(position: Int) {
        Log.d("@@ MainViewModel", "onClickLikeMyImage | position : $position")

        val myList: ArrayList<ImageData> = (_myImageDataList.value?: return) as ArrayList<ImageData>

        Log.d("@@ MainViewModel", "onClickLikeMyImage | myList.size : ${myList.size}")

        if (myList.size <= position) return

        myList[position].thumbnailUrl?.let {
            Log.d("@@ MainViewModel", "onClickLikeMyImage | myList[position].thumbnailUrl : $it")

            totalImageList.indexOfFirst { imageData ->
                imageData.thumbnailUrl == it
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

    @TestOnly
    fun setTestDataForGetMoreImage(
        isLoading: Boolean,
        query: String,
        page: Int,
        imageDataQueueData: List<ImageData>,
        videoQueueData: List<ImageData>
    ) {
        this.isLoading = isLoading
        this.query = query
        this.page = page

        imageDataQueue.addAll(imageDataQueueData)
        videoQueue.addAll(videoQueueData)
    }

}