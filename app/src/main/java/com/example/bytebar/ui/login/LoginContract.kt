package com.example.bytebar.ui.login

/**
 * 登录模块的MVP契约接口
 */
interface LoginContract {

    /**
     * View接口，定义UI操作
     */
    interface View {
        /**
         * 显示加载状态
         */
        fun showLoading()

        /**
         * 隐藏加载状态
         */
        fun hideLoading()

        /**
         * 显示错误信息
         */
        fun showError(message: String)

        /**
         * 跳转到主页面
         */
        fun goToMain()

        /**
         * 跳转到注册页面
         */
        fun goToRegister()
        
        /**
         * 填充登录信息
         */
        fun fillLoginInfo(username: String, password: String, rememberMe: Boolean)
    }

    /**
     * Presenter接口，定义业务逻辑
     */
    interface Presenter {
        /**
         * 登录
         */
        fun login(username: String, password: String, rememberMe: Boolean)
        
        /**
         * 加载保存的登录信息
         */
        fun loadSavedLoginInfo()

        /**
         * 跳转到注册页面
         */
        fun goToRegister()

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
    interface Model {
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
}