package com.example.bytebar.model

import android.content.Context
import android.content.SharedPreferences

/**
 * 登录数据模型实现
 * 使用SharedPreferences存储登录信息
 */
class LoginModelImpl(context: Context) : LoginModel {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_REMEMBER_ME = "remember_me"
    }

    override fun saveLoginInfo(username: String, password: String, rememberMe: Boolean) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
        }
            .apply()
    }

    override fun getLoginInfo(): Pair<String, String> {
        val username = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        val password = sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
        return Pair(username, password)
    }

    override fun isRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }
}