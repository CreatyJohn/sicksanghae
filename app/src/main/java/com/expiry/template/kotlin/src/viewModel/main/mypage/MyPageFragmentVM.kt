package com.expiry.template.kotlin.src.viewModel.main.mypage

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jetbrains.annotations.Async.Schedule

@SuppressLint("CheckResult")
class MyPageFragmentVM : ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _myData0 = MutableLiveData<String>()
    val myData0: LiveData<String> get() = _myData0

    private val _myData1 = MutableLiveData<String>()
    val myData1: LiveData<String> get() = _myData1

    private val _myData2 = MutableLiveData<String>()
    val myData2: LiveData<String> get() = _myData2

    fun getUsers(jwt: String) {

        // 유저 정보 받기
        RetrofitClient.apiService
            .getUsers(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->

                _myData0.value = it.result.email
                _myData1.value = it.result.name
                _myData2.value = it.result.profileImg

                Log.d(TAG, "MyPageFragmentVM| getUsers-getUsers: ${it.message}")
            }, {
                // 다른 에러 발생 시 처리
                Log.e(TAG, it.message.toString())
            })
    }

    private val _isSuccessed = MutableLiveData<Boolean>()
    val isSuccessed: LiveData<Boolean> get() = _isSuccessed

    // 회원 로그아웃
    fun logoutUser(jwt: String) {
        RetrofitClient.apiService
            .logoutUsers(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "MyPageFragmentVM| logoutUser-logoutUsers: ${it.message}")
                if(it.isSuccess) _isSuccessed.postValue(it.isSuccess)
            }, {
                Log.e(TAG, it.message.toString())
            })
    }
}