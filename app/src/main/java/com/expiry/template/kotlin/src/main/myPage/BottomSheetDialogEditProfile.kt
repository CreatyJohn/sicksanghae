package com.expiry.template.kotlin.src.main.myPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment.Companion.prefs
import com.expiry.template.kotlin.src.login.LoginActivity
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.viewModel.main.mypage.BottomSheetDialogEditProfileVM
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.LOGININFO.NICKNAME
import com.expiry.template.kotlin.util.NULL
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@SuppressLint("CutPasteId", "MissingInflatedId")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("DEPRECATION")
class BottomSheetDialogEditProfile : BottomSheetDialogFragment() {

    lateinit var mainActivity: MainActivity
    lateinit var viewModel: BottomSheetDialogEditProfileVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottomsheeteditprofile, container, false)

        val btnEditNick = view.findViewById<Button>(R.id.btn_edit_users_info)
        val btnReview = view.findViewById<Button>(R.id.btn_review)
//        val btnRegist = view.findViewById<Button>(R.id.btn_regist)
        val btnDeleteUser = view.findViewById<Button>(R.id.btn_delete_user)

        btnEditNick.setOnClickListener {
            prefs.setInt(NICKNAME, 1)
            startActivity(Intent(requireContext(), UploadUsersActivity::class.java))
            // 프래그먼트에서 finish() 하기
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }

        // 알림 설정
        btnReview.setOnClickListener {
            val run = Runnable {
                // 원하는 웹 페이지 URL을 지정합니다.
                val url = "https://forms.gle/SbSdyrkWvQB555V77"
                // URL을 열기 위한 Intent를 생성합니다.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                // 웹 브라우저를 열기 위한 앱을 선택하도록 요청합니다.
                startActivity(intent)
            }
            mainActivity.showAlertDialog(2, "피드백을\n진행하시겠습니까?", "확인", run, null)
        }

//        // 고객 문의
//        btnRegist.setOnClickListener {
//
//        }

        // 회원 탈퇴
        btnDeleteUser.setOnClickListener {
            val run = Runnable {
                // 데이터 로딩을 수행할 ViewModel 인스턴스 생성
                viewModel = ViewModelProvider(requireActivity())[BottomSheetDialogEditProfileVM::class.java]
                viewModel.deleteUsers(prefs.getString(JWT, NULL))

                viewModel.myData.observe(viewLifecycleOwner, Observer { it ->
                    when (it) {
                        true -> success()
                        false -> mainActivity.showCustomToast("회원탈퇴 실패")
                        else -> mainActivity.showCustomToast("다시 시도해주세요!")
                    }
                })
            }
            mainActivity.showAlertDialog(2, "정말로 회원탈퇴를\n진행하시겠습니까?", "회원탈퇴", run, null)
        }

        return view
    }

    private fun success() {
        activity?.let {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        prefs.destroyData()
        mainActivity.showCustomToast("회원탈퇴 성공!")
    }
}