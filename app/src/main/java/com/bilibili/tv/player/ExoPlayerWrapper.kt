package com.bilibili.tv.player

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.bilibili.tv.network.CookieManager

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerWrapper(
    videoUrl: String,
    audioUrl: String? = null,
    isPlaying: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e("ExoPlayerWrapper", "Playback error: ${error.message}", error)
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> Log.d("ExoPlayerWrapper", "Buffering...")
                        Player.STATE_READY -> Log.d("ExoPlayerWrapper", "Ready to play")
                        Player.STATE_ENDED -> Log.d("ExoPlayerWrapper", "Playback ended")
                        Player.STATE_IDLE -> Log.d("ExoPlayerWrapper", "Idle")
                    }
                }
            })
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    LaunchedEffect(videoUrl, audioUrl) {
        Log.d("ExoPlayerWrapper", "Preparing sources - Video: $videoUrl, Audio: $audioUrl")

        // 构建包含 Cookie 和 Referer 的 HttpDataSource
        val buvid3 = "FE49B76A-A98B-4B3F-BD4E-7C1C2D9A5F1Einfoc"
        val savedCookies = CookieManager.getCookies()
        val cookieMap = mutableMapOf<String, String>()
        cookieMap["buvid3"] = buvid3
        savedCookies?.forEach { cookie ->
            val pair = cookie.substringBefore(";").split("=", limit = 2)
            if (pair.size == 2) {
                cookieMap[pair[0].trim()] = pair[1].trim()
            }
        }
        val cookieString = cookieMap.entries.joinToString("; ") { "${it.key}=${it.value}" }

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setDefaultRequestProperties(mapOf(
                "Referer" to "https://www.bilibili.com",
                "Origin" to "https://www.bilibili.com",
                "Cookie" to cookieString
            ))
            
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
            
        val finalSource = if (!audioUrl.isNullOrEmpty()) {
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(audioUrl))
            MergingMediaSource(videoSource, audioSource)
        } else {
            videoSource
        }

        exoPlayer.setMediaSource(finalSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = isPlaying
    }

    AndroidView(
        factory = { ctx: Context ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick() }
    )
}
