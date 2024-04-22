package com.expiry.template.kotlin.src.main.myPage.group

import android.annotation.SuppressLint
import android.os.Bundle
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityUserSearchBinding
import com.expiry.template.kotlin.src.viewModel.main.mypage.group.InviteViewPagerAdapter
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.google.android.material.tabs.TabLayoutMediator

class UserSearchActivity : BaseActivity<ActivityUserSearchBinding>(ActivityUserSearchBinding::inflate) {

    private val tabTextList = listOf("초대하기", "추방하기")
//    private val tabIconList = listOf(R.drawable.ic_1, R.drawable.ic_2)

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        binding.vpViewPager.adapter = InviteViewPagerAdapter(this)

        TabLayoutMediator(binding.tbTabLayout, binding.vpViewPager) { tab, pos ->
            tab.text = tabTextList[pos]
//            tab.setIcon(tabIconList[pos])
        }.attach()
    }
}
