package com.expiry.template.kotlin.src.viewModel.main.mypage.friends

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.expiry.template.kotlin.R

@SuppressLint("SetTextI18n")
class FriendsRVAdapter(private val context: Context) : RecyclerView.Adapter<FriendsRVAdapter.ViewHolder>() {

    var datas = mutableListOf<FriendsModel>()

    // 다른 프래그먼트에서 어떤 버튼을 표시할지를 나타내는 변수
    var showFollowingButton = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_friends, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: FriendsModel, pos: Int)
    }

    private var listener: OnItemClickListener? = null

//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.listener = listener
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtNickname: TextView = itemView.findViewById(R.id.tv_rv_nickname)
        private val txtName: TextView = itemView.findViewById(R.id.tv_rv_name)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_rv_profile)
        private val fIngBtn: Button = itemView.findViewById(R.id.btn_rv_following)
        private val fErBtn: Button = itemView.findViewById(R.id.btn_rv_follower)

        fun bind(item: FriendsModel) {

            txtNickname.text = item.nickname
            txtName.text = item.name

            val pos = adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }

            Glide.with(context)
                .load(item.image)
                .into(imgProfile)

            // showFollowingButton 변수에 따라 버튼을 VISIBLE 또는 GONE으로 설정
            if (showFollowingButton) {
                fIngBtn.visibility = View.VISIBLE
                fErBtn.visibility = View.GONE
            } else {
                fIngBtn.visibility = View.GONE
                fErBtn.visibility = View.VISIBLE
            }
        }
    }
}