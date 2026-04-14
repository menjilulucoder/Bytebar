package com.example.bytebar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.bytebar.databinding.ActivityMainBinding
import com.example.bytebar.ui.adapters.MainPagerAdapter
import com.example.bytebar.R

class MainActivity : AppCompatActivity() {

    // 使用ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ViewPager2
        setupViewPager()
    }

    /**
     * 设置ViewPager2和BottomNavigationView
     */
    private fun setupViewPager() {
        // 创建适配器
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // 设置ViewPager2与BottomNavigationView的联动
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_weather -> binding.viewPager.currentItem = 0
                R.id.nav_notes -> binding.viewPager.currentItem = 1
                R.id.nav_plans -> binding.viewPager.currentItem = 2
                R.id.nav_user -> binding.viewPager.currentItem = 3
            }
            true
        }

        // 设置ViewPager2页面变化监听
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNav.selectedItemId = when (position) {
                    0 -> R.id.nav_weather
                    1 -> R.id.nav_notes
                    2 -> R.id.nav_plans
                    3 -> R.id.nav_user
                    else -> R.id.nav_weather
                }
            }
        })
    }
}