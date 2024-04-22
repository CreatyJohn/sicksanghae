package com.expiry.template.kotlin.src.viewModel.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomSheetDialogAddRefrisVM : ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _mydata = MutableLiveData<String>()
    val myData: LiveData<String> get() = _mydata


}
