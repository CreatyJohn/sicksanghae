package com.expiry.template.kotlin.src.main.refrige

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityInforefrigeBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.KEY.EATS_IDX
import com.expiry.template.kotlin.util.KEY.PAGE_NUM_EATS
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import com.expiry.template.kotlin.util.KEY.REFRIGE_NAME
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.refrige.EatsRVAdapter
import com.expiry.template.kotlin.src.viewModel.main.refrige.EatsRVData
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.OWNER
import com.expiry.template.kotlin.util.KEY.USER_ID
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@SuppressLint("CheckResult", "NotifyDataSetChanged")
class InfoRefrigeActivity : BaseActivity<ActivityInforefrigeBinding>(ActivityInforefrigeBinding::inflate) {

    private var datas = mutableListOf<EatsRVData>()
    private lateinit var eatsRVAdapter: EatsRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refrigeName: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        prefs = PreferenceUtil(applicationContext)

        binding.appbarlayout.bringToFront()

        val refrigeIdx = prefs.getInt(REFRIGE_IDX, -1)
        val isOwner = prefs.getInt(OWNER, -1)
        val myId = prefs.getInt(USER_ID, -1)
        refrigeName = prefs.getString(REFRIGE_NAME, "null")

        recyclerView = binding.rvEats
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        when(refrigeName.isEmpty()){
            true -> {
                binding.etRefrigName.text = ""
            }
            else -> {
                binding.etRefrigName.text = refrigeName
            }
        }

        when(isOwner) {
            0 -> {
                // is not owner
                binding.btnEdit.hide()
                binding.btnDelete.setOnClickListener {
                    val confirmTask = Runnable {
                        showLoadingDialog(this)
                        groupOutMySelf(refrigeIdx, myId)
                        dismissLoadingDialog()
                        finish()
                    }
                    showAlertDialog(2, "냉장고 그룹에서 나가시겠습니까?\n이 냉장고를 볼 수 없습니다", "나가기", confirmTask, null)
                }
            }
            1 -> {
                // is owned
                binding.btnEdit.show()
                binding.btnDelete.setOnClickListener {
                    val confirmTask = Runnable {
                        showLoadingDialog(this)
                        deleteRefrige(refrigeIdx)
                        dismissLoadingDialog()
                        finish()
                    }
                    showAlertDialog(2, "이 냉장고를 삭제하시겠습니까?\n모든 식품이 사라집니다", "삭제", confirmTask, null)
                }
            }
            else -> {
                // TODO
                /*** 둘다 안왔을 때 */
            }
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }

        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, EditRefrigeActivity::class.java))
        }

        binding.btnAddEats.setOnClickListener {
            prefs.setInt(PAGE_NUM_EATS, 0)
            startActivity(Intent(this, EditEatsActivity::class.java))
        }

        binding.btnAddContents.setOnClickListener {
            prefs.setInt(PAGE_NUM_EATS, 0)
            startActivity(Intent(this, EditEatsActivity::class.java))
        }
    }

    /** 식품 삭제 */
    private fun deleteRefrige(v1: Int) {
        showLoadingDialog(this)

        /** API 파싱단계 */
        RetrofitClient.apiService.deleteRefrige(v1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "InfoRefrigeActivity| deleteRefrige: ${it.message}")
                showCustomToast("냉장고 삭제 성공")
                dismissLoadingDialog()
                newActivity()
            }, { error ->
                showCustomToast("냉장고 삭제 실패")
                dismissLoadingDialog()
                errorPage(error, this)
            })
    }

    /** 냉장고 나가기 */
    private fun groupOutMySelf (p1: Int, p2: Int) {
        showLoadingDialog(this)

        /** 그룹에서 자가추방 같은느낌
         * p1: 추방할 냉장고 ID / p2: 내 유저 ID
         * */
        RetrofitClient.apiService.exileGroup(p1, p2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "InfoRefrigeActivity| groupOutMySelf: ${it.message}")
                showCustomToast("냉장고 나가기 성공")
                dismissLoadingDialog()
                newActivity()
            }, { error ->
                showCustomToast("냉장고 나가기 실패")
                dismissLoadingDialog()
                errorPage(error, this)
            })
    }

    private fun newActivity() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    // 여까지

    /** 배열 추가 */
    private fun initRecycler() {
        eatsRVAdapter = EatsRVAdapter(this) // 배열에 어댑터 추가
        recyclerView.adapter = eatsRVAdapter

        // 데이터를 어댑터에 설정
        eatsRVAdapter.datas.clear()
        eatsRVAdapter.datas.addAll(datas)
        eatsRVAdapter.notifyDataSetChanged()

        /** 리사이클러뷰 onItem 클릭 이벤트리스너 */
        eatsRVAdapter.setOnItemClickListener(object : EatsRVAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: EatsRVData, pos: Int) {
                Intent(this@InfoRefrigeActivity, InfoEatsActivity::class.java).apply{
                    prefs.setInt(EATS_IDX, data.productidx)
                }.run {
                    startActivity(this)
                }
            }
        })
    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */

    private fun addTask(img: String, productsIDX: Int, title: String, date: String, description: String) {
        val result = EatsRVData(img, productsIDX, title, date, description)
        datas.add(result)
    }

    private fun rvFoodsList(refrigeIDX: Int) {
        showLoadingDialog(this@InfoRefrigeActivity)
        /** API 파싱단계 */
        RetrofitClient.apiService.getFoodsList(refrigeIDX)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "InfoRefrigeActivity| rvFoodsList: ${it.message}")

                binding.ivNomoreTv.show()
                binding.ivNomoreEmotion.show()
                binding.btnAddEats.show()
                binding.rvEats.show()
                binding.btnAddContents.show()

                val listSize = it.result.size

                if(listSize >= 1){

                    binding.ivNomoreEmotion.hide()
                    binding.ivNomoreTv.hide()
                    binding.btnAddEats.hide()
                    binding.rvEats.show()
                    binding.btnAddContents.show()

                    /** 식품 리스트 리사이클러뷰 생성 */
                    for(i in 0 until listSize){
                        addTask(
                            it.result[i].productImg,
                            it.result[i].productId,
                            it.result[i].productName,
                            it.result[i].date,
                            it.result[i].description
                        )
                    }
                } else {
                    binding.ivNomoreEmotion.show()
                    binding.ivNomoreTv.show()
                    binding.btnAddEats.show()
                    binding.rvEats.hide()
                    binding.btnAddContents.hide()
                }
                initRecycler()
                dismissLoadingDialog()
            }, {
                Log.e(TAG, it.message.toString())
                initRecycler()
                dismissLoadingDialog()
            })
    }

    override fun onResume() {
        super.onResume()

        datas.clear()
        rvFoodsList(prefs.getInt(REFRIGE_IDX, -1))
    }
}