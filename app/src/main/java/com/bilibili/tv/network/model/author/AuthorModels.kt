package com.bilibili.tv.network.model.author

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 移动端空间视频接口响应模型
 * 接口: https://app.bilibili.com/x/v2/space/archive
 */
@JsonClass(generateAdapter = true)
data class AuthorVideoResponseData(
    @Json(name = "item") val list: List<AuthorVideoItem>?
)

@JsonClass(generateAdapter = true)
data class AuthorVideoItem(
    @Json(name = "param") val bvid: String,
    @Json(name = "title") val title: String,
    @Json(name = "cover") val pic: String,
    @Json(name = "desc") val desc: String?,
    @Json(name = "duration") val duration: String?,
    @Json(name = "play") val view: Int?
)

/**
 * Web 端空间视频接口响应模型
 * 接口: https://api.bilibili.com/x/space/arc/search
 */
@JsonClass(generateAdapter = true)
data class AuthorWebVideoResponseData(
    @Json(name = "list") val list: AuthorWebVideoList?
)

@JsonClass(generateAdapter = true)
data class AuthorWebVideoList(
    @Json(name = "vlist") val vlist: List<AuthorWebVideoItem>?
)

@JsonClass(generateAdapter = true)
data class AuthorWebVideoItem(
    @Json(name = "bvid") val bvid: String,
    @Json(name = "title") val title: String,
    @Json(name = "pic") val pic: String,
    @Json(name = "description") val desc: String?,
    @Json(name = "length") val duration: String?,
    @Json(name = "play") val view: Int?
)
