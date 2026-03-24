package com.bilibili.tv.repository

import com.bilibili.tv.network.BiliNetworkClient
import com.bilibili.tv.network.model.login.QrCodeGenerateData
import com.bilibili.tv.network.model.login.QrCodePollData
import retrofit2.Response

class AuthRepository {

    private val apiService = BiliNetworkClient.biliApiService

    // 获取登录二维码图片及其对应的 qrcode_key
    suspend fun getLoginQrCode(): Result<QrCodeGenerateData> {
        return try {
            val response = apiService.getLoginQrCode()
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to get QR Code: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 轮询二维码状态并捕获可能返回的 Cookie
    suspend fun pollLoginQrCode(qrcodeKey: String): Result<QrCodePollData> {
        return try {
            val response = apiService.pollLoginQrCode(qrcodeKey)
            
            // Code 0: Login Success (Wait to extract Set-Cookie header)
            // Code 86038: QR Code Expired
            // Code 86090: Scan Success but not confirmed
            // Code 86101: Wait to scan
            
            if (response.data != null) {
                // If login success, we must parse headers later to store cookies
                // Usually retrofit `Response<T>` provides headers, but here we used raw model response.
                // We'll process URL parameter if `code == 0` for basic tokens 
                Result.success(response.data)
            } else {
                Result.failure(Exception("Poll Failed: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
