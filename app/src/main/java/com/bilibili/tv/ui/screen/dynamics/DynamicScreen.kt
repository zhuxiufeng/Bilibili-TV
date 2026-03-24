package com.bilibili.tv.ui.screen.dynamics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.bilibili.tv.network.model.dynamic.DynamicItem
import com.bilibili.tv.ui.screen.dynamics.viewmodel.DynamicUiState
import com.bilibili.tv.ui.screen.dynamics.viewmodel.DynamicViewModel

@Composable
fun DynamicScreen(
    modifier: Modifier = Modifier,
    viewModel: DynamicViewModel = viewModel(),
    onVideoClick: (bvid: String) -> Unit,
    onAuthorClick: (mid: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is DynamicUiState.Loading -> {
                Text("正在拉取动态...", color = MaterialTheme.colorScheme.onBackground)
            }
            is DynamicUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is DynamicUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(state.items, key = { _, item -> item.idStr }) { index, item ->
                        if (index == state.items.size - 4 && state.hasMore && !state.isAppending) {
                            LaunchedEffect(index) {
                                viewModel.loadDynamics()
                            }
                        }
                        
                        DynamicCard(
                            item = item, 
                            onVideoClick = onVideoClick,
                            onAuthorClick = onAuthorClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DynamicCard(
    item: DynamicItem,
    modifier: Modifier = Modifier,
    onVideoClick: (bvid: String) -> Unit,
    onAuthorClick: (mid: Long) -> Unit
) {
    val author = item.modules.moduleAuthor
    val content = item.modules.moduleDynamic
    
    val archive = content.major?.archive
    val bvid = archive?.bvid
    val cover = archive?.cover ?: content.major?.draw?.items?.firstOrNull()?.src
    val title = archive?.title ?: content.desc?.text ?: "无标题动态"

    Card(
        onClick = { if (bvid != null) onVideoClick(bvid) },
        modifier = modifier.fillMaxWidth(),
        scale = CardScale.None,
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Column {
            // 点击头部进入 UP 主空间
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAuthorClick(author.mid) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = author.face,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = author.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = author.pubTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (cover != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                ) {
                    AsyncImage(
                        model = cover,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
