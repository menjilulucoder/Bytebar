package com.example.bytebar.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bytebar.ui.fragments.NotesFragment
import com.example.bytebar.ui.fragments.PlansFragment
import com.example.bytebar.ui.fragments.UserFragment
import com.example.bytebar.ui.fragments.WeatherFragment

/**
 * ViewPager2的适配器，管理四个主要页面的Fragment
 */
class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> WeatherFragment()
            1 -> NotesFragment()
            2 -> PlansFragment()
            3 -> UserFragment()
            else -> WeatherFragment()
        }
        return fragment
    }
}