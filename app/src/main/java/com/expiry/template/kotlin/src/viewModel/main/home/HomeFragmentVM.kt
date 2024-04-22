package com.expiry.template.kotlin.src.viewModel.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.LoadingDialog
import com.expiry.template.kotlin.util.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException


@SuppressLint("CheckResult")
class HomeFragmentVM : ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData1 = MutableLiveData<String>()
    val myData1: LiveData<String> get() = _myData1

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData2 = MutableLiveData<String>()
    val myData2: LiveData<String> get() = _myData2

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData3 = MutableLiveData<String>()
//    val myData3: LiveData<String> get() = _myData3

    fun getUsers(jwt: String) {

        /** API 파싱단계 */
        RetrofitClient.apiService.getUsers(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response->

                Log.d(TAG, "HomeFragmentVM| getUsers: ${response.message}")

                // UI 업데이트를 위해 LiveData를 통해 데이터를 전달
                _myData1.value = response.result.name
                _myData2.value = response.result.profileImg
                
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
            })
    }
}
