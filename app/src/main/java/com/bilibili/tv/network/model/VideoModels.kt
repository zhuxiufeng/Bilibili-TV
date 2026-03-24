package com.bilibili.tv.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoItem(
    @Json(name = "bvid") val bvid: String,
    @Json(name = "title") val title: String,
    @Json(name = "pic") val pic: String, // Cover image URL
    @Json(name = "desc") val desc: String?,
    @Json(name = "owner") val owner: VideoOwner?,
    @Json(name = "stat") val stat: VideoStat?,
    @Json(name = "duration") val duration: Long? = 0 // In seconds typically
)

@JsonClass(generateAdapter = true)
data class VideoOwner(
    @Json(name = "mid") val mid: Long,
    @Json(name = "name") val name: String,
    @Json(name = "face") val face: String // Avatar URL
)

@JsonClass(generateAdapter = true)
data class VideoStat(
    @Json(name = "view") val view: Int = 0,
    @Json(name = "danmaku") val danmaku: Int = 0,
    @Json(name = "reply") val reply: Int? = 0,
    @Json(name = "favorite") val favorite: Int? = 0,
    @Json(name = "coin") val coin: Int? = 0,
    @Json(name = "share") val share: Int? = 0,
    @Json(name = "like") val like: Int? = 0
)

@JsonClass(generateAdapter = true)
data class RecommendResponseData(
    @Json(name = "item") val items: List<VideoItem>? = null,
    @Json(name = "list") val list: List<VideoItem>? = null
)
