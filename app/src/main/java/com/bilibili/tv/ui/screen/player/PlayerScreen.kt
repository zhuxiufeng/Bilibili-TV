package com.bilibili.tv.ui.screen.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.bilibili.tv.player.ExoPlayerWrapper
import com.bilibili.tv.ui.screen.player.viewmodel.PlayerUiState
import com.bilibili.tv.ui.screen.player.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerScreen(
    bvid: String,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQualityMenu by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(bvid) {
        viewModel.loadVideo(bvid = bvid)
    }

    // 自动隐藏菜单逻辑
    LaunchedEffect(showQualityMenu) {
        if (showQualityMenu) {
            delay(5000) 
            showQualityMenu = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onKeyEvent {
                if (it.key == Key.DirectionUp || it.key == Key.DirectionDown || it.key == Key.Settings || it.key == Key.Menu) {
                    showQualityMenu = true
                    true
                } else if (it.key == Key.Back) {
                    if (showQualityMenu) {
                        showQualityMenu = false
                        true
                    } else {
                        onBackPressed()
                        true
                    }
                } else if (it.key == Key.DirectionCenter || it.key == Key.Enter || it.key == Key.Spacebar) {
                    isPlaying = !isPlaying
                    true
                } else {
                    false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is PlayerUiState.Idle, is PlayerUiState.Loading -> {
                Text(
                    text = "正在解析视频地址...",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            is PlayerUiState.Error -> {
                Text(
                    text = "加载失败: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            is PlayerUiState.Ready -> {
                ExoPlayerWrapper(
                    videoUrl = state.videoUrl,
                    audioUrl = state.audioUrl,
                    isPlaying = isPlaying,
                    onClick = { isPlaying = !isPlaying }, // 允许鼠标点击切换暂停
                    modifier = Modifier.fillMaxSize()
                )

                // 暂停时的视觉反馈
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Paused",
                            modifier = Modifier.size(64.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // 分辨率选择菜单
                if (showQualityMenu) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(200.dp)
                                .fillMaxSize(),
                            onClick = {} 
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "选择清晰度",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                LazyColumn {
                                    items(state.availableQualities) { quality ->
                                        Surface(
                                            onClick = {
                                                viewModel.switchQuality(quality.id)
                                                showQualityMenu = false
                                            },
                                            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .width(160.dp)
                                        ) {
                                            Text(
                                                text = quality.description,
                                                modifier = Modifier.padding(16.dp),
                                                color = if (state.currentQuality == quality.id) 
                                                    MaterialTheme.colorScheme.primary 
                                                else 
                                                    Color.Unspecified
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
