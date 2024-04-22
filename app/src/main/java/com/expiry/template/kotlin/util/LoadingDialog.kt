package com.expiry.template.kotlin.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.expiry.template.kotlin.R

class LoadingDialog(context: Context) : Dialog(context) {

    //    private lateinit var binding: DialogLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)

//        Glide.with(context).load(R.raw.loading_gif).into(binding.ivGifBounge)
        Glide.with(context).load(R.raw.loading_gif)
            .apply(RequestOptions().override(200,200))
            .into(findViewById<ImageView>(R.id.ivGifBounge) as ImageView)

        // 취소 불가능
        setCancelable(false)

        // 배경 투명하게 바꿔줌
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}
