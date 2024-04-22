package com.expiry.template.kotlin.src.main.home

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.src.login.BottomSheetDialogLogin
import com.expiry.template.kotlin.src.main.refrige.EditRefrigeActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import com.expiry.template.kotlin.util.KEY.REFRIGE_NAME
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.TAG
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@SuppressLint("CutPasteId", "CheckResult")
class BottomSheetDialogAddRefris : BottomSheetDialogFragment() {

    private lateinit var editText: EditText
    private lateinit var message: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottomsheetaddrefis, container, false)

        message = ""

        prefs = PreferenceUtil(requireContext())
        BottomSheetDialogLogin.prefs = PreferenceUtil(requireContext())

        // 화면 전환시, edittext 바로 출력
        editText = view.findViewById<EditText>(R.id.et_refris_nickname)
        editText.requestFocus()

        view.findViewById<ConstraintLayout>(R.id.cl_contain).bringToFront()

        val btnEnter = view.findViewById<Button>(R.id.btn_enter)
        btnEnter.setBackgroundResource(R.drawable.unripple_square_effect_nickname_button)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            /** 값 변경 시 실행되는 함수 */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /** 메세지 입력 값 담기 */
                message = editText.text.toString()

                /** 값 유무에 따른 활성화 여부 */
                btnEnter.isEnabled = message.isNotEmpty() // editText에 값이 있다면 true 없다면 false

                when(btnEnter.isEnabled){
                  true -> {
                      btnEnter.setBackgroundResource(R.drawable.ripple_square_effect_nickname_button)
                      btnEnter.isEnabled = true
                  }
                  false  -> {
                      btnEnter.setBackgroundResource(R.drawable.unripple_square_effect_nickname_button)
                      btnEnter.isEnabled = false
                  }
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        btnEnter.setOnClickListener {

            // jwt
            val jwtdata = prefs.getString(JWT, "null")
            // 냉장고 이름 받고
            val editText = view?.findViewById<EditText>(R.id.et_refris_nickname)!!.text.toString()

            /** API 파싱단계 */
            RetrofitClient.apiService
                .postRefrigerator(jwtdata, editText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.d(TAG, "BottomSheetDialogAddRefris| postRefrigerator: ${it.message}")

                    try {
//                        Log.d(TAG, "BottomSheetDialogAddRefis: result - ${it.result}")
//                        Log.d(TAG, "BottomSheetDialogAddRefis: resMessage - ${it.message}")
//                        Log.d(TAG, "BottomSheetDialogAddRefis: editText - $editText")

                        // 프래그먼트에서 finish() 하기
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.remove(this@BottomSheetDialogAddRefris)
                            ?.commit()

                        when(it.result){
                            -1 -> { TODO() }
                            else -> {
                                prefs.setInt(REFRIGE_IDX, it.result)
                                prefs.setString(REFRIGE_NAME, editText)
                            }
                        }

                        /** 냉장고 생성 레이아웃 파싱 */
                        startActivity(Intent(requireContext(), EditRefrigeActivity::class.java))

                    }catch(e: Exception){

                        Log.e(TAG, "ERROR")

                    }
                }, { error ->
                    errorPage(error, requireContext())
            })
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<Button>(R.id.btn_enter)!!.isEnabled = false
    }
}