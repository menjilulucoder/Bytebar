package com.example.bytebar.ui.login

import com.example.bytebar.model.LoginModel

/**
 * 登录模块的Presenter，实现业务逻辑
 */
class LoginPresenter(private val view: LoginContract.View, private val loginModel: LoginModel) : LoginContract.Presenter {

    override fun login(username: String, password: String, rememberMe: Boolean) {
        // 显示加载状态
        view.showLoading()

        // 保存登录信息
        loginModel.saveLoginInfo(username, password, rememberMe)

        // 登录成功，跳转到主页面
        view.goToMain()
        view.hideLoading()
    }
    
    override fun loadSavedLoginInfo() {
        // 从Model获取保存的登录信息
        val (username, password) = loginModel.getLoginInfo()
        val rememberMe = loginModel.isRememberMe()
        
        // 填充到View
        view.fillLoginInfo(username, password, rememberMe)
    }

    override fun goToRegister() {
        view.goToRegister()
    }

    override fun destroy() {
        // 清理资源
    }
}