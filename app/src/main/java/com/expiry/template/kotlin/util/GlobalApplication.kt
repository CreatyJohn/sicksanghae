package com.expiry.template.kotlin.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "eee8b1ff00e9f8c2ce8018b29dbb9936")

        NaverIdLoginSDK.initialize(this, "o3UsmTVEYGORAREFn9w_", "iEQtOrGEHZ", "식상해"
        )
    }
}