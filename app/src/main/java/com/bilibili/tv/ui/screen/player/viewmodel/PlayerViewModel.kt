package com.bilibili.tv.ui.screen.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.network.model.DashTrack
import com.bilibili.tv.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoQuality(
    val id: Int,
    val description: String
)

sealed class PlayerUiState {
    object Idle : PlayerUiState()
    object Loading : PlayerUiState()
    data class Ready(
        val bvid: String,
        val title: String,
        val videoUrl: String,
        val audioUrl: String? = null,
        val currentQuality: Int,
        val availableQualities: List<VideoQuality> = emptyList(),
        val isDanmakuEnabled: Boolean = true
    ) : PlayerUiState()
    data class Error(val message: String) : PlayerUiState()
}

class PlayerViewModel : ViewModel() {
    private val repository = VideoRepository()

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var currentCid: Long = 0
    private var currentBvid: String = ""

    fun loadVideo(bvid: String, quality: Int? = null) {
        currentBvid = bvid
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = PlayerUiState.Loading
            
            // 1. Fetch Video Detail if cid is unknown
            if (currentCid == 0L) {
                val detailResult = repository.getVideoDetail(bvid)
                if (detailResult.isFailure) {
                    _uiState.value = PlayerUiState.Error("无法获取视频信息")
                    return@launch
                }
                currentCid = detailResult.getOrNull()?.cid ?: 0L
            }

            // 2. Fetch PlayUrl
            // quality != null means user manually switched resolution
            val playUrlResult = repository.getVideoPlayUrl(bvid, currentCid, qn = quality ?: 80) // Default to 1080P (80)
            if (playUrlResult.isFailure) {
                _uiState.value = PlayerUiState.Error("无法获取视频播放地址")
                return@launch
            }
            val playData = playUrlResult.getOrNull()!!

            val qualities = playData.acceptQuality.zip(playData.acceptDescription).map {
                VideoQuality(it.first, it.second)
            }

            if (playData.dash != null) {
                // Find track matching requested quality or best available
                val videoTrack = if (quality != null) {
                    playData.dash.videoList.find { it.id == quality } ?: playData.dash.videoList.maxByOrNull { it.bandwidth }
                } else {
                    playData.dash.videoList.maxByOrNull { it.bandwidth }
                }
                
                val audioTrack = playData.dash.audioList?.maxByOrNull { it.bandwidth }
                
                if (videoTrack != null) {
                    _uiState.value = PlayerUiState.Ready(
                        bvid = bvid,
                        title = "视频播放",
                        videoUrl = videoTrack.baseUrl,
                        audioUrl = audioTrack?.baseUrl,
                        currentQuality = videoTrack.id,
                        availableQualities = qualities
                    )
                    return@launch
                }
            }
            
            _uiState.value = PlayerUiState.Error("不支持的播放格式")
        }
    }

    fun switchQuality(qualityId: Int) {
        loadVideo(currentBvid, qualityId)
    }
}
