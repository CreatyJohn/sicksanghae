package com.expiry.template.kotlin.src.main.myPage.friends

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityFriendsBinding
import com.expiry.template.kotlin.util.PreferenceUtil

@SuppressLint("CommitTransaction, ObsoleteSdkInt")
class FriendsActivity : BaseActivity<ActivityFriendsBinding>(ActivityFriendsBinding::inflate) {

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceUtil(applicationContext)

        // 이후에 팔로잉 팔로워가 만들어지면 그때 사용
//        when (prefs.getInt(FOLLOWINTRO, -1)) {
//
//            1 -> {
//                /** 팔로워 버튼을 눌렀을 때 */
//                val followerFragment = FollowerFragment()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fl_fragment_place, followerFragment)
//                    .commit()
//            }
//
//            0 -> {
//                /** 팔로잉 버튼을 눌렀을 때 */
//                val followingFragment = FollowingFragment()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fl_fragment_place, followingFragment)
//                    .commit()
//            }
//
//            else -> {
//                Log.e(TAG, "FollowActivity : ERROR")
//            }
//        }
    }

    override fun onResume() {
        super.onResume()

        // StatusBar의 글자 색상 변경 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = this.window.insetsController
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = this.window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    override fun onPause() {
        super.onPause()

        // StatusBar를 원래 상태로 복원
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = this.window.insetsController
            controller?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = this.window
            window.decorView.systemUiVisibility = 0 // 기본값으로 변경
        }
    }
}