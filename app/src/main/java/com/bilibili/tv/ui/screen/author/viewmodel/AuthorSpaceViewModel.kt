package com.bilibili.tv.ui.screen.author.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.network.model.VideoItem
import com.bilibili.tv.network.model.VideoOwner
import com.bilibili.tv.network.model.VideoStat
import com.bilibili.tv.repository.AuthorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthorSpaceUiState {
    object Loading : AuthorSpaceUiState()
    data class Success(val videos: List<VideoItem>) : AuthorSpaceUiState()
    data class Error(val message: String) : AuthorSpaceUiState()
}

class AuthorSpaceViewModel(
    private val mid: Long,
    private val repository: AuthorRepository = AuthorRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthorSpaceUiState>(AuthorSpaceUiState.Loading)
    val uiState: StateFlow<AuthorSpaceUiState> = _uiState.asStateFlow()

    init {
        loadAuthorVideos()
    }

    fun loadAuthorVideos() {
        viewModelScope.launch {
            _uiState.value = AuthorSpaceUiState.Loading
            val result = repository.getAuthorVideos(mid)
            result.onSuccess { data ->
                // 适配移动端接口的数据结构
                val videoItems = data.list?.map { item ->
                    VideoItem(
                        bvid = item.bvid,
                        title = item.title,
                        pic = item.pic,
                        desc = item.desc ?: "",
                        owner = VideoOwner(mid = mid, name = "", face = ""),
                        stat = VideoStat(view = item.view ?: 0, danmaku = 0, reply = 0),
                        duration = 0
                    )
                } ?: emptyList()
                _uiState.value = AuthorSpaceUiState.Success(videoItems)
            }.onFailure { error ->
                _uiState.value = AuthorSpaceUiState.Error(error.localizedMessage ?: "加载视频失败")
            }
        }
    }
}
