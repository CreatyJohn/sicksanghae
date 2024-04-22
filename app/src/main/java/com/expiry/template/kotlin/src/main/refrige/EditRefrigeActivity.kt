package com.expiry.template.kotlin.src.main.refrige

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityEditrefrigeBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.KEY.PAGE_NUM_EATS
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import com.expiry.template.kotlin.util.KEY.REFRIGE_NAME
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.refrige.EatsRVAdapter
import com.expiry.template.kotlin.src.viewModel.main.refrige.EatsRVData
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.EATS_IDX

@SuppressLint("CheckResult, NotifyDataSetChanged")
class EditRefrigeActivity : BaseActivity<ActivityEditrefrigeBinding>(ActivityEditrefrigeBinding::inflate) {

    private var datas = mutableListOf<EatsRVData>()
    private lateinit var eatsRVAdapter: EatsRVAdapter
    private lateinit var recyclerView: RecyclerView

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        prefs = PreferenceUtil(applicationContext)

        showLoadingDialog(this@EditRefrigeActivity)

        binding.appbarlayout.bringToFront()

        binding.appbarlayout.bringToFront()

        val refrigeIDX = prefs.getInt(REFRIGE_IDX, -1)
        val refrigeName = prefs.getString(REFRIGE_NAME, "null")

        initRecycler()

        recyclerView = binding.rvEats
        recyclerView.layoutManager = GridLayoutManager(this@EditRefrigeActivity, 2)

        when(refrigeName.isNotEmpty()){
            true -> {
                binding.etRefrigName.setText(refrigeName)
            }
            else -> {
                binding.etRefrigName.setText("")
            }
        }

        when(refrigeIDX == -1){

            true -> {
                Log.e(TAG, "EditRefrigeActivity| NULL INFORMATION: 404")
                showCustomToast("정보를 불러올 수 없습니다")
                dismissLoadingDialog()
            }

            else -> {

                /** API 파싱단계 */
                RetrofitClient.apiService.getFoodsList(refrigeIDX)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ res ->

                        binding.ivNomoreTv.show()
                        binding.ivNomoreEmotion.show()
                        binding.btnAddEats.show()
                        binding.rvEats.show()

                        if(res.result.isNotEmpty()){

                            val listSize = res.result.size

                            binding.ivNomoreEmotion.hide()
                            binding.ivNomoreTv.hide()
                            binding.btnAddEats.hide()

                            /** 식품 리스트 리사이클러뷰 생성 */
                            binding.rvEats.show()

                            for(i in 0 until listSize){
                                addTask(
                                    res.result[i].productImg,
                                    res.result[i].productId,
                                    res.result[i].productName,
                                    res.result[i].date,
                                    res.result[i].description
                                )
                            }

                            dismissLoadingDialog()

                            Log.d(TAG, "EditRefrigeActivity| OnCreate-getFoodsList: ${res.message}")

                        } else {

                            binding.ivNomoreEmotion.show()
                            binding.ivNomoreTv.show()
                            binding.btnAddEats.show()
                            binding.rvEats.hide()

                            dismissLoadingDialog()

                            Log.d(TAG, "EditRefrigeActivity| OnCreate-getFoodsList: ${res.message}")
                        }
                    }, { error ->
                        Log.e(TAG, "EditRefrigeActivity| OnCreate: ${error.message.toString()}")
                        errorPage(error, this)
                        dismissLoadingDialog()
                    }
                )
            }
        }

        binding.btnAddEats.setOnClickListener {
            prefs.setInt(PAGE_NUM_EATS, 0)
            val intent = Intent(this@EditRefrigeActivity, EditEatsActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }

        binding.btnFinish.setOnClickListener {
            finishEdit(binding.etRefrigName.text.toString(), refrigeIDX)
        }
    }

    private fun finishEdit(v1: String, v2: Int) {
        showLoadingDialog(this)

        /** API 파싱단계 */
        RetrofitClient.apiService
            .patchRefrige(v1, v2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "EditRefrigeActivity| finishEdit-patchRefrige: ${it.message}")
                showCustomToast("수정 성공")
                dismissLoadingDialog()
                newActivity()
            }, { error ->
                showCustomToast("수정 실패")
                dismissLoadingDialog()
                errorPage(error, this)
            }
        )
    }

    /** 배열 추가 */
    private fun initRecycler() {
        eatsRVAdapter = EatsRVAdapter(this) // 배열에 어댑터 추가
        binding.rvEats.adapter = eatsRVAdapter

        datas.apply {  }

        eatsRVAdapter.datas = datas
        eatsRVAdapter.notifyDataSetChanged()

        /** 리사이클러뷰 onItem 클릭 이벤트리스너 */
        eatsRVAdapter.setOnItemClickListener(object : EatsRVAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: EatsRVData, pos: Int) {
                Intent(this@EditRefrigeActivity, InfoEatsActivity::class.java).apply{
                    InfoRefrigeActivity.prefs.setInt(EATS_IDX, data.productidx)
                }.run {
                    startActivity(this)
                }
            }
        })
    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */
    private fun addTask(image: String, productsIDX: Int, title: String, date: String, description: String) {
        val result = EatsRVData(image, productsIDX, title, date, description)
        datas.add(result)

        binding.rvEats.adapter?.notifyDataSetChanged()
    }

    private fun newActivity() {
        val i = Intent(this@EditRefrigeActivity, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    override fun onRestart() {
        super.onRestart()

        recreate()
    }
}