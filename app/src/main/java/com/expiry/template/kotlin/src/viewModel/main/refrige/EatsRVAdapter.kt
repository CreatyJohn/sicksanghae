package com.expiry.template.kotlin.src.viewModel.main.refrige

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.util.LoadingDialog

data class EatsRVData (
    var image : String,
    var productidx : Int,
    var title : String,
    var date : String,
    var description : String
)

@SuppressLint("SetTextI18n")
class EatsRVAdapter (private val context: Context) : RecyclerView.Adapter<EatsRVAdapter.ViewHolder>() {

    var datas = mutableListOf<EatsRVData>()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.eats_item_rv_grid_double, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: EatsRVData, pos: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtTitle: TextView = itemView.findViewById(R.id.tv_rv_title)
        private val imgIcon: ImageView = itemView.findViewById(R.id.img_rv_photos)

        fun bind(item: EatsRVData) {

            txtTitle.text = item.title

            val pos = adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }

            Glide.with(context)
                .load(item.image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .centerCrop()
                .into(imgIcon)
        }
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