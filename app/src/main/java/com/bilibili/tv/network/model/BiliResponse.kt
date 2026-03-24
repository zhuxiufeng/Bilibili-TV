package com.bilibili.tv.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 通用的 Bilibili API 响应包装类
 */
@JsonClass(generateAdapter = true)
data class BiliResponse<T>(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String?,
    @Json(name = "ttl") val ttl: Int?,
    @Json(name = "data") val data: T?
) {
    val isSuccess: Boolean get() = code == 0
}
