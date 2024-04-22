package com.expiry.template.kotlin.src.login

import android.annotation.SuppressLint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

import com.expiry.template.kotlin.src.reqres.RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityNicknameBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.LOGININFO.NICKNAME
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG

@SuppressLint("CheckResult", "SetTextI18n")
class NicknameActivity : BaseActivity<ActivityNicknameBinding>(ActivityNicknameBinding::inflate) {

    private lateinit var jwtData: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        prefs = PreferenceUtil(applicationContext)

        jwtData = prefs.getString(JWT, NULL)
        prefs.getInt(NICKNAME, -1)
//        binding.btnGoBack.hide()
        binding.etNickname.hint = "닉네임을 적어주세요!"
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnEnter.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isTextEmpty = s.isNullOrEmpty()

                if (!isTextEmpty) {
                    // 텍스트가 비어 있지 않으면 버튼 활성화
                    binding.btnEnter.setBackgroundResource(R.drawable.radius4_background)
                    binding.btnEnter.isEnabled = true

                    binding.btnEnter.setOnClickListener {
                        // 여기에 버튼 클릭 시 수행할 작업을 추가합니다.
                        RetrofitClient.apiService
                            .patchNickname(jwtData, binding.etNickname.text.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d(TAG, "NicknameActivity| onCreate-setNickname: ${it.message}")
                                showCustomToast("닉네임 설정 완료")
                                startMainActivity()
                            }, { error ->
                                showCustomToast("닉네임 설정 실패")
                                errorPage(error, this@NicknameActivity)
                            })
                    }
                } else {
                    // 텍스트가 비어 있으면 버튼 비활성화
                    binding.btnEnter.setBackgroundResource(R.drawable.radius4_background_stop)
                    binding.btnEnter.isEnabled = false
                    showCustomToast("닉네임을 적어주세요")
                    binding.btnEnter.setOnClickListener { showCustomToast("닉네임을 적어주세요") }
                }
            }
        })

//        binding.btnGoBack.setOnClickListener { finish() }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}