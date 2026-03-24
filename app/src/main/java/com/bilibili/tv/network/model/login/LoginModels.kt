package com.bilibili.tv.network.model.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QrCodeGenerateData(
    @Json(name = "url") val url: String,
    @Json(name = "qrcode_key") val qrcodeKey: String
)

@JsonClass(generateAdapter = true)
data class QrCodePollData(
    @Json(name = "url") val url: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String
)
