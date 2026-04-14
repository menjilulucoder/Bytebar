package com.example.bytebar.ui.register

/**
 * 注册模块的Presenter，实现业务逻辑
 */
class RegisterPresenter(private val view: RegisterContract.View) : RegisterContract.Presenter {

    override fun register(username: String, password: String, confirmPassword: String) {
        // 显示加载状态
        view.showLoading()

        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("用户名和密码不能为空")
            view.hideLoading()
            return
        }

        if (password != confirmPassword) {
            view.showError("两次输入的密码不一致")
            view.hideLoading()
            return
        }

        view.goToLogin()
        view.hideLoading()
    }

    override fun goToLogin() {
        view.goToLogin()
    }

    override fun destroy() {
        // 清理资源
    }
}