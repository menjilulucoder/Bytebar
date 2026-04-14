package com.example.bytebar.ui.user

/**
 * 用户模块的Presenter，实现业务逻辑
 */
class UserPresenter(private val view: UserContract.View) : UserContract.Presenter {

    override fun loadUserInfo() {
        // 显示加载状态
        view.showLoading()

        // 模拟用户数据
        val username = "小明"
        val avatar = ""

        // 显示用户信息
        view.showUserInfo(username, avatar)

        // 隐藏加载状态
        view.hideLoading()
    }

    override fun editUserInfo() {
        view.showEditUserDialog()
    }

    override fun changeTheme() {
        view.showThemeDialog()
    }

    override fun destroy() {
        // 清理资源
    }
}