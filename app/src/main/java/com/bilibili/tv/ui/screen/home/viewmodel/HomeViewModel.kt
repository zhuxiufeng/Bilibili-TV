package com.bilibili.tv.ui.screen.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.network.model.VideoItem
import com.bilibili.tv.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val videos: List<VideoItem>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {
    private val repository = VideoRepository()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var isFetching = false

    init {
        loadRecommendVideos()
    }

    fun loadRecommendVideos(forceRefresh: Boolean = false) {
        if (isFetching) return
        
        // 如果不是强制刷新，且当前已有数据，则不再发起请求
        if (!forceRefresh && _uiState.value is HomeUiState.Success) return

        viewModelScope.launch {
            isFetching = true
            _uiState.value = HomeUiState.Loading
            
            val result = repository.getRecommendVideos(forceRefresh = forceRefresh)
            result.onSuccess { data ->
                val videos = data.items ?: data.list
                if (!videos.isNullOrEmpty()) {
                    _uiState.value = HomeUiState.Success(videos)
                } else {
                    _uiState.value = HomeUiState.Error("没有推荐数据")
                }
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(error.localizedMessage ?: "未知错误")
            }
            
            isFetching = false
        }
    }
}
