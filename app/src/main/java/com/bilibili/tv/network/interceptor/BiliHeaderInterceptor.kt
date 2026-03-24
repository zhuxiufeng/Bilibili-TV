package com.bilibili.tv.network.interceptor

import com.bilibili.tv.network.CookieManager
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Random
import java.util.concurrent.atomic.AtomicLong

class BiliHeaderInterceptor : Interceptor {
    private val random = Random()

    companion object {
        // 全局请求间隔控制，强制所有请求之间至少间隔 500ms
        private val lastRequestTime = AtomicLong(0)
        private const val MIN_REQUEST_INTERVAL = 500L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // 强制节流
        synchronized(lastRequestTime) {
            val now = System.currentTimeMillis()
            val timeSinceLast = now - lastRequestTime.get()
            if (timeSinceLast < MIN_REQUEST_INTERVAL) {
                Thread.sleep(MIN_REQUEST_INTERVAL - timeSinceLast)
            }
            lastRequestTime.set(System.currentTimeMillis())
        }

        // 额外的随机延迟
        Thread.sleep(100L + random.nextInt(200))

        val originalRequest = chain.request()
        val url = originalRequest.url
        val urlString = url.toString()
        
        val buvid3 = "FE49B76A-A98B-4B3F-BD4E-7C1C2D9A5F1Einfoc" 
        
        val requestBuilder = originalRequest.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
            .header("Accept", "application/json, text/plain, */*")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            .header("Origin", "https://www.bilibili.com")
            .header("Sec-Fetch-Dest", "empty")
            .header("Sec-Fetch-Mode", "cors")
            .header("Sec-Fetch-Site", "same-site")

        if (urlString.contains("space/arc/search")) {
            val mid = url.queryParameter("mid") ?: ""
            requestBuilder.header("Referer", "https://space.bilibili.com/$mid/video")
        } else {
            requestBuilder.header("Referer", "https://www.bilibili.com/")
        }

        val savedCookies = CookieManager.getCookies()
        val cookieMap = mutableMapOf<String, String>()
        cookieMap["buvid3"] = buvid3
        
        savedCookies?.forEach { cookie ->
            val pair = cookie.substringBefore(";").split("=", limit = 2)
            if (pair.size == 2) {
                val key = pair[0].trim()
                val value = pair[1].trim()
                if (key.isNotBlank()) {
                    cookieMap[key] = value
                }
            }
        }
        
        val cookieString = cookieMap.entries.joinToString("; ") { "${it.key}=${it.value}" }
        requestBuilder.header("Cookie", cookieString)
            
        return chain.proceed(requestBuilder.build())
    }
}
