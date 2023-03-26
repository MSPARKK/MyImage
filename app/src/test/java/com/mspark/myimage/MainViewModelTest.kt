package com.mspark.myimage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mspark.myimage.data.ImageData
import com.mspark.myimage.repository.MainLocalDataSource
import com.mspark.myimage.repository.MainRemoteDataSource
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.repository.MainRepositoryImpl
import com.mspark.myimage.util.Constants
import com.mspark.myimage.util.getOrAwaitValue
import com.mspark.myimage.viewmodel.MainViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    val mainThreadSurrogate = newSingleThreadContext(MainViewModelTest::class.java.simpleName)

    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: MainRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(mainThreadSurrogate)

        val mockRemoteDataSource = mock(MainRemoteDataSource::class.java)
        val mockLocalDataSource = mock(MainLocalDataSource::class.java)

        mainRepository = MainRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)

        mainViewModel = MainViewModel(mainRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun searchNewQueryTest_when_dataFromVideoApi_has_oldestData() {
        runBlocking {
            // Given
            val dataFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00"),
            )

            val dataFromVideoApi = mutableListOf(
                ImageData("video-A","2023-03-20T21:21:00.000+09:00"),
                ImageData("video-B","2023-03-20T21:19:00.000+09:00"),
            )


            val resultFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("video-A","2023-03-20T21:21:00.000+09:00", true),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00", true),
            )

            val query = "test"
            val page = 1

            val myImageListString = "|image-B|video-A"

            `when`(mainRepository.searchImage(Constants.Api.PATH_IMAGE, query, page))
                .thenReturn(dataFromImageApi)

            `when`(mainRepository.searchImage(Constants.Api.PATH_VIDEO, query, page))
                .thenReturn(dataFromVideoApi)

            `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

            // When
            mainViewModel.searchNewQuery(query)

            // Then
            assertEquals(resultFromImageApi, mainViewModel.imageDataList.getOrAwaitValue())

        }
    }



    @Test
    fun searchNewQueryTest_when_dataFromImageApi_has_oldestData() {
        runBlocking {
            // Given
            val dataFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00"),
                ImageData("image-C","2023-03-20T21:16:00.000+09:00"),
            )

            val dataFromVideoApi = mutableListOf(
                ImageData("video-A","2023-03-20T21:21:00.000+09:00"),
                ImageData("video-B","2023-03-20T21:19:00.000+09:00"),
            )


            val resultFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("video-A","2023-03-20T21:21:00.000+09:00", true),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00", true),
                ImageData("video-B","2023-03-20T21:19:00.000+09:00"),
            )

            val query = "test"
            val page = 1

            val myImageListString = "|image-B|video-A"

            `when`(mainRepository.searchImage(Constants.Api.PATH_IMAGE, query, page))
                .thenReturn(dataFromImageApi)

            `when`(mainRepository.searchImage(Constants.Api.PATH_VIDEO, query, page))
                .thenReturn(dataFromVideoApi)

            `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

            // When
            mainViewModel.searchNewQuery(query)

            // Then
            assertEquals(resultFromImageApi, mainViewModel.imageDataList.getOrAwaitValue())

        }
    }


    @Test
    fun getMoreImageTest_when_videoQueue_has_former_data_which_older_than_image_A() {
        runBlocking {
            // Given
            val dataFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00"),
            )

            val dataFromVideoApi = mutableListOf(
                ImageData("video-A","2023-03-20T21:21:00.000+09:00"),
                ImageData("video-B","2023-03-20T21:19:00.000+09:00"),
            )

            val videoQueueData = mutableListOf(
                ImageData("video-pre-A", "2023-03-20T21:21:30.000+09:00"),
            )

            val resultFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("video-pre-A", "2023-03-20T21:21:30.000+09:00"),
                ImageData("video-A","2023-03-20T21:21:00.000+09:00", true),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00", true),
            )

            val query = "test"
            val page1 = 2

            val myImageListString = "|image-B|video-A"

            `when`(mainRepository.searchImage(Constants.Api.PATH_IMAGE, query, page1))
                .thenReturn(dataFromImageApi)

            `when`(mainRepository.searchImage(Constants.Api.PATH_VIDEO, query, page1))
                .thenReturn(dataFromVideoApi)

            `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

            // When
            mainViewModel.setTestDataForGetMoreImage(false, "test", 1, emptyList(), videoQueueData)
            mainViewModel.getMoreImage()

            // Then
            assertEquals(resultFromImageApi, mainViewModel.imageDataList.getOrAwaitValue())

        }
    }

    @Test
    fun getMoreImageTest_when_videoQueue_has_former_data_which_newer_than_image_A() {
        runBlocking {
            // Given
            val dataFromImageApi = mutableListOf(
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00"),
            )

            val dataFromVideoApi = mutableListOf(
                ImageData("video-A","2023-03-20T21:21:00.000+09:00"),
                ImageData("video-B","2023-03-20T21:19:00.000+09:00"),
            )

            val videoQueueData = mutableListOf(
                ImageData("video-pre-A", "2023-03-20T21:23:30.000+09:00"),
            )

            val resultFromImageApi = mutableListOf(
                ImageData("video-pre-A", "2023-03-20T21:23:30.000+09:00"),
                ImageData("image-A","2023-03-20T21:22:00.000+09:00"),
                ImageData("video-A","2023-03-20T21:21:00.000+09:00", true),
                ImageData("image-B","2023-03-20T21:20:00.000+09:00", true),
            )

            val query = "test"
            val page1 = 2

            val myImageListString = "|image-B|video-A"

            `when`(mainRepository.searchImage(Constants.Api.PATH_IMAGE, query, page1))
                .thenReturn(dataFromImageApi)

            `when`(mainRepository.searchImage(Constants.Api.PATH_VIDEO, query, page1))
                .thenReturn(dataFromVideoApi)

            `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

            // When
            mainViewModel.setTestDataForGetMoreImage(false, "test", 1, emptyList(), videoQueueData)
            mainViewModel.getMoreImage()

            // Then
            assertEquals(resultFromImageApi, mainViewModel.imageDataList.getOrAwaitValue())

        }
    }

    @Test
    fun getMyImage_empty() {
        // Given
        val myImageListString = ""

        `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

        // When
        mainViewModel.getMyImage()

        // Then
        assertEquals(mutableListOf<ImageData>(), mainViewModel.myImageDataList.getOrAwaitValue())
        assertEquals(true, mainViewModel.isMyImageEmpty.getOrAwaitValue())
    }

    @Test
    fun getMyImage_notEmpty() {
        // Given
        val myImageListString = "|image-B|video-A"

        val result = mutableListOf(
            ImageData("image-B","", true),
            ImageData("video-A","", true),
        )

        `when`(mainRepository.getMyImageListString()).thenReturn(myImageListString)

        // When
        mainViewModel.getMyImage()

        // Then
        assertEquals(result, mainViewModel.myImageDataList.getOrAwaitValue())
        assertEquals(false, mainViewModel.isMyImageEmpty.getOrAwaitValue())
    }


}
