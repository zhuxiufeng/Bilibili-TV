package com.bilibili.tv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
private val TvDarkColorScheme = darkColorScheme(
    primary = BiliPink,
    onPrimary = TextPrimary,
    secondary = BiliPinkDark,
    background = BiliDarkBackground,
    surface = BiliDarkSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BilibiliTvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // TV apps typically force dark mode for better viewing experience
    val colorScheme = TvDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
