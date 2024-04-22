package com.expiry.template.kotlin.src.viewModel.main.refrige

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@SuppressLint("CheckResult")
class InfoEatsActivityVM : ViewModel() {

    fun deleteEatsInfo(v1: Int) {
        /** API 파싱단계 */
        RetrofitClient.apiService.deleteFoods(v1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "deleteEatsInfo: ${it.message}")
            }, { error ->
                Log.e(TAG, "deleteEatsInfo: ${error.message}")
            })
    }
}
