package com.example.bytebar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bytebar.databinding.ActivityLoginBinding
import com.example.bytebar.model.LoginModel
import com.example.bytebar.model.LoginModelImpl
import com.example.bytebar.ui.login.LoginContract
import com.example.bytebar.ui.login.LoginPresenter
import com.example.bytebar.data.UserDao
class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化Model
        val loginModel: LoginModel = LoginModelImpl(this)
        // 初始化UserDao
        val userDao = UserDao(this)
        
        // 初始化Presenter
        presenter = LoginPresenter(this, loginModel)
        
        // 加载保存的登录信息
        presenter.loadSavedLoginInfo()

        // 设置登录按钮点击事件
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val rememberMe = binding.rememberMeCheckBox.isChecked
            
            // 1. 验证输入
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 2. 登录验证
            if (userDao.login(username, password)) {
                // 3. 保存登录信息并跳转到主页面
                presenter.login(username, password, rememberMe)
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
            }
        }

        // 设置注册按钮点击事件
        binding.registerButton.setOnClickListener {
            presenter.goToRegister()
        }
    }

    override fun showLoading() {
        // 显示加载状态
        binding.loadingProgress.visibility = android.view.View.VISIBLE
        binding.loginButton.isEnabled = false
    }

    override fun hideLoading() {
        // 隐藏加载状态
        binding.loadingProgress.visibility = android.view.View.GONE
        binding.loginButton.isEnabled = true
    }

    override fun showError(message: String) {
        // 显示错误信息
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun goToMain() {
        // 跳转到主页面
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun goToRegister() {
        // 跳转到注册页面
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    
    override fun fillLoginInfo(username: String, password: String, rememberMe: Boolean) {
        // 填充登录信息
        if (username.isNotEmpty()) {
            binding.usernameEditText.setText(username)
        }
        if (password.isNotEmpty() && rememberMe) {
            binding.passwordEditText.setText(password)
        }
        binding.rememberMeCheckBox.isChecked = rememberMe
    }
}