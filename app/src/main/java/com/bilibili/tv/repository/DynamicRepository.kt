package com.bilibili.tv.repository

import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.dynamic.DynamicResponseData

class DynamicRepository {

    private val apiService = BiliNetworkClient.biliApiService

    // 获取最新动态列表（根据 offset 处理翻页）
    suspend fun getDynamicFeed(offset: String = ""): Result<DynamicResponseData> {
        return try {
            val response = apiService.getDynamicFeed(offset = offset)
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
