package com.expiry.template.kotlin.src.viewModel.main.mypage.group

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.expiry.template.kotlin.src.main.myPage.group.InviteRefFragment
import com.expiry.template.kotlin.src.main.myPage.group.KickRefFragment

class InviteViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InviteRefFragment()
            else -> KickRefFragment()
        }
    }
}