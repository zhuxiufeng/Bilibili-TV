package com.bilibili.tv.ui.screen.author

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.bilibili.tv.ui.component.VideoCard
import com.bilibili.tv.ui.screen.author.viewmodel.AuthorSpaceUiState
import com.bilibili.tv.ui.screen.author.viewmodel.AuthorSpaceViewModel

@Composable
fun AuthorSpaceScreen(
    mid: Long,
    modifier: Modifier = Modifier,
    onVideoClick: (bvid: String) -> Unit
) {
    val viewModel: AuthorSpaceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthorSpaceViewModel(mid) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is AuthorSpaceUiState.Loading -> {
                Text(text = "正在加载 UP 主的视频...", color = MaterialTheme.colorScheme.onBackground)
            }
            is AuthorSpaceUiState.Error -> {
                Text(text = "出错了: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            is AuthorSpaceUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.videos, key = { it.bvid }) { video ->
                        VideoCard(
                            videoItem = video,
                            onClick = { onVideoClick(video.bvid) }
                        )
                    }
                }
            }
        }
    }
}
