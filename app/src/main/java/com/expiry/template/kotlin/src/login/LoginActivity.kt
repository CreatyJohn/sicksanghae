package com.expiry.template.kotlin.src.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivitySplashBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.KEY.DEVICETOKEN
import com.expiry.template.kotlin.util.KEY.KEYHASH
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility

//
class LoginActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "LoginActivity| getFirebaseToken-DeviceToken: $it")
            prefs.setString(DEVICETOKEN, it)
        }
    }

    // 개발 디바이스 별 해시 키 뽑는 거시기
    private fun hashkeyPrint() {
        val keyHash = Utility.getKeyHash(this)
        Log.d(TAG, "LoginActivity| hashkeyPrint-keyHash: $keyHash")
        prefs.setString(KEYHASH, keyHash)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceUtil(applicationContext)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())
        
        // 오류로 초기화할때 사용
//        prefs.destroyData()
        
        // 키 해시 뽑을때 사용
//        hashkeyPrint()

        // 파이어베이스 토큰 뽑을때 사용
        getFirebaseToken()

        // 유사 자동로그인 (저퀄 알고리즘)
        when(prefs.getString(JWT, NULL)){
            NULL -> {
                /** 로그인하기를 눌렀을 시, 로그인 바텀시트가 나오는 동작 */
                binding.btnLogin.setOnClickListener {
                    val bottomSheet = BottomSheetDialogLogin()
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            }
            else -> {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}