package com.expiry.template.kotlin.src.main.myPage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment
import com.expiry.template.kotlin.databinding.FragmentMyPageBinding
import com.expiry.template.kotlin.src.login.LoginActivity
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.KEY.SOCIAL
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.mypage.MyPageFragmentVM
import com.expiry.template.kotlin.util.LOGININFO.KAKAOKEY
import com.expiry.template.kotlin.util.LOGININFO.NAVERKEY
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.main.myPage.group.InviteRefrigeActivity
import com.expiry.template.kotlin.util.NULL

@SuppressLint("SetTextI18n, ResourceType")
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private lateinit var viewModel: MyPageFragmentVM
    private lateinit var jwtData: String
    private lateinit var socialLogin: String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jwtData = prefs.getString(JWT, NULL)
        socialLogin = prefs.getString(SOCIAL, NULL)

        /** StartActivity to 팔로잉 팔로우 */
        binding.btnFollowing.setOnClickListener {
//            prefs.setInt(FOLLOWINTRO, 0)
//            startActivity(Intent(requireContext(), FriendsActivity::class.java))
            showAlertDialog(1, "팔로잉 기능을 업데이트 중입니다\n기대해주세요!", "닫기", null, null)
        }

        binding.btnFollow.setOnClickListener {
//            prefs.setInt(FOLLOWINTRO, 1)
//            startActivity(Intent(requireContext(), FriendsActivity::class.java))
            showAlertDialog(1, "팔로워 기능을 업데이트 중입니다\n기대해주세요!", "닫기", null, null)
        }

        binding.btnEditProfile.setOnClickListener {
            activity?.let {
                val bottomSheet = BottomSheetDialogEditProfile()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        }

        binding.btnMyRefrige.setOnClickListener {
            (activity as MainActivity).switchBottomNavigationView(R.id.menu_main_btm_nav_home)
        }

        binding.btnRefrigeGroup.setOnClickListener {
            startActivity(Intent(requireContext(), InviteRefrigeActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val logout = Runnable {
                viewModel.logoutUser(jwtData)
                viewModel.isSuccessed.observe(viewLifecycleOwner) { it ->
                    if (it) {
                        startLoginActivity()
                        prefs.destroyData()
                        showCustomToast("로그아웃 성공")
                    } else {
                        showCustomToast("로그아웃 실패")
                    }
                }
            }
            showAlertDialog(2, "정말로 로그아웃\n하시겠습니까?","로그아웃", logout, null)
        }

        binding.btnReview.setOnClickListener {
            val run = Runnable {
                // 원하는 웹 페이지 URL을 지정합니다.
                val url = "https://forms.gle/SbSdyrkWvQB555V77"
                // URL을 열기 위한 Intent를 생성합니다.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                // 웹 브라우저를 열기 위한 앱을 선택하도록 요청합니다.
                startActivity(intent)
            }
            showAlertDialog(2, "피드백을\n진행하시겠습니까?", "확인", run, null)
        }
    }

    private fun startLoginActivity() {
        activity?.let {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    override fun onResume() {
        super.onResume()

        showLoadingDialog(requireContext())

        viewModel = ViewModelProvider(this)[MyPageFragmentVM::class.java]

        binding.appbarlayout.bringToFront()

        when(socialLogin){
            KAKAOKEY -> {
                binding.ivLoginDot.setImageResource(R.drawable.ic_circle_24_yellow)
                binding.tvLogined.text = KAKAOKEY
            }
            NAVERKEY -> {
                binding.ivLoginDot.setImageResource(R.drawable.ic_circle_24_green)
                binding.tvLogined.text = NAVERKEY
            }
            else -> {
                Log.e(TAG, "ERROR : 회원정보 불러오기")
                showCustomToast("회원정보 갱신실패")

                binding.ivLoginDot.setImageResource(R.drawable.ic_circle_24_gray)
                binding.tvLogined.text = "접속오류"

                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.remove(this)
                    ?.commit()
            }
        }

        viewModel.getUsers(jwtData)

        viewModel.myData1.observe(viewLifecycleOwner, Observer { name ->
            binding.tvMyId.text = name
        })

        viewModel.myData2.observe(viewLifecycleOwner, Observer { img ->
            if (img.isNullOrEmpty()) dismissLoadingDialog()
            else {
                Glide.with(this@MyPageFragment)
                    .load(img)
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
                    .into(binding.ivProfile)
            }
        })
    }
}