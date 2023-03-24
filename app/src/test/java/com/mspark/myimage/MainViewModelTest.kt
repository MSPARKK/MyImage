package com.mspark.myimage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.repository.MainLocalDataSource
import com.mspark.myimage.repository.MainRemoteDataSource
import com.mspark.myimage.repository.MainRepository
import com.mspark.myimage.repository.MainRepositoryImpl
import com.mspark.myimage.util.getOrAwaitValue
import com.mspark.myimage.viewmodel.MainViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private val testDispatcher = StandardTestDispatcher()
    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: MainRepository



    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
//        mainRepository = MainRepositoryImpl.getRepository(context)

        val mockRemoteDataSource = mock(MainRemoteDataSource::class.java)
        val mockLocalDataSource = mock(MainLocalDataSource::class.java)

        mainRepository = MainRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)

        mainViewModel = MainViewModel(mainRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetString() {
        val id = 1
        val string = "test"
        `when`(mainRepository.getMyImageListString()).thenReturn(string)
    }

    @Test
    fun `searchNewQuery should update query and reset variables`() {
        // Given
        val query = "test query"

        // When
        mainViewModel.searchNewQuery(query)

        // Then
        assertEquals(emptyList<KakaoImage>(), mainViewModel.imageList.getOrAwaitValue())
//        assertTrue(mainViewModel.totalImageList.isEmpty())
//        assertTrue(mainViewModel.imageQueue.isEmpty())
//        assertTrue(mainViewModel.videoQueue.isEmpty())
//        assertEquals(1, mainViewModel.imagePage)
//        assertEquals(1, mainViewModel.videoPage)
    }

//    @Test
//    fun getAllMoviesTest() {
//        runBlocking {
//            Mockito.`when`(mainRepository.searchImage("test",1))
//                .thenReturn(retrofit2.Response<ImageSearchResponse>)
//            mainViewModel.getAllMovies()
//            val result = mainViewModel.movieList.getOrAwaitValue()
//            assertEquals(listOf<Movie>(Movie("movie", "", "new")), result)
//        }
//    }


}
