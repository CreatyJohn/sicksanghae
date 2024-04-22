package com.expiry.template.kotlin.src.main.refrige

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityInfoeatsBinding
import com.expiry.template.kotlin.src.reqres.*
import com.expiry.template.kotlin.util.KEY.DEVICETOKEN
import com.expiry.template.kotlin.util.KEY.EATS_IDX
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.refrige.InfoEatsActivityVM
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.PAGE_NUM_EATS
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@SuppressLint("CheckResult", "SimpleDateFormat")
class InfoEatsActivity : BaseActivity<ActivityInfoeatsBinding>(ActivityInfoeatsBinding::inflate) {

    private lateinit var viewModel: InfoEatsActivityVM
    private lateinit var dDay: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceUtil(applicationContext)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        val eatsIDX = prefs.getInt(EATS_IDX, -1)

        binding.appbarlayout.bringToFront()

        /** API 파싱단계 */
        RetrofitClient.apiService.getFood(eatsIDX)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                try {
                    val foodsName = it.result.productName
                    binding.tvEatsName.text = it.result.productName
                    binding.tvEatsInformation.text = it.result.description

                    showLoadingDialog(this)

                    if (it.result.productImg.isNullOrEmpty()) dismissLoadingDialog()
                    else {
                        Glide.with(this)
                            .load(it.result.productImg)
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                    dismissLoadingDialog()
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                    dismissLoadingDialog()
                                    return false
                                }
                            })
                            .into(binding.ivEatsImg)
                    }

                    dDay = it.result.date

                    // 숫자가 8자리인 경우 (YYYYMMDD 형식)
                    if (it.result.date.length == 8) {
                        val result1 = it.result.date.substring(0, 4)
                        val result2 = it.result.date.substring(4, 6)
                        val result3 = it.result.date.substring(6, 8)
                        val veryResult = "$result1.$result2.$result3"
                        binding.tvEatsExpiry.text = veryResult

                        Log.d(TAG, "InfoEatsActivity| onCreate-getFood: ${it.message}")

//                        Log.d(TAG, "InfoEatsActivity: " +
//                                "\n\tproductName ${it.result.productName}" +
//                                "\n\tproductInformation ${it.result.description}" +
//                                "\n\tproductDate $veryResult" +
//                                "\n\tproductImage ${it.result.productImg}")

                    } else {
                        // 숫자가 8자리가 아닌 경우 잘못된 형식이라고 판단
                        Log.e(TAG, "getEatsInfo: 숫자가 8자리가 아님")
                    }

                    fcmButton(foodsName)
                    deleteFood(eatsIDX, foodsName)

                }catch(e: Exception){
                    Log.e(TAG,"error: $e")
                }
            }, { error ->
                Log.e(TAG, "eatsInformation: ${error.message}")
                errorPage(error,this)
            })

        /** 식품 수정 */
        binding.btnEdit.setOnClickListener {
            InfoRefrigeActivity.prefs.setInt(PAGE_NUM_EATS, 1)
            val intent = Intent(this, EditEatsActivity::class.java)
            startActivity(intent)
        }

        /** GO back */
        binding.btnGoBack.setOnClickListener { finish() }
    }

    private fun fcmButton(eatsName: String) {
        binding.btnFcmTest.setOnClickListener {
            val deviceToken = prefs.getString(DEVICETOKEN, NULL)
            val header = "key=AAAAZD0ErRc:APA91bFY1U1AvVGMzAw0wtES71R1gx3Ak3DWyZ1-2Gxt27c2MqLNcvfwVf0jlxTnd_Ly_V-evVKf8m20hKNlgSeB2Rsn-5Gc9h5jdceZZ6IgHiHHDf8Tv_hJ12qE2gN2u6BP-fh2Af2a"
            val result = daysDifference(dDay).toInt()

            Log.d(TAG, "InfoEatsActivity| fcmButton-deviceToken: ${deviceToken}")

            try {

                lateinit var datas: Datas

                if (result > 0) {
                    datas = Datas(
                        deviceToken,
                        "high",
                        Data(
                            "음식이 상하고 있어요! 어서 드세요!",
                            "'${eatsName}'식품의 소비기한이 ${result}일 남았습니다"
                        )
                    )
                } else if (result == 0) {
                    datas = Datas(
                        deviceToken,
                        "high",
                        Data(
                            "오늘까지 드! 세! 요!",
                            "오늘이 '${eatsName}'식품의 소비기한 당일입니다"
                        )
                    )
                } else {
                    datas = Datas(
                        deviceToken,
                        "high",
                        Data(
                            "음식이 상했어요! 얼른 버리세요!",
                            "'${eatsName}'식품의 소비기한이 ${result*-1}일 지났습니다"
                        )
                    )
                }

                RetrofitClientFCM.apiService.postFcmTest(header, datas)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ res ->
                        Log.d(TAG, "InfoEatsActivity| postFcmTest-isSuccessed???: ${res.success}")
                    }, { error ->
                        Log.e(TAG, "fcmTestTry: ${error.message}")
                        errorPage(error, this)
                    })

            } catch (e: Exception) {
                Log.e(TAG, "fcmTestCatch: ${e.message}")
            }
        }
    }

    private fun deleteFood(eatsIdx: Int, eatsName: String) {
        binding.btnDelete.setOnClickListener {
            showLoadingDialog(this)
            val confirmTask = Runnable {
                // 데이터 로딩을 수행할 ViewModel 인스턴스 생성
                viewModel = ViewModelProvider(this)[InfoEatsActivityVM::class.java]
                viewModel.deleteEatsInfo(eatsIdx)
                finish()
            }
            showAlertDialog(2, "${eatsName}식품을\n삭제하시겠습니까?", "삭제", confirmTask, null)
            dismissLoadingDialog()
        }
    }

    // FCM 때문에 임시로 오늘 날짜 구해보기
    private fun daysDifference(foodsday: String): Long {
        // 오늘 날짜
        val today = LocalDate.now()
        // 소비기한 날짜
        val foodDate = foodsday // yyyy-MM-dd 형식의 문자열
        val formatterDday = DateTimeFormatter.ofPattern("yyyyMMdd") // 날짜 형식 지정
        val otherDay = LocalDate.parse(foodDate, formatterDday)
        // 포맷
        today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        otherDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        // 날짜 차수 (daysDiffrence 가 결과)
        return ChronoUnit.DAYS.between(today, otherDay)
        // 끝 (나중에 하나의 함수로 정리예정)
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.setInt(EATS_IDX, -1)
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}