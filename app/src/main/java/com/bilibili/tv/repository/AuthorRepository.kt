package com.bilibili.tv.repository

import android.util.Log
import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.author.AuthorVideoItem
import com.bilibili.tv.network.model.author.AuthorVideoResponseData

class AuthorRepository {
    private val apiService = BiliNetworkClient.biliApiService

    /**
     * 获取 UP 主视频列表
     * 针对 -400 错误的最终防御逻辑
     */
    suspend fun getAuthorVideos(mid: Long, page: Int = 1): Result<AuthorVideoResponseData> {
        // 关键防御：如果 mid 看起来像是一个 16 位的视频/动态 ID，直接拦截
        if (mid > 2000000000L || mid <= 0) {
            val error = "ID 异常: $mid。这看起来不是一个有效的 UP 主 mid，请确认传入的是 owner.mid"
            Log.e("AuthorRepository", error)
            return Result.failure(Exception(error))
        }

        return try {
            // 优先使用 Web 搜索接口，这是目前最通用的方式
            val response = apiService.getAuthorVideosWeb(mid = mid, page = page)
            if (response.code == 0 && response.data?.list?.vlist != null) {
                val legacyList = response.data.list.vlist.map { webItem ->
                    AuthorVideoItem(
                        bvid = webItem.bvid,
                        title = webItem.title,
                        pic = webItem.pic,
                        desc = webItem.desc,
                        duration = webItem.duration,
                        view = webItem.view
                    )
                }
                Result.success(AuthorVideoResponseData(list = legacyList))
            } else if (response.code == -400 || response.code == -799) {
                // 如果 Web 接口被风控或参数报错，尝试使用 APP 接口作为最后保底
                Log.w("AuthorRepository", "Web API Error ${response.code}, trying App API...")
                val appResponse = apiService.getAuthorVideos(mid = mid, page = page)
                if (appResponse.code == 0 && appResponse.data != null) {
                    Result.success(appResponse.data)
                } else {
                    Result.failure(Exception("API Error: ${appResponse.code} ${appResponse.message}"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e("AuthorRepository", "Request Exception", e)
            Result.failure(e)
        }
    }
}
