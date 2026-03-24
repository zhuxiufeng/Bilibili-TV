package com.bilibili.tv.ui.screen.following.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.network.CookieManager
import com.bilibili.tv.network.model.following.FollowingItem
import com.bilibili.tv.repository.FollowingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FollowingUiState {
    object Loading : FollowingUiState()
    data class Success(
        val items: List<FollowingItem>,
        val total: Int,
        val isAppending: Boolean = false
    ) : FollowingUiState()
    data class Error(val message: String) : FollowingUiState()
}

class FollowingViewModel : ViewModel() {
    private val repository = FollowingRepository()

    private val _uiState = MutableStateFlow<FollowingUiState>(FollowingUiState.Loading)
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var allItems = mutableListOf<FollowingItem>()
    private var isFetching = false

    init {
        loadFollowings(isRefresh = true)
    }

    fun loadFollowings(isRefresh: Boolean = false) {
        if (isFetching) return

        // We need the user's mid to fetch followings. 
        // In Bilibili API, if we don't have it, we can extract from DedeUserID cookie.
        val cookies = CookieManager.getCookies()
        val midStr = cookies?.find { it.contains("DedeUserID=") }
            ?.substringAfter("DedeUserID=")
            ?.substringBefore(";")
        
        val mid = midStr?.toLongOrNull()

        if (mid == null) {
            _uiState.value = FollowingUiState.Error("无法获取用户信息，请重新登录")
            return
        }

        viewModelScope.launch {
            isFetching = true
            
            if (isRefresh) {
                currentPage = 1
                allItems.clear()
                _uiState.value = FollowingUiState.Loading
            }

            val result = repository.getFollowings(mid, currentPage)
            result.onSuccess { data ->
                allItems.addAll(data.list)
                
                if (allItems.isNotEmpty()) {
                    _uiState.value = FollowingUiState.Success(
                        items = allItems.toList(),
                        total = data.total,
                        isAppending = false
                    )
                    currentPage++
                } else {
                    _uiState.value = FollowingUiState.Error("暂无关注")
                }
            }.onFailure { error ->
                _uiState.value = FollowingUiState.Error(error.localizedMessage ?: "获取关注列表失败")
            }
            
            isFetching = false
        }
    }
}
