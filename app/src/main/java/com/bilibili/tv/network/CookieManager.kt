package com.bilibili.tv.network

import android.content.Context
import android.content.SharedPreferences

object CookieManager {
    private const val PREF_NAME = "bili_cookies"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveCookies(cookies: Set<String>) {
        // 合并新旧 Cookie，确保 SESSDATA 等关键信息不被覆盖
        val current = getCookies()?.toMutableSet() ?: mutableSetOf()
        current.addAll(cookies)
        prefs?.edit()?.putStringSet("cookies", current)?.apply()
    }

    fun getCookies(): Set<String>? {
        return prefs?.getStringSet("cookies", null)
    }

    fun clearCookies() {
        prefs?.edit()?.remove("cookies")?.apply()
    }

    /**
     * 只有包含 SESSDATA 字段才认为登录成功
     */
    fun isLogin(): Boolean {
        val cookies = getCookies() ?: return false
        return cookies.any { it.contains("SESSDATA=", ignoreCase = true) }
    }
}
