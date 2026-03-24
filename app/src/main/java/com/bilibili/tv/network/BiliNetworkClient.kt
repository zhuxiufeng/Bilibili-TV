package com.bilibili.tv.network

import com.bilibili.tv.network.interceptor.BiliHeaderInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object BiliNetworkClient {
    private const val BASE_URL_API = "https://api.bilibili.com/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // For debug purposes
        }

        OkHttpClient.Builder()
            .addInterceptor(BiliHeaderInterceptor())
            .addInterceptor(logging)
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    if (url.toString().contains("qrcode/poll") || url.toString().contains("login")) {
                        val cookieStrings = cookies.map { it.toString() }.toSet()
                        if (cookieStrings.isNotEmpty()) {
                            CookieManager.saveCookies(cookieStrings)
                        }
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val savedCookies = CookieManager.getCookies()
                    return savedCookies?.mapNotNull { Cookie.parse(url, it) } ?: emptyList()
                }
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    val biliApiService: BiliApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BiliApiService::class.java)
    }
}
