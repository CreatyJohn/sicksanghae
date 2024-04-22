package com.expiry.template.kotlin.src.viewModel.main.mypage.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.src.main.myPage.group.InviteRefrigeActivity
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import com.expiry.template.kotlin.util.PreferenceUtil

lateinit var prefs: PreferenceUtil

class InviteRefrigeRVAdapter(
    private val context: Context
) : RecyclerView.Adapter<InviteRefrigeRVAdapter.ViewHolder>() {

    var datas = mutableListOf<InviteModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_invite_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val refrigeName: TextView = itemView.findViewById(R.id.tv_rv_nickname)
        private val ivOwner: ImageView = itemView.findViewById(R.id.iv_owner_crown)
        private val btnInvite: Button = itemView.findViewById(R.id.btn_invite_group)
        private val inviteRefrigeActivity = InviteRefrigeActivity.getInstance()
//        private val profileImg: ImageView = itemView.findViewById(R.id.img_rv_profile)

        fun bind(item: InviteModel) {
            refrigeName.text = item.title

            if(item.isOwner == 1) {
                ivOwner.visibility = View.VISIBLE
            } else{
                ivOwner.visibility = View.GONE
            }

            btnInvite.setOnClickListener {
                inviteRefrigeActivity?.selectRefrige(item.refrigeIdx, item.isOwner)
            }
        }

        override fun onClick(v: View?) {}
    }
}

data class InviteModel (
    var isOwner : Int,
    var refrigeIdx : Int,
    var title : String
)