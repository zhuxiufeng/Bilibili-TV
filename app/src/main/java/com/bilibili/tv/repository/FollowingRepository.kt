package com.bilibili.tv.repository

import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.following.FollowingResponseData

class FollowingRepository {
    private val apiService = BiliNetworkClient.biliApiService

    companion object {
        private val followingCache = mutableMapOf<Int, FollowingResponseData>()
        private var lastFetchTime: Long = 0
        private const val CACHE_EXPIRATION = 5 * 60 * 1000 // 5 minutes
    }

    suspend fun getFollowings(vmid: Long, page: Int = 1, forceRefresh: Boolean = false): Result<FollowingResponseData> {
        val currentTime = System.currentTimeMillis()
        if (!forceRefresh && followingCache.containsKey(page) && (currentTime - lastFetchTime) < CACHE_EXPIRATION) {
            return Result.success(followingCache[page]!!)
        }

        return try {
            val response = apiService.getFollowings(vmid = vmid, page = page)
            if (response.isSuccess && response.data != null) {
                followingCache[page] = response.data
                lastFetchTime = currentTime
                Result.success(response.data)
            } else {
                Result.failure(Exception("API Error: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
