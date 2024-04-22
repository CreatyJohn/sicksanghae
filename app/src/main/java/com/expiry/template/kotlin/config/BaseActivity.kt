package com.expiry.template.kotlin.config

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.Constants.setStatusBarTransparent
import com.expiry.template.kotlin.util.LoadingDialog
import com.expiry.template.kotlin.util.TAG

// 액티비티의 기본을 작성, 뷰 바인딩 활용
@SuppressLint("ObsoleteSdkInt", "InlinedApi")
abstract class BaseActivity<B : ViewBinding>(
    private val inflate: (LayoutInflater) -> B,
) : AppCompatActivity() {

    protected lateinit var binding: B
    lateinit var mLoadingDialog: LoadingDialog

    // 뷰 바인딩 객체를 받아서 inflate해서 화면을 만들어줌.
    // 즉 매번 onCreate에서 setContentView를 하지 않아도 됨.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setStatusBarTransparent()

        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }

    // 로딩 다이얼로그, 즉 로딩창을 띄워줌.
    // 네트워크가 시작될 때 사용자가 무작정 기다리게 하지 않기 위해 작성.
    fun showLoadingDialog(context: Context) {
        mLoadingDialog = LoadingDialog(context)
        mLoadingDialog.show()
    }

    // 띄워 놓은 로딩 다이얼로그를 없앰.
    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }

    /** 팝업창으로 대체 (완료)
     * https://todaycode.tistory.com/184  */
    // 파라미터 정보입니다 (int 사용할 버튼 갯수 (한개 사용 1 /두개 사용 2), 알림에 들어갈 내용, 닫기 이외에 사용될 버튼이 있으면 사용할 버튼의 텍스트 (확인, 허용 등),
    // 한개 버튼 사용 시 콜백될 함수 1, 두개 버튼 사용 시 콜백될 함수 2)
    // 함수 사용할 꺼면 넣고 안 할꺼면 null 처리 가능 // 예외처리 해두었음.
    fun showAlertDialog(
        btnNum: Int,
        alertText: String,
        btnText: String,
        customRun: Runnable?,
        cancelRun: Runnable?
    ) {

        // XML 레이아웃 파일을 로드
        val inflater = layoutInflater
        val customView: View = inflater.inflate(R.layout.fragment_alert_dialog, null)

        // 레이아웃의 사용할 View들을 세팅
        val information = customView.findViewById<TextView>(R.id.tv_information)
        val customButton = customView.findViewById<Button>(R.id.btn_custom)
        val closeButton = customView.findViewById<Button>(R.id.btn_close)

        // AlertDialog에 레이아웃 설정
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.AlertDialog)
        alertDialogBuilder.setView(customView)

        // 알람 내용과 버튼 종류 설정
        information.text = alertText
        customButton.text = btnText
        try {
            // AlertDialog 표시
            val alertDialog = alertDialogBuilder.create()

            // 배경 터치 막기
            alertDialog.setCancelable(false)

            // 팝업창 활성
            alertDialog.show()

            when (btnNum) {
                1 -> {
                    closeButton.hide()
                    customButton.show()
                    customButton.setOnClickListener {
                        if (customRun != null) {
                            customRun.run()
                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    }
                }

                2 -> {
                    closeButton.show()
                    customButton.show()
                    closeButton.setOnClickListener {
                        if (cancelRun != null) {
                            cancelRun.run()
                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    }
                    customButton.setOnClickListener {
                        if (customRun != null) {
                            customRun.run()
                            alertDialog.dismiss()
                        }
                    }
                }

                else -> {
                    closeButton.hide()
                    customButton.hide()
                    Log.e(TAG, "showAlertDialog: Wrong Derection! Plz try Again")
                    finish()
                }
            }
        } catch (e: Exception) {
            // 에러 발생
            Log.e(TAG, "showAlertDialog catchERROR : ", e)
        }
    }

    // 토스트를 쉽게 띄울 수 있게 해줌.
    fun showCustomToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }
}
