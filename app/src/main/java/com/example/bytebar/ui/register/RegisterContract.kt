package com.example.bytebar.ui.register

/**
 * 注册模块的MVP契约接口
 */
interface RegisterContract {

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
         * 跳转到登录页面
         */
        fun goToLogin()
    }

    /**
     * Presenter接口，定义业务逻辑
     */
    interface Presenter {
        /**
         * 注册
         */
        fun register(username: String, password: String, confirmPassword: String)

        /**
         * 跳转到登录页面
         */
        fun goToLogin()

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
}