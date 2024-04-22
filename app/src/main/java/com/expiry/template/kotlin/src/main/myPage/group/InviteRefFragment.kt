package com.expiry.template.kotlin.src.main.myPage.group

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment
import com.expiry.template.kotlin.databinding.FragmentInviteRefBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.src.viewModel.main.mypage.group.InviteRefRVAdapter
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.mypage.group.UserModel
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.CLICKREFIDX
import com.expiry.template.kotlin.util.KEY.USER_ID
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("NotifyDataSetChanged, CheckResult, ResourceAsColor, StaticFieldLeak")
class InviteRefFragment : BaseFragment<FragmentInviteRefBinding>(FragmentInviteRefBinding::bind, R.layout.fragment_invite_ref) {

    private lateinit var inviteRefRVAdapter: InviteRefRVAdapter
    private lateinit var recyclerView: RecyclerView
    private val datas = mutableListOf<UserModel>()

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceUtil(requireContext())

        val myId = prefs.getInt(USER_ID, -1)
        val refrigeIdx = prefs.getInt(CLICKREFIDX, -1)

        recyclerView = binding.rvUsers
        recyclerView.layoutManager = GridLayoutManager(context, 1)

// 모르는 속성은 블로그 참조 : https://landroid.tistory.com/5
        binding.svSearchUser.isIconified = true

        // 검색 UI 커스터마이징
        val mSearchSrcTextView = binding.svSearchUser.findViewById<View>(R.id.search_src_text) as? SearchView.SearchAutoComplete
        mSearchSrcTextView?.apply{
            mSearchSrcTextView.setTextColor(Color.BLACK)
            mSearchSrcTextView.textSize = 16F
            mSearchSrcTextView.setHintTextColor(R.color.pastelGray)

            showLoadingDialog(requireContext())
            if (mSearchSrcTextView.text.isEmpty()) getSomeUser("", myId, refrigeIdx)
            dismissLoadingDialog()
        }

        binding.svSearchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showLoadingDialog(requireContext())
                // 검색 버튼 누를 때 호출
                if (!query.isNullOrEmpty()) {
                    getSomeUser(query, myId, refrigeIdx)
                    initRecycler()
                    dismissLoadingDialog()
                } else {
                    getSomeUser("", myId, refrigeIdx)
                    showCustomToast("전체 유저를 검색합니다")
                    initRecycler()
                    dismissLoadingDialog()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                if (!newText.isNullOrEmpty()) {
                    getSomeUser(newText, myId, refrigeIdx)
                    initRecycler()
                } else {
                    getSomeUser("", myId, refrigeIdx)
                    initRecycler()
                }

                return true
            }
        })
    }

    fun getSomeUser(name: String, myId: Int, refriId: Int) {
        datas.clear()
        // 유저 정보 받기
        RetrofitClient.apiService.getSomeUsers(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                // 받아온 데이터를 반복문을 통해 RecyclerView에 추가
                val count = res.result.size
                if (res.result.isNotEmpty()) {
                    for (i in 0 until count) {
                        val userIdx = res.result[i].userId
                        runBlocking {
                            launch(Dispatchers.IO) {
                                isGrouped(refriId, userIdx) { it ->
                                    if (myId != userIdx && it == 1) {
                                        addTask(
                                            userIdx,
                                            res.result[i].name,
                                            res.result[i].email,
                                            res.result[i].profileImg
                                        )
                                    }
                                    initRecycler()
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "InviteRefFragment| getSomeUser: ${res.message}")
            }, { error ->
                errorPage(error, requireContext())
            })
    }

    private fun isGrouped(refriId: Int, userIdx: Int, callback: (Int) -> Unit) {
        // 유저 정보 받기
        RetrofitClient.apiService.getGroupNotInUsers(refriId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                val count = res.result.size
                if (res.result.isNotEmpty()) {
                    for (i in 0 until count) {
                        if (userIdx == res.result[i].userId) {
                            Log.i(TAG, "isGrouped: $i")
                            callback(1) // User is Not grouped
                            return@subscribe
                        }
                    }
                }
                callback(0)
                return@subscribe
            }, { error ->
                // 다른 에러 발생 시 처리
                Log.e(TAG, "getSomeUser: ${error.message}")
                errorPage(error, requireContext())
                callback(0)
                return@subscribe
            })
    }

    private fun initRecycler() {
        inviteRefRVAdapter = InviteRefRVAdapter(requireContext(), this)
        recyclerView.adapter = inviteRefRVAdapter

        inviteRefRVAdapter.datas.clear()
        inviteRefRVAdapter.datas.addAll(datas)
        inviteRefRVAdapter.notifyDataSetChanged()
    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */
    private fun addTask(userIDX: Int, name: String, email: String, profileImg: String) {
        val newData = UserModel(userIDX, name, email, profileImg)
        // 데이터 추가 로직을 수행
        datas.add(newData)
    }

    // 냉장고 그룹 초대용

    fun inviteGroup (userIdx: Int) {
        val refIdx = prefs.getInt(CLICKREFIDX, -1)
        /** API 파싱단계
         * p1 -> refIdx(선택한 refrige ID)
         * p2 -> userIdx(선택한 user ID) */
        RetrofitClient.apiService.postInviteGroup(refIdx, userIdx)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response->

                Log.d(TAG, "InviteRefFragment| inviteGroup-postInviteGroup: ${response.message}")

                startMain()

                showCustomToast("냉장고 초대 성공")

            }, { error ->
                startMain()
                showCustomToast("냉장고 초대 실패")
                errorPage(error,requireContext())
            })
    }

    private fun startMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}