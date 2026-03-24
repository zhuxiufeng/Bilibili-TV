package com.bilibili.tv.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Represents the data structure from Bilibili video detail API (/x/web-interface/view)
@JsonClass(generateAdapter = true)
data class VideoDetailResponseData(
    @Json(name = "bvid") val bvid: String,
    @Json(name = "aid") val aid: Long,
    @Json(name = "cid") val cid: Long,
    @Json(name = "title") val title: String,
    @Json(name = "desc") val desc: String,
    @Json(name = "pic") val pic: String,
    @Json(name = "pages") val pages: List<VideoPage>
)

@JsonClass(generateAdapter = true)
data class VideoPage(
    @Json(name = "cid") val cid: Long,
    @Json(name = "page") val page: Int,
    @Json(name = "part") val part: String,
    @Json(name = "duration") val duration: Long
)

// Represents the play stream info (/x/player/playurl)
@JsonClass(generateAdapter = true)
data class VideoPlayUrlData(
    @Json(name = "quality") val quality: Int,
    @Json(name = "format") val format: String,
    @Json(name = "timelength") val timelength: Long,
    @Json(name = "accept_quality") val acceptQuality: List<Int>,
    @Json(name = "accept_description") val acceptDescription: List<String>,
    @Json(name = "dash") val dash: DashInfo?
)

@JsonClass(generateAdapter = true)
data class DashInfo(
    @Json(name = "duration") val duration: Long,
    @Json(name = "minBufferTime") val minBufferTime: Float,
    @Json(name = "video") val videoList: List<DashTrack>,
    @Json(name = "audio") val audioList: List<DashTrack>?
)

@JsonClass(generateAdapter = true)
data class DashTrack(
    @Json(name = "id") val id: Int, // Indicates resolution/quality scale
    @Json(name = "baseUrl") val baseUrl: String,
    @Json(name = "backupUrl") val backupUrl: List<String>?,
    @Json(name = "bandwidth") val bandwidth: Long,
    @Json(name = "codecid") val codecid: Int,
    @Json(name = "codecs") val codecs: String
)
