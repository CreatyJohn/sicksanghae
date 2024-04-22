package com.expiry.template.kotlin.src.reqres

import com.expiry.template.kotlin.util.API.BASE_URL
import com.expiry.template.kotlin.util.API.BASE_URL_FCM
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

//싱글턴 방식으로 생성한 레트로핏 오브젝트
object RetrofitClient {

    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
    }

    val apiService: IRetrofit by lazy {
        retrofit
            .build()
            .create(IRetrofit::class.java)
    }
}

object RetrofitClientFCM {

    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_FCM)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: IRetrofitFCM by lazy {
        retrofit
            .build()
            .create(IRetrofitFCM::class.java)
    }
}