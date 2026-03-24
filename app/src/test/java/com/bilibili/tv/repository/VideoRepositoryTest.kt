package com.bilibili.tv.repository

import com.bilibili.tv.network.BiliApiService
import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.BiliResponse
import com.bilibili.tv.network.model.VideoPlayUrlData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VideoRepositoryTest {

    private lateinit var repository: VideoRepository
    private val mockApiService = mockk<BiliApiService>()

    @Before
    fun setup() {
        mockkObject(BiliNetworkClient)
        every { BiliNetworkClient.biliApiService } returns mockApiService
        repository = VideoRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getVideoPlayUrl should pass qn parameter to apiService`() = runTest {
        // Arrange
        val bvid = "BV1test"
        val cid = 12345L
        val qn = 80
        val mockData = mockk<VideoPlayUrlData>()
        val mockResponse = BiliResponse(code = 0, message = "success", ttl = 1, data = mockData)

        coEvery { 
            mockApiService.getVideoPlayUrl(bvid = bvid, cid = cid, qn = qn) 
        } returns mockResponse

        // Act
        val result = repository.getVideoPlayUrl(bvid, cid, qn = qn)

        // Assert
        assert(result.isSuccess)
        assertEquals(mockData, result.getOrNull())
    }

    @Test
    fun `getVideoPlayUrl should use default qn when not provided`() = runTest {
        // Arrange
        val bvid = "BV1test"
        val cid = 12345L
        val defaultQn = 116
        val mockData = mockk<VideoPlayUrlData>()
        val mockResponse = BiliResponse(code = 0, message = "success", ttl = 1, data = mockData)

        coEvery { 
            mockApiService.getVideoPlayUrl(bvid = bvid, cid = cid, qn = defaultQn) 
        } returns mockResponse

        // Act
        val result = repository.getVideoPlayUrl(bvid, cid)

        // Assert
        assert(result.isSuccess)
    }
}
