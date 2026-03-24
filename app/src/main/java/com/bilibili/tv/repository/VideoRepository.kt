package com.bilibili.tv.repository

import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.RecommendResponseData
import com.bilibili.tv.network.model.VideoDetailResponseData
import com.bilibili.tv.network.model.VideoPlayUrlData

class VideoRepository {

    private val apiService = BiliNetworkClient.biliApiService

    companion object {
        private var recommendCache: RecommendResponseData? = null
        private var lastRecommendFetchTime: Long = 0
        private const val CACHE_EXPIRATION = 60 * 1000 // 1 minute cache
    }

    // 获取首页推荐流
    suspend fun getRecommendVideos(ps: Int = 10, forceRefresh: Boolean = false): Result<RecommendResponseData> {
        val currentTime = System.currentTimeMillis()
        if (!forceRefresh && recommendCache != null && (currentTime - lastRecommendFetchTime) < CACHE_EXPIRATION) {
            return Result.success(recommendCache!!)
        }

        return try {
            val response = apiService.getRecommendVideos(ps = ps)
            if (response.isSuccess && response.data != null) {
                recommendCache = response.data
                lastRecommendFetchTime = currentTime
                Result.success(response.data)
            } else {
                Result.failure(Exception("API Error: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 获取视频详情
    suspend fun getVideoDetail(bvid: String): Result<VideoDetailResponseData> {
        return try {
            val response = apiService.getVideoDetail(bvid)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("API Error: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 获取流地址
    suspend fun getVideoPlayUrl(bvid: String, cid: Long, qn: Int = 116): Result<VideoPlayUrlData> {
        return try {
            val response = apiService.getVideoPlayUrl(bvid = bvid, cid = cid, qn = qn)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("API Error: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
