package com.example.bytebar.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bytebar.databinding.FragmentUserBinding
import com.example.bytebar.ui.user.UserContract
import com.example.bytebar.ui.user.UserPresenter

/**
 * 用户信息页面Fragment
 */
class UserFragment : Fragment(), UserContract.View {

    private lateinit var binding: FragmentUserBinding
    private lateinit var presenter: UserContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        // 初始化Presenter
        presenter = UserPresenter(this)
        // 加载用户信息
        presenter.loadUserInfo()
        // 设置修改按钮点击事件
        binding.editButton.setOnClickListener {
            presenter.editUserInfo()
        }
        // 设置主题切换按钮点击事件
        binding.themeButton.setOnClickListener {
            presenter.changeTheme()
        }
        return binding.root
    }

    override fun showUserInfo(username: String, avatar: String) {
        binding.usernameText.text = username
        // 设置头像
    }

    override fun showEditUserDialog() {
        // 显示编辑用户信息对话框
    }

    override fun showThemeDialog() {
        // 显示主题选择对话框
    }

    override fun showLoading() {
        binding.loadingProgress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingProgress.visibility = View.GONE
    }

    override fun showError(message: String) {
        // 显示错误信息
    }
}