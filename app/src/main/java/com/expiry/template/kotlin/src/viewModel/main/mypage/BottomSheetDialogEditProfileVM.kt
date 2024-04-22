package com.expiry.template.kotlin.src.viewModel.main.mypage

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class BottomSheetDialogEditProfileVM: ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData = MutableLiveData<Boolean>()
    val myData: LiveData<Boolean> get() = _myData

    @SuppressLint("CheckResult")
    fun deleteUsers(jwt: String) {
        /** API 파싱단계 */
        RetrofitClient.apiService.deleteUsers(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                Log.d(TAG, "BottomSheetDialogEditProfileVM| deleteUsers-deleteUsers: ${res.message}")
                _myData.value = res.isSuccess
            }, { error ->
                // Exception Handling
                if (error is HttpException) {
                    if (error.code() == 404) {
                        // HTTP 404 에러 발생 시 처리
                        Log.e(TAG, "User not found")
                    } else {
                        // 다른 HTTP 에러 발생 시 처리
                        Log.e(TAG, "HTTP Error ${error.code()}")
                    }
                } else {
                    // 다른 에러 발생 시 처리
                    Log.e(TAG, "Error occurred: ${error.message}")
                }
                _myData.value = false
            })
    }
}