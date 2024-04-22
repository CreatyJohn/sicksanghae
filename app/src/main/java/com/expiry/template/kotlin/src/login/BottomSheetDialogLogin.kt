package com.expiry.template.kotlin.src.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.src.reqres.Token
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.KEY.DEVICETOKEN
import com.expiry.template.kotlin.util.KEY.SOCIAL
import com.expiry.template.kotlin.util.KEY.USER_ID
import com.expiry.template.kotlin.util.LOGININFO.KAKAOKEY
import com.expiry.template.kotlin.util.LOGININFO.NAVERKEY
import com.expiry.template.kotlin.util.LOGININFO.NAVERTOKEN
import com.expiry.template.kotlin.util.LOGININFO.NICKNAME
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@SuppressLint("CheckResult")
class BottomSheetDialogLogin : BottomSheetDialogFragment() {

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    /** 로그인 정보 */
    private var email: String = ""
    private var gender: String = ""
    private var name: String = ""
    private var image: String = ""
    private var phone: String = ""
    private var deviceToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottomsheetlogin, container, false)

        prefs = PreferenceUtil(requireContext())

        view?.findViewById<Button>(R.id.btn_kakao)?.setOnClickListener {
            kakaologin()
        }

        view?.findViewById<Button>(R.id.btn_naver)?.setOnClickListener {
            naverLogin()
        }

        // Kakao SDK 초기화 (KEY)
        KakaoSdk.init(requireContext(), "eee8b1ff00e9f8c2ce8018b29dbb9936")

        return view
    }

    private fun kakaologin() {
        /** 카카오 로그인 */

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.d(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                val tokenvalues = Token(
                    "",
                    prefs.getString(DEVICETOKEN, NULL),
                    token.accessToken
                )
                postKakaoLogin(tokenvalues)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = callback
                    )
                } else if (token != null) {
                    Log.d(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    val tokenValues = Token(
                        "",
                        prefs.getString(DEVICETOKEN, NULL),
                        token.accessToken
                    )
                    postKakaoLogin(tokenValues)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

    private fun naverLogin() {
        try {
            val oAuthLoginCallback = object : OAuthLoginCallback {
                // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object :
                        NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            name = result.profile?.name.toString()
                            email = result.profile?.email.toString()
                            gender = result.profile?.gender.toString()
                            image = result.profile?.profileImage.toString()
                            phone = result.profile?.mobile.toString()
                            deviceToken = prefs.getString(DEVICETOKEN, NULL)
                            val naverAccessToken = NaverIdLoginSDK.getAccessToken()

                            Log.d(TAG, "로그인 성공")
                            Log.d(TAG, "=================================")
                            Log.d(TAG, "네이버 로그인한 유저 정보 - 이름 : $name")
                            Log.d(TAG, "네이버 로그인한 유저 정보 - 이메일 : $email")
//                            Log.d(TAG, "네이버 로그인한 유저 정보 - 프로필사진 : $image")
//                            Log.d(TAG, "네이버 로그인한 유저 정보 - 전화번호 : $phone")
                            Log.d(TAG, "네이버 로그인한 유저 정보 - 액세스토큰 : $naverAccessToken")
                            Log.d(TAG, "네이버 로그인한 유저 정보 - 디바이스토큰 : $deviceToken")

                            prefs.setString(NAVERTOKEN, "$naverAccessToken")

                            val token = Token(
                                NAVERKEY,
                                deviceToken,
                                naverAccessToken.toString()
                            )

                            /** API 파싱단계 */
                            RetrofitClient.apiService
                                .postSocialLogin(token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ it ->
                                    try {
                                        // jwt 받고 로컬로 옮기기
                                        prefs.setString(JWT, it.result.jwt)
                                        // user_id 받기
                                        prefs.setInt(USER_ID, it.result.userId)

                                        Log.d(TAG, "naverlogin: ${it.result.loginInfo}")

                                        when (it.result.loginInfo) {
                                            "1" -> {
                                                Log.d(TAG, "Users LoginInformation: ${it.result.loginInfo}")
                                                Log.d(TAG, "Users Id: ${it.result.userId}")
                                                Log.d(TAG, "Users JWT: ${it.result.jwt}")
                                                startMainActivity()
                                            }
                                            "0" -> {
                                                startNicknameActivity()
                                            }
                                            else -> {
                                                Log.e(TAG, "response ERROR in Alghorithm " + it.result.loginInfo)
                                            }
                                        }

                                    } catch(e: Exception){
                                        Log.e(TAG, "response ERROR in KAKAO " + it.result.loginInfo)
                                    }

                                    prefs.setString(SOCIAL, NAVERKEY)

                                }, { error ->
                                    Log.e(TAG, "${error.message}")
                                    showAlertDialog(1, "서버연결에 실패하였습니다\n잠시 후 다시 시도해주세요", "닫기", null, null)
                                    prefs.setString(SOCIAL, NULL)
                                })
                        }

                        override fun onError(errorCode: Int, message: String) {
                            Log.e(TAG, "로그인 에러")
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            Log.e(TAG, "로그인 실패")
                        }
                    })
                }

                override fun onError(errorCode: Int, message: String) {
                    val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                    Log.e(TAG, "naverAccessToken : $naverAccessToken")
                    Log.e(TAG, "로그인 에러")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Log.e(TAG, "로그인 실패")
                }
            }

            NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
        } catch (e: Exception) {
            Log.e(TAG, "naverLogin: ${e.message}")
        }
    }

    private fun startNicknameActivity() {
        activity?.let {
            prefs.setInt(NICKNAME, 0)
            val intent = Intent(context, NicknameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    private fun startMainActivity() {
        activity?.let {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    private fun postKakaoLogin (token: Token) {

        /** API 파싱단계 */
        RetrofitClient.apiService
            .postSocialLogin(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                try {
                    // jwt 받고 로컬로 옮기기
                    prefs.setString(JWT, it.result.jwt)
                    // user_id 받기
                    prefs.setInt(USER_ID, it.result.userId)

                    Log.d(TAG, "postKakaoLogin: ${it.result.loginInfo}")

                    when (it.result.loginInfo) {
                        "1" -> {
                            Log.d(TAG, "Users LoginInformation: ${it.result.loginInfo}")
                            Log.d(TAG, "Users Id: ${it.result.userId}")
                            Log.d(TAG, "Users JWT: ${it.result.jwt}")

                            startMainActivity()
                        }
                        "0" -> {
                            startNicknameActivity()
                        }
                        else -> {
                            Log.e(TAG, "response ERROR in Alghorithm " + it.result.loginInfo)
                        }
                    }

                } catch(e: Exception){
                    Log.e(TAG, "response ERROR in KAKAO " + it.result.loginInfo)
                }

                prefs.setString(SOCIAL, KAKAOKEY)

            }, { error ->
                Log.e(TAG, error.message.toString())
                showAlertDialog(1, "서버연결에 실패하였습니다\n잠시 후 다시 시도해주세요", "닫기", null, null)

                prefs.setString(SOCIAL, NULL)
            })
    }

    /** 팝업창으로 대체 (완료)
     * https://todaycode.tistory.com/184  */
    // 파라미터 정보입니다 (int 사용할 버튼 갯수 (한개 사용 1 /두개 사용 2), 알림에 들어갈 내용, 닫기 이외에 사용될 버튼이 있으면 사용할 버튼의 텍스트 (확인, 허용 등),
    // 한개 버튼 사용 시 콜백될 함수 1, 두개 버튼 사용 시 콜백될 함수 2)
    // 함수 사용할 꺼면 넣고 안 할꺼면 null 처리 가능 // 예외처리 해두었음.
    private fun showAlertDialog(
        btnNum: Int,
        alertText: String,
        btnText: String,
        customRun: Runnable?,
        cancelRun: Runnable?
    ) {

        // XML 레이아웃 파일을 로드
        val inflater = layoutInflater
        val customView: View = inflater.inflate(R.layout.fragment_alert_dialog, null)

        // 레이아웃의 사용할 View들을 세팅
        val information = customView.findViewById<TextView>(R.id.tv_information)
        val customButton = customView.findViewById<Button>(R.id.btn_custom)
        val closeButton = customView.findViewById<Button>(R.id.btn_close)

        // AlertDialog에 레이아웃 설정
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialog)
        alertDialogBuilder.setView(customView)

        // 알람 내용과 버튼 종류 설정
        information.text = alertText
        customButton.text = btnText

        try {
            // AlertDialog 표시
            val alertDialog = alertDialogBuilder.create()

            // 배경 터치 막기
            alertDialog.setCancelable(false)

            // 팝업창 활성
            alertDialog.show()

            when (btnNum) {
                1 -> {
                    closeButton.hide()
                    customButton.show()
                    customButton.setOnClickListener {
                        if (customRun != null) {
                            customRun.run()
                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    }
                }

                2 -> {
                    closeButton.show()
                    customButton.show()
                    closeButton.setOnClickListener {
                        if (cancelRun != null) {
                            cancelRun.run()
                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    }
                    customButton.setOnClickListener {
                        if (customRun != null) {
                            customRun.run()
                            alertDialog.dismiss()
                        }
                    }
                }

                else -> {
                    closeButton.hide()
                    customButton.hide()
                    Log.e(TAG, "showAlertDialog: Wrong Derection! Plz try Again")
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.remove(this)
                        ?.commit()
                }
            }
        } catch (e: Exception) {
            // 에러 발생
            Log.e(TAG, "showAlertDialog catchERROR : ", e)
        }
    }
    private fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }
}
