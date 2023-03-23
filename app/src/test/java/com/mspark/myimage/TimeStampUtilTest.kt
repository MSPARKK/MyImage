package com.mspark.myimage

import com.mspark.myimage.util.BuildVersionProvider
import com.mspark.myimage.util.TimeStampUtil
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class TimeStampUtilTest {

    @Mock
    private lateinit var mockBuildProvider: BuildVersionProvider

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getTimeStamp_returnsCorrectFormattedTimestamp_whenIsOreoAndAbove() {
        // Arrange
        Mockito.`when`(mockBuildProvider.isOreoAndAbove()).thenReturn(true)

        assertEquals("17.06.21 15:59", TimeStampUtil.getTimeStamp("2017-06-21T15:59:30.000+09:00", mockBuildProvider))
    }

    @Test
    fun getTimeStamp_returnsWrongFormattedTimestamp_whenIsOreoAndAbove() {
        // Arrange
        Mockito.`when`(mockBuildProvider.isOreoAndAbove()).thenReturn(true)

        assertEquals("", TimeStampUtil.getTimeStamp("2017-06-21&^T15:59:30.00", mockBuildProvider))
    }

    @Test
    fun getTimeStamp_returnsCorrectFormattedTimestamp_whenIsUnderOreo() {
        // Arrange
        Mockito.`when`(mockBuildProvider.isOreoAndAbove()).thenReturn(false)

        assertEquals("17.06.21 15:59", TimeStampUtil.getTimeStamp("2017-06-21T15:59:30.000+09:00", mockBuildProvider))
    }

    @Test
    fun getTimeStamp_returnsWrongFormattedTimestamp_whenIsUnderOreo() {
        // Arrange
        Mockito.`when`(mockBuildProvider.isOreoAndAbove()).thenReturn(true)

        assertEquals("", TimeStampUtil.getTimeStamp("2017-06-21 15:59:30", mockBuildProvider))
    }
}