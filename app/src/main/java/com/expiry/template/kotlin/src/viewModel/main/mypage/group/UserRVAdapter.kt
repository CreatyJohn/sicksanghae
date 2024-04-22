package com.expiry.template.kotlin.src.viewModel.main.mypage.group

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.src.main.myPage.group.InviteRefFragment
import com.expiry.template.kotlin.src.main.myPage.group.KickRefFragment
import com.expiry.template.kotlin.src.main.myPage.group.UserSearchNoTabActivity
import com.expiry.template.kotlin.util.LoadingDialog

data class UserModel (
    var userIDX: Int,
    var name : String,
    var email : String,
    var profileImg: String
)

class InviteRefNoTabRVAdapter(
    private val context: Context,
    private val userSearchNoTabActivity: UserSearchNoTabActivity
) : RecyclerView.Adapter<InviteRefNoTabRVAdapter.ViewHolder>() {

    var datas = mutableListOf<UserModel>()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_user_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val nickName: TextView = itemView.findViewById(R.id.tv_rv_nickname)
        private val email: TextView = itemView.findViewById(R.id.tv_rv_email)
        private val btnInvite: Button = itemView.findViewById(R.id.btn_invite_group)
        private val profileImg: ImageView = itemView.findViewById(R.id.cv_img)

        fun bind(item: UserModel) {
            btnInvite.text = "초대"
            btnInvite.setTextColor(Color.BLACK)
            btnInvite.setBackgroundResource(R.drawable.ripple_radius4_stroke_background)

            nickName.text = item.name
            email.text = item.email

            btnInvite.setOnClickListener {
                userSearchNoTabActivity.inviteGroup(item.userIDX)
            }

            Glide.with(context)
                .load(item.profileImg)
                .into(profileImg)
        }

        override fun onClick(v: View?) {}
    }

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
}

class InviteRefRVAdapter(
    private val context: Context,
    private val inviteRefFragment: InviteRefFragment
) : RecyclerView.Adapter<InviteRefRVAdapter.ViewHolder>() {

    var datas = mutableListOf<UserModel>()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_user_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val nickName: TextView = itemView.findViewById(R.id.tv_rv_nickname)
        private val email: TextView = itemView.findViewById(R.id.tv_rv_email)
        private val btnInvite: Button = itemView.findViewById(R.id.btn_invite_group)
        private val profileImg: ImageView = itemView.findViewById(R.id.cv_img)

        fun bind(item: UserModel) {
            btnInvite.text = "초대"
            btnInvite.setTextColor(Color.BLACK)
            btnInvite.setBackgroundResource(R.drawable.ripple_radius4_stroke_background)
            
            nickName.text = item.name
            email.text = item.email

            btnInvite.setOnClickListener {
                inviteRefFragment.inviteGroup(item.userIDX)
            }

//            showLoadingDialog(context)
//            Glide.with(context)
//                .load(item.profileImg)
//                .listener(object: RequestListener<Drawable> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                        dismissLoadingDialog()
//                        return false
//                    }
//
//                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                        dismissLoadingDialog()
//                        return false
//                    }
//                })
//                .into(profileImg)

            Glide.with(context)
                .load(item.profileImg)
                .into(profileImg)
        }

        override fun onClick(v: View?) {}
    }

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
}

class KickRefRVAdapter(
    private val context: Context,
    private val kickRefFragment: KickRefFragment
) : RecyclerView.Adapter<KickRefRVAdapter.ViewHolder>() {

    var datas = mutableListOf<UserModel>()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_user_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val nickName: TextView = itemView.findViewById(R.id.tv_rv_nickname)
        private val email: TextView = itemView.findViewById(R.id.tv_rv_email)
        private val btnInvite: Button = itemView.findViewById(R.id.btn_invite_group)
        private val profileImg: ImageView = itemView.findViewById(R.id.cv_img)

        fun bind(item: UserModel) {
            btnInvite.setTextColor(Color.WHITE)
            btnInvite.setBackgroundResource(R.drawable.ripple_radius4_background)

            btnInvite.text = "추방"
            nickName.text = item.name
            email.text = item.email

            btnInvite.setOnClickListener {
                kickRefFragment.exileGroup(item.userIDX)
            }

//            showLoadingDialog(context)
//            Glide.with(context)
//                .load(item.profileImg)
//                .listener(object: RequestListener<Drawable> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                        dismissLoadingDialog()
//                        return false
//                    }
//
//                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                        dismissLoadingDialog()
//                        return false
//                    }
//                })
//                .into(profileImg)

            Glide.with(context)
                .load(item.profileImg)
                .into(profileImg)
        }

        override fun onClick(v: View?) {}
    }

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
}
