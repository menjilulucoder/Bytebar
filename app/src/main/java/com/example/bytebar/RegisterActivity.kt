package com.example.bytebar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebar.data.UserDao
import com.example.bytebar.databinding.ActivityRegisterBinding
import com.example.bytebar.ui.register.RegisterContract
import com.example.bytebar.ui.register.RegisterPresenter

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化Presenter
        presenter = RegisterPresenter(this)
        
        // 注册完成后在Activity中直接处理逻辑，不再调用presenter.register()
        // 初始化UserDao
        val userDao = UserDao(this)

        // 设置注册按钮点击事件
        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            
            // 1. 验证输入
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 2. 验证密码一致性
            if (password != confirmPassword) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 3. 检查用户是否存在
            if (userDao.isUserExist(username)) {
                Toast.makeText(this, "用户已存在", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 4. 注册用户
            if (userDao.register(username, password)) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                presenter.goToLogin() // 注册成功后跳转到登录页面
            } else {
                Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show()
            }
        }

        // 设置登录按钮点击事件
        binding.loginButton.setOnClickListener {
            presenter.goToLogin()
        }
    }

    override fun showLoading() {
        // 显示加载状态
        binding.loadingProgress.visibility = android.view.View.VISIBLE
        binding.registerButton.isEnabled = false
    }

    override fun hideLoading() {
        // 隐藏加载状态
        binding.loadingProgress.visibility = android.view.View.GONE
        binding.registerButton.isEnabled = true
    }

    override fun showError(message: String) {
        // 显示错误信息
        Toast.makeText(this, "Errors", Toast.LENGTH_SHORT).show()
    }

    override fun goToLogin() {
        // 跳转到登录页面
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}