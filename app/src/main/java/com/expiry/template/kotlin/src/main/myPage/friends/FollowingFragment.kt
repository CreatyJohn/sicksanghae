package com.expiry.template.kotlin.src.main.myPage.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseFragment
import com.expiry.template.kotlin.databinding.FragmentFollowingBinding
import com.expiry.template.kotlin.util.TAG
import com.expiry.template.kotlin.src.viewModel.main.mypage.friends.FriendsRVAdapter
import com.expiry.template.kotlin.src.viewModel.main.mypage.friends.FriendsVM

@SuppressLint("NotifyDataSetChanged")
class FollowingFragment : BaseFragment<FragmentFollowingBinding>(FragmentFollowingBinding::bind, R.layout.fragment_following) {

    private lateinit var friendsRVAdapter: FriendsRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FriendsVM
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingDialog(requireContext())

        recyclerView = binding.rvFollowing
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        Log.d(TAG, "onCreate: FollowFragment")

        // DATA HERE
        viewModel = ViewModelProvider(requireActivity()).get(FriendsVM::class.java)
        viewModel.getFollowerList()

        initRecycler()

        dismissLoadingDialog()
    }

    private fun initRecycler() {
        friendsRVAdapter = FriendsRVAdapter(requireContext())
        friendsRVAdapter.showFollowingButton = true
        binding.rvFollowing.adapter = friendsRVAdapter

        val refrisData = viewModel.getFriendsData()
        friendsRVAdapter.datas.clear()

        if (refrisData.isNotEmpty()) {
            friendsRVAdapter.datas.addAll(refrisData)
        } else {
            Log.e(TAG, "FollowingFragment initRecycler: NullPointerException ERROR")
        }

        friendsRVAdapter.notifyDataSetChanged()
    }
}