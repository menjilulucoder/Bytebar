package com.example.bytebar.ui.user

/**
 * 用户模块的MVP契约接口
 */
interface UserContract {

    /**
     * 用户数据模型
     */
    data class User(
        val id: Int,
        val username: String,
        val password: String,
        val avatar: String
    )

    /**
     * View接口，定义UI操作
     */
    interface View {
        /**
         * 显示用户信息
         */
        fun showUserInfo(username: String, avatar: String)

        /**
         * 显示编辑用户信息对话框
         */
        fun showEditUserDialog()

        /**
         * 显示主题选择对话框
         */
        fun showThemeDialog()

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
    }

    /**
     * Presenter接口，定义业务逻辑
     */
    interface Presenter {
        /**
         * 加载用户信息
         */
        fun loadUserInfo()

        /**
         * 编辑用户信息
         */
        fun editUserInfo()

        /**
         * 更改主题
         */
        fun changeTheme()

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
}