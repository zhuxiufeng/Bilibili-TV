package com.bilibili.tv.network.model.following

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowingResponseData(
    @Json(name = "list") val list: List<FollowingItem>,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class FollowingItem(
    @Json(name = "mid") val mid: Long,
    @Json(name = "uname") val name: String,
    @Json(name = "face") val face: String,
    @Json(name = "sign") val sign: String?,
    @Json(name = "special") val special: Int
)
