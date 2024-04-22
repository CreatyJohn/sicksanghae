package com.expiry.template.kotlin.src.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityMainBinding
import com.expiry.template.kotlin.src.main.home.HomeFragment
import com.expiry.template.kotlin.src.main.myPage.MyPageFragment
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.TAG

@SuppressLint("ObsoleteSdkInt, InlinedApi, WrongConstant")
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    // Back Press 막기
    private val timeFinished: Long = 1000
    private var presstime: Long = 0

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
            showCustomToast("알림 권한을 허용해주세요")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clMainActivity.setPadding(
            0, 0, 0, this.navigationHeight()
        )

        /** DynamicLink 수신확인 */
        initDynamicLink()

        askNotificationPermission()

        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()

        binding.mainBtmNav.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_main_btm_nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, HomeFragment())
                            .commitAllowingStateLoss()

                        return@setOnItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_profile -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, MyPageFragment())
                            .commitAllowingStateLoss()

                        return@setOnItemSelectedListener true
                    }
                    else -> return@setOnItemSelectedListener false
                }
            }
            selectedItemId = R.id.menu_main_btm_nav_home
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    /** DynamicLink */
    private fun initDynamicLink() {
        val dynamicLinkData = intent.extras
        if (dynamicLinkData != null) {
            var dataStr = "DynamicLink 수신받은 값\n"
            for (key in dynamicLinkData.keySet()) {
                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
            }
            Log.d(TAG, "MainActivity| initDynamicLink-FCMToken: $dataStr")
        }
    }

    fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        // 오래오 이후, 즉 티라미슈 부터는 아래 코드로 채널 변경이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // 네비게이션 프래그먼트 변경하기
    fun switchBottomNavigationView(itemId: Int) {
        binding.mainBtmNav.selectedItemId = itemId
    }

    override fun onNewIntent(intent: Intent?) {
        Log.e("YMC", "MainActivity onNewIntent")
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - presstime
        if (intervalTime in 0..timeFinished) {
            finish()
        } else {
            presstime = tempTime
            showCustomToast("한번 더 누르시면 앱이 종료됩니다")
        }
    }
}