package com.expiry.template.kotlin.src.main.myPage.group

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityInviteRefrigeBinding
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.mypage.group.InviteModel
import com.expiry.template.kotlin.src.viewModel.main.mypage.group.InviteRefrigeRVAdapter
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.CLICKREFIDX
import com.expiry.template.kotlin.util.KEY.REFOWNER
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@SuppressLint("NotifyDataSetChanged, CheckResult")
class InviteRefrigeActivity : BaseActivity<ActivityInviteRefrigeBinding>(ActivityInviteRefrigeBinding::inflate) {

    private lateinit var inviteRefrigeRVAdapter: InviteRefrigeRVAdapter
    private lateinit var recyclerView: RecyclerView
    private val datas = mutableListOf<InviteModel>()

    companion object {
        lateinit var prefs: PreferenceUtil

        private var instance: InviteRefrigeActivity? = null
        fun getInstance(): InviteRefrigeActivity? {
            return instance
        }
    }

    init {
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        prefs = PreferenceUtil(applicationContext)

        val jwtData = prefs.getString(JWT, NULL)

        recyclerView = binding.rvRefrisInvite
        recyclerView.bringToFront()
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        getRefrisGroupList(jwtData)
    }

    private fun initRecycler() {
        inviteRefrigeRVAdapter = InviteRefrigeRVAdapter(this)
        recyclerView.adapter = inviteRefrigeRVAdapter

        inviteRefrigeRVAdapter.datas.clear()
        inviteRefrigeRVAdapter.datas.addAll(datas)
        inviteRefrigeRVAdapter.notifyDataSetChanged()
    }

    fun selectRefrige (refrigeIdx: Int, isOwner: Int) {
        prefs.setInt(CLICKREFIDX, refrigeIdx)
        when (isOwner){
            0 -> {
                val intent = Intent(this, UserSearchNoTabActivity::class.java)
                startActivity(intent)
            }
            1 -> {
                val intent = Intent(this, UserSearchActivity::class.java)
                startActivity(intent)
            }
            else -> {
                showCustomToast("해당 냉장고로 초대가 불가능합니다")
            }
        }
    }

    private fun getRefrisGroupList(jwt: String) {
        /** API 파싱단계 */
        RetrofitClient.apiService.getRefrisList(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response->
                Log.d(TAG, "InviteRefrigeActivity| getRefrisGroupList-getRefrisList: ${response.message}")
                // 받아온 데이터를 반복문을 통해 RecyclerView에 추가
                val count = response.result.size
                if(response.result.isNotEmpty()){
                    for(i in 0 until count) {
                        addTask(
                            response.result[i].ownerFg,
                            response.result[i].refrigeratorId,
                            response.result[i].refrigeratorName
                        )
                    }
                    initRecycler()
                }
            }, { error ->
                errorPage(error, this)
            })
    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */
    private fun addTask(isOwner: Int, productsIDX: Int, title: String) {
        val newData = InviteModel(isOwner, productsIDX, title)
        // 데이터 추가 로직을 수행
        datas.add(newData)
    }
}
