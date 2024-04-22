package com.expiry.template.kotlin.src.viewModel.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expiry.template.kotlin.R
import kotlin.reflect.jvm.internal.impl.utils.DFS.VisitedWithSet

data class RefrisModel (
    var isOwner : Int,
    var productidx : Int,
    var title : String
)

class RefRVAdapter(
    private val context: Context
) : RecyclerView.Adapter<RefRVAdapter.ViewHolder>() {

    var datas = mutableListOf<RefrisModel>()
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rv_grid_double, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val txtTitle: TextView = itemView.findViewById(R.id.tv_rv_title)
        private val ivOwner: ImageView = itemView.findViewById(R.id.iv_owner_crown)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: RefrisModel) {
            txtTitle.text = item.title
            if(item.isOwner == 1) {
                ivOwner.visibility = View.VISIBLE
            } else{
                ivOwner.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = datas[position]
                listener?.onItemClick(v, data, position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: RefrisModel, pos: Int)
    }
}