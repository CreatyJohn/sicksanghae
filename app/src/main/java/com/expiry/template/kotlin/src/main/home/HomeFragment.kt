package com.expiry.template.kotlin.src.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment
import com.expiry.template.kotlin.databinding.FragmentHomeBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.main.refrige.InfoRefrigeActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import com.expiry.template.kotlin.util.KEY.REFRIGE_NAME
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.home.HomeFragmentVM
import com.expiry.template.kotlin.src.viewModel.main.home.RefRVAdapter
import com.expiry.template.kotlin.src.viewModel.main.home.RefrisModel
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.KEY.OWNER
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

@SuppressLint("SetTextI18n, NotifyDataSetChanged, ObsoleteSdkInt, CheckResult")
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private lateinit var refrigeRVAdapter: RefRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: HomeFragmentVM
    private lateinit var jwtData: String

    // RecyclerView에 표시될 데이터를 담을 MutableList
    private val datas = mutableListOf<RefrisModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvRefris
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        binding.btnFabMain.setOnClickListener {
            activity?.let {
                val bottomSheet = BottomSheetDialogAddRefris()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        }

        binding.ivMyProfile.setOnClickListener {
            (activity as MainActivity).switchBottomNavigationView(R.id.menu_main_btm_nav_profile)
        }
    }

     private fun initRecycler() {
         refrigeRVAdapter = RefRVAdapter(requireContext())
         recyclerView.adapter = refrigeRVAdapter

         refrigeRVAdapter.datas.clear()
         refrigeRVAdapter.datas.addAll(datas)
         refrigeRVAdapter.notifyDataSetChanged()

         refrigeRVAdapter.setOnItemClickListener(object : RefRVAdapter.OnItemClickListener {
             override fun onItemClick(v: View, data: RefrisModel, pos: Int) {
                 Intent(requireContext(), InfoRefrigeActivity::class.java).apply {
                     prefs.setInt(OWNER, data.isOwner)
                     prefs.setInt(REFRIGE_IDX, data.productidx)
                     prefs.setString(REFRIGE_NAME, data.title)
                 }.run {
                     startActivity(this)
                 }
             }
         })
     }

    private fun getRefrisList(jwt: String) {
        /** API 파싱단계 */
        RetrofitClient.apiService.getRefrisList(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response->

                // 데이터 초기화
                datas.clear()
                // 받아온 데이터를 반복문을 통해 RecyclerView에 추가
                val count = response.result.size
                if(response.result.isNotEmpty()){
                    // 결과 알림
                    for (i in 0 until count) {
                        addTask(
                            response.result[i].ownerFg,
                            response.result[i].refrigeratorId,
                            response.result[i].refrigeratorName
                        )
                    }
                    initRecycler()
                }
                Log.d(TAG, "HomeFragment| getRefrisList: ${response.message}")

            }, { error ->
                errorPage(error, requireContext())
            })
    }

    /** 외부에서 배열에 추가할 수 있게 사용하는 곳 */
    private fun addTask(isMine: Int, productsIDX: Int, title: String) {
        val newData = RefrisModel(isMine, productsIDX, title)
        // 데이터 추가 로직을 수행
        datas.add(newData)
    }

    override fun onResume() {
        super.onResume()

        showLoadingDialog(requireContext())

        // 데이터 로드 작업 시작
        jwtData = prefs.getString(JWT, NULL)
        viewModel = ViewModelProvider(requireActivity())[HomeFragmentVM::class.java]

        getRefrisList(jwtData)
        viewModel.getUsers(jwtData)

        viewModel.myData1.observe(viewLifecycleOwner, Observer { name ->
            binding.tvTitle.text = "${name}님의 냉장고"
        })

        viewModel.myData2.observe(viewLifecycleOwner, Observer { img ->
            if (img.isNullOrEmpty()) dismissLoadingDialog()
            else {
                Glide.with(this@HomeFragment)
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
                    .into(binding.ivMyProfile)
            }
        })
    }
}