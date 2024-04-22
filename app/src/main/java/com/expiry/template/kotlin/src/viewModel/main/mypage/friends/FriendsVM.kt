package com.expiry.template.kotlin.src.viewModel.main.mypage.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.util.TAG

@SuppressLint("CheckResult")
class FriendsVM : ViewModel() {

    // 데이터를 관리하는 데 필요한 MutableLiveData 객체를 정의
    private val _mydata = MutableLiveData<String>()
    val myData: LiveData<String> get() = _mydata

    // RecyclerView에 표시될 데이터를 담을 MutableList
    private val datas = mutableListOf<FriendsModel>()

    fun getFollowerList() {

        // 데이터 초기화
        datas.clear()

        // 테스트 데이터

        addTask (
            0,
            R.drawable.honggildong,
            "hong_gil_haha",
            "홍길동",
        )

        addTask (
            1,
            R.drawable.shimchung,
            "shimshim_EEE_09",
            "심청이",
        )

        addTask (
            2,
            R.drawable.shinjjanggu,
            "crayon_shinJJang",
            "신짱구",
        )
    }

//    fun getFollowingList() {
//
//        // 데이터 초기화
//        datas.clear()
//
//        // 테스트 데이터
//        val img1 : String = R.drawable.honggildong.toString()
//        val img2 : String = R.drawable.shimchung.toString()
//        val img3 : String = R.drawable.shinjjanggu.toString()
//
//        addTask (
//            0,
//            img1,
//            "hong_gil_haha",
//            "홍길동",
//        )
//
//        addTask (
//            1,
//            img2,
//            "shimshim_EEE_09",
//            "심청이",
//        )
//
//        addTask (
//            2,
//            img3,
//            "crayon_shinJJang",
//            "신짱구",
//        )
//    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */
    private fun addTask(productsIDX: Int, img: Int, nickname: String, name: String) {
        val nData = FriendsModel(productsIDX, img, nickname, name)
        // 데이터 추가 로직을 수행
        datas.add(nData)
        // 리사이클러뷰 데이터 로그 찍기
        Log.d(TAG, "$datas 데이터 수정 완료")
    }

    fun getFriendsData(): List<FriendsModel> = datas
}