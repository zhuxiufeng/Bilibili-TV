package com.bilibili.tv.network

import com.bilibili.tv.network.model.BiliResponse
import com.bilibili.tv.network.model.RecommendResponseData
import com.bilibili.tv.network.model.author.AuthorVideoResponseData
import com.bilibili.tv.network.model.following.FollowingResponseData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BiliApiService {

    @GET("https://api.bilibili.com/x/web-interface/index/top/rcmd")
    suspend fun getRecommendVideos(
        @Query("ps") ps: Int = 10
    ): BiliResponse<RecommendResponseData>
    
    @GET("https://passport.bilibili.com/x/passport-login/web/qrcode/generate")
    suspend fun getLoginQrCode(): BiliResponse<com.bilibili.tv.network.model.login.QrCodeGenerateData>

    @GET("https://passport.bilibili.com/x/passport-login/web/qrcode/poll")
    suspend fun pollLoginQrCode(
        @Query("qrcode_key") qrcodeKey: String
    ): BiliResponse<com.bilibili.tv.network.model.login.QrCodePollData>

    @GET("https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/all")
    suspend fun getDynamicFeed(
        @Query("offset") offset: String = "",
        @Query("type") type: String = "all"
    ): BiliResponse<com.bilibili.tv.network.model.dynamic.DynamicResponseData>

    @GET("https://api.bilibili.com/x/web-interface/view")
    suspend fun getVideoDetail(
        @Query("bvid") bvid: String
    ): BiliResponse<com.bilibili.tv.network.model.VideoDetailResponseData>

    @GET("https://api.bilibili.com/x/player/playurl")
    suspend fun getVideoPlayUrl(
        @Query("bvid") bvid: String,
        @Query("cid") cid: Long,
        @Query("qn") qn: Int = 80,
        @Query("fnval") fnval: Int = 4048,
        @Query("fourk") fourk: Int = 1
    ): BiliResponse<com.bilibili.tv.network.model.VideoPlayUrlData>

    @GET("https://api.bilibili.com/x/relation/followings")
    suspend fun getFollowings(
        @Query("vmid") vmid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 50
    ): BiliResponse<FollowingResponseData>

    /**
     * 最终修复：使用 TV 端空间列表接口
     * 此接口在 TV 版 build 下通常不需要 WBI 签名
     */
    @GET("https://app.bilibili.com/x/v2/space/archive")
    suspend fun getAuthorVideos(
        @Query("vmid") mid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 30,
        @Query("mobi_app") mobiApp: String = "android_tv",
        @Query("platform") platform: String = "android",
        @Query("build") build: Int = 1031010
    ): BiliResponse<AuthorVideoResponseData>

    /**
     * Web 版空间搜索接口 - 仅作为备选
     */
    @GET("https://api.bilibili.com/x/space/arc/search")
    suspend fun getAuthorVideosWeb(
        @Query("mid") mid: Long,
        @Query("pn") page: Int = 1,
        @Header("Referer") referer: String = "https://space.bilibili.com"
    ): BiliResponse<com.bilibili.tv.network.model.author.AuthorWebVideoResponseData>
}
