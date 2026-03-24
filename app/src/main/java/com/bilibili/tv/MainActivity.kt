package com.bilibili.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.bilibili.tv.network.CookieManager
import com.bilibili.tv.network.interceptor.BiliHeaderInterceptor
import com.bilibili.tv.ui.screen.author.AuthorSpaceScreen
import com.bilibili.tv.ui.screen.dynamics.DynamicScreen
import com.bilibili.tv.ui.screen.following.FollowingScreen
import com.bilibili.tv.ui.screen.home.HomeScreen
import com.bilibili.tv.ui.screen.login.LoginScreen
import com.bilibili.tv.ui.screen.player.PlayerScreen
import com.bilibili.tv.ui.theme.BilibiliTvTheme
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient

@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : ComponentActivity(), ImageLoaderFactory {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        CookieManager.init(this)
        
        setContent {
            BilibiliTvTheme {
                var isLoggedIn by remember { mutableStateOf(CookieManager.isLogin()) }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val navController = rememberNavController()
                    val selectedTabIndex = remember { mutableIntStateOf(0) }
                    var focusedTabIndex by remember { mutableIntStateOf(0) }
                    
                    val tabs = listOf(
                        "首页推荐" to "home", 
                        "订阅动态" to "dynamics",
                        "我的关注" to "following",
                        "退出登录" to "logout"
                    )

                    LaunchedEffect(focusedTabIndex) {
                        if (focusedTabIndex != selectedTabIndex.intValue) {
                            val targetRoute = tabs[focusedTabIndex].second
                            if (targetRoute != "logout") {
                                delay(1200)
                                selectedTabIndex.intValue = focusedTabIndex
                                navController.navigate(targetRoute) { 
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        val currentBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry?.destination?.route

                        val showTopBar = isLoggedIn && (currentRoute == "home" || currentRoute == "dynamics" || currentRoute == "following" || currentRoute == null)

                        if (showTopBar) {
                            TabRow(
                                selectedTabIndex = selectedTabIndex.intValue,
                                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp)
                            ) {
                                tabs.forEachIndexed { index, pair ->
                                    Tab(
                                        selected = selectedTabIndex.intValue == index,
                                        onFocus = {
                                            focusedTabIndex = index
                                        },
                                        onClick = {
                                            if (pair.second == "logout") {
                                                CookieManager.clearCookies()
                                                isLoggedIn = false
                                                navController.navigate("login") {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            } else {
                                                focusedTabIndex = index
                                                selectedTabIndex.intValue = index
                                                navController.navigate(pair.second) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo("home")
                                                }
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = pair.first,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "login") {
                                composable("login") {
                                    LoginScreen(
                                        onLoginSuccess = {
                                            isLoggedIn = true
                                            selectedTabIndex.intValue = 0
                                            focusedTabIndex = 0
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable("home") {
                                    HomeScreen(
                                        onVideoClick = { bvid ->
                                            navController.navigate("player/$bvid")
                                        },
                                        onAuthorClick = { mid ->
                                            navController.navigate("author/$mid")
                                        }
                                    )
                                }

                                composable("dynamics") {
                                    DynamicScreen(
                                        onVideoClick = { bvid ->
                                            navController.navigate("player/$bvid")
                                        },
                                        onAuthorClick = { mid ->
                                            navController.navigate("author/$mid")
                                        }
                                    )
                                }

                                composable("following") {
                                    FollowingScreen(
                                        onAuthorClick = { mid ->
                                            navController.navigate("author/$mid")
                                        }
                                    )
                                }

                                composable(
                                    route = "author/{mid}",
                                    arguments = listOf(navArgument("mid") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val mid = backStackEntry.arguments?.getLong("mid") ?: 0L
                                    AuthorSpaceScreen(
                                        mid = mid,
                                        onVideoClick = { bvid ->
                                            navController.navigate("player/$bvid")
                                        }
                                    )
                                }

                                composable(
                                    route = "player/{bvid}",
                                    arguments = listOf(navArgument("bvid") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val bvid = backStackEntry.arguments?.getString("bvid") ?: ""
                                    PlayerScreen(
                                        bvid = bvid,
                                        onBackPressed = {
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(BiliHeaderInterceptor())
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
