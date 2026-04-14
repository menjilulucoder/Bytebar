package com.example.bytebar.model

/**
 * 登录数据模型接口
 * 负责处理登录相关的数据操作
 */
interface LoginModel {
    /**
     * 保存登录信息
     */
    fun saveLoginInfo(username: String, password: String, rememberMe: Boolean)

    /**
     * 获取登录信息
     */
    fun getLoginInfo(): Pair<String, String>

    /**
     * 检查是否记住密码
     */
    fun isRememberMe(): Boolean
}