package com.example.bytebar

import android.content.Context

class SharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_REMEMBER_ME = "remember_me"
    }

    fun saveloginInfo(username: String, password: String, rememberMe: Boolean) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
        }
        .apply()
    }

    fun getloginInfo(): Pair<String, String> {
        val username = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        val password = sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
        return Pair(username, password)
    }
    fun isRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }
    fun clearLoginInfo() {
        sharedPreferences.edit()
        .remove(KEY_USERNAME)
        .remove(KEY_PASSWORD)
        .remove(KEY_REMEMBER_ME)
        .apply()
    }




}
