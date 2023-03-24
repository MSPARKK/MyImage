package com.mspark.myimage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mspark.myimage.data.ImageSearchResponse
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.repository.MainLocalDataSource
import com.mspark.myimage.repository.MainRemoteDataSource
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.repository.MainRepositoryImpl
import com.mspark.myimage.util.Constants
import com.mspark.myimage.util.getOrAwaitValue
import com.mspark.myimage.viewmodel.MainViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: MainRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)

        val mockRemoteDataSource = mock(MainRemoteDataSource::class.java)
        val mockLocalDataSource = mock(MainLocalDataSource::class.java)

        mainRepository = MainRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)

        mainViewModel = MainViewModel(mainRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

//    @Test
//    fun testGetString() {
//        val id = 1
//        val string = "test"
//        `when`(mainRepository.getMyImageListString()).thenReturn(string)
//    }

    @Test
    fun `searchNewQuery should update query and reset variables`() {
//        runBlocking {
//            `when`(mainRepository.searchImage(Constants.KakaoApi.PATH_IMAGE,"test", 1))
//                .thenReturn(mutableListOf())
//
//            `when`(mainRepository.searchImage(Constants.KakaoApi.PATH_VIDEO,"test", 1))
//                .thenReturn(mutableListOf())
//
////            // Given
////            val query = "test"
////
////            // When
////            mainViewModel.searchNewQuery(query)
////
////            // Then
////            assertEquals(emptyList<KakaoImage>(), mainViewModel.imageList.getOrAwaitValue())
//
//        }

        runTest {
            withContext(Dispatchers.Main) {
                val dataFromImageApi = mutableListOf(
                    KakaoImage("image-A","2023-03-20T21:22:00.000+09:00"),
                    KakaoImage("image-B","2023-03-20T21:20:00.000+09:00"),
                )

                val dataFromVideoApi = mutableListOf(
                    KakaoImage("video-A","2023-03-20T21:21:00.000+09:00"),
                    KakaoImage("video-B","2023-03-20T21:19:00.000+09:00"),
                )


                val resultFromImageApi = mutableListOf(
                    KakaoImage("image-A","2023-03-20T21:22:00.000+09:00"),
                    KakaoImage("video-A","2023-03-20T21:21:00.000+09:00", true),
                    KakaoImage("image-B","2023-03-20T21:20:00.000+09:00", true),
                )

                `when`(mainRepository.searchImage(Constants.KakaoApi.PATH_IMAGE,"test", 1))
                    .thenReturn(dataFromImageApi)

                `when`(mainRepository.searchImage(Constants.KakaoApi.PATH_VIDEO,"test", 1))
                    .thenReturn(dataFromVideoApi)

                `when`(mainRepository.getMyImageListString()).thenReturn("image-B|video-A")

                // Given
                val query = "test"

                // When
                mainViewModel.searchNewQuery(query)

                // Then
                assertEquals(resultFromImageApi, mainViewModel.imageList.getOrAwaitValue())

            }

        }
    }


}
