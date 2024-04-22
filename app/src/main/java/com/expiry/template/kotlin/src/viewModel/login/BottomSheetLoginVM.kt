package com.expiry.template.kotlin.src.viewModel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomSheetLoginVM : ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData = MutableLiveData<String>()

    val myData: LiveData<String> get() = _myData

//    @SuppressLint("CheckResult")
//    val postKakaoLogin : (token: Token) -> Unit = {
//        /** API 파싱단계 */
//        RetrofitClient.apiService.postKakaoLogin(token)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ it ->
//                Log.d(TAG, "요청 성공")
//
//                Log.d(TAG, it.result.jwt)
//                Log.d(TAG, "${it.result.userId}")
//
//                // jwt 받고 로컬로 옮기기
//                prefs.setString(JWT, it.result.jwt)
//                // user_id 받기
//                prefs.setInt("USERID", it.result.userId)
//
//                Log.d(TAG, it.result.loginInfo.toString())
//
//                if(it.result.loginInfo == 1){
//                    startHomeFragment()
//                } else {
//                    startNicknameActivity()
//                }
//
//                try {
//                    Log.d(TAG, it.result.jwt)
//                    Log.d(TAG, "${it.result.userId}")
//
//                    // jwt 받고 로컬로 옮기기
//                    prefs.setString(JWT, it.result.jwt)
//                    // user_id 받기
//                    prefs.setInt("USERID", it.result.userId)
//
//                    Log.d(TAG, it.result.loginInfo.toString())
//
//                        if(it.result.loginInfo == 1){
//                            startHomeFragment()
//                        } else {
//                            startNicknameActivity()
//                        }
//
//                    } catch(e: Exception){ Log.e(TAG, "response ERROR in KAKAO") }
//
//            }, { error ->
//                Log.e(TAG, error.message.toString())
//                activity?.let { Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show() }
//            })
//    }
}