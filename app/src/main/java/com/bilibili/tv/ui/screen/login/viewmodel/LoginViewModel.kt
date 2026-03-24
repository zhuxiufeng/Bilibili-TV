package com.bilibili.tv.ui.screen.login.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.repository.AuthRepository
import com.bilibili.tv.utils.QrCodeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object LoadingQr : LoginUiState()
    data class QrReady(val qrBitmap: Bitmap, val qrcodeKey: String, val message: String = "请使用哔哩哔哩客户端扫码登录") : LoginUiState()
    object LoginSuccess : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun startLoginFlow() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.LoadingQr
            val result = repository.getLoginQrCode()
            result.onSuccess { data ->
                val bitmap = QrCodeUtil.generateQrCodeBitmap(data.url)
                if (bitmap != null) {
                    _uiState.value = LoginUiState.QrReady(bitmap, data.qrcodeKey)
                    startPolling(data.qrcodeKey)
                } else {
                    _uiState.value = LoginUiState.Error("二维码图片生成失败")
                }
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error.localizedMessage ?: "获取二维码失败")
            }
        }
    }

    private fun startPolling(qrcodeKey: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000) // Poll every 3 seconds
                val result = repository.pollLoginQrCode(qrcodeKey)
                
                result.onSuccess { data ->
                    when (data.code) {
                        0 -> {
                            // Login Success. In real case, we need to extract cookies (SESSDATA, bili_jct, DedeUserID) from headers
                            // Since we don't have Retrofit raw response here currently, this is a simplified success state.
                            _uiState.value = LoginUiState.LoginSuccess
                            cancelPolling()
                        }
                        86038 -> {
                            // QR Expired, refresh automatically
                            _uiState.value = LoginUiState.Error("二维码已过期，正在刷新...")
                            cancelPolling()
                            delay(1000)
                            startLoginFlow()
                        }
                        86090 -> {
                            // Scanned, waiting for confirm
                            val currentState = _uiState.value
                            if (currentState is LoginUiState.QrReady) {
                                _uiState.value = currentState.copy(message = "扫码成功，请在手机上确认登录")
                            }
                        }
                        86101 -> {
                            // Not scanned yet, do nothing and keep polling
                        }
                        else -> {
                            _uiState.value = LoginUiState.Error("未知的登录状态码: ${data.code}")
                            cancelPolling()
                        }
                    }
                }.onFailure {
                    // Ignore transient network errors during polling
                }
            }
        }
    }

    private fun cancelPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelPolling()
    }
}
