package com.expiry.template.kotlin.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.core.view.WindowCompat
import com.expiry.template.kotlin.src.main.MainActivity
import retrofit2.HttpException

object API {

    // Rest Api의 기본 Base URL
//    const val BASE_URL: String = "http://210.123.255.110:8080/app/" //현우 본체
//    const val BASE_URL: String = "http://seyun298.site:9000/app/" //세윤 본체
//    const val BASE_URL: String = "http://52.78.12.147:9000/app/" // WinScp&Docker 서버
    const val BASE_URL: String = "https://fgmate.shop/app/" // New 서버 (from 230922)
    const val BASE_URL_FCM: String = "https://fcm.googleapis.com/fcm/" // FCM 서버 (from 231009)

    const val SEND_FCM: String = "send"

    // 사용되는 API 들의 URL const object

    //유저 RUD
    const val USERS: String = "users"
    // GETS
    const val GET_ALL_USERS: String = "users/all"
    const val GET_SOME_USERS: String = "users/list"
    const val GET_GROUP_IN_USER: String = "users/group/in"
    const val GET_GROUP_NOT_IN_USER: String = "users/group/notIn"
    const val GET_ALL_REFRIS: String = "refris" // 전체 냉장고 조회 (리스트)
    const val GET_ALL_EATS: String = "products/{refriId}" //전체 식품 조회
    const val GET_FOODS: String = "products" //식품 1개 조회
    const val GET_FOODS_COMMENTS: String = "products/comment/{productId}" //식품에 달린 댓글 전체 조회
//    const val GET_MY_COMMENTS: String = "users/comment" //내 댓글 조회

    // POSTS
    const val POST_SOCIAL_LOGIN: String = "users/login" //카카오 로그인
    const val POST_REFRIS: String = "refris/{name}" //냉장고 생성
    const val POST_INVITE_GROUP: String = "refris/{refriId}/{userId}" //냉장고 그룹 초대
    const val POST_FOODS: String = "products/upload" //식품 등록 test
//    const val POST_FOODS: String = "products" //식품 등록
    const val POST_FOODS_COMMENTS: String = "products/comment" //식품에 댓글 작성
//    const val POST_EMOTION: String = "products/emo" //식품 이모티콘

    // PATCH
    const val PATCH_USERS_LOGOUT: String = "users/logout"
    const val PATCH_USERS_UPLOAD: String = "users/upload"
    const val PATCH_REFRIG: String = "refris/{name}/{refriId}" //냉장고 정보 수정
    const val PATCH_FOODS: String = "products" //식품 수정
    const val PATCH_NICKNAME: String = "users/{name}" //닉네임 설정
    const val PATCH_COMMENT: String = "products/comment/{commentId}" //댓글 수정
//    const val PATCH_EMOTION: String = "products/emo" //이모티콘 수정

    // DELETE
    const val DELETE_REFRIG: String = "refris/{refriId}" //냉장고 삭제
    const val DELETE_GROUP: String = "refris/{refriId}/{userId}" //냉장고 그룹 탈퇴(추방)
    const val DELETE_FOODS: String = "products/{productId}" //식품 삭제
//    const val DELETE_EMOTION: String = "products/emo" //이모티콘 삭제
    const val DELETE_COMMENT: String = "products/comment/{commentId}" //댓글 삭제
}

object KEY {
    const val REFRIGE_IDX = "refrigeratorsidx"
    const val REFRIGE_NAME = "refrigeratorsname"
    const val EATS_IDX = "eatsidx"
    const val USER_ID = "useridx"
    const val SOCIAL = "sociallogin"
    const val PAGE_NUM_EATS = "pagenumbertoeats"
    const val DEVICETOKEN = "devicetoken"
    const val PRODUCTID = "productId"
    const val CLICKREFIDX = "idxwhenclicked"
    const val REFOWNER ="REFOWNERREFOWNERREFOWNER"
    const val OWNER = "ownerforcheck"
    const val KEYHASH = "KEYHASHKEYHASHKEYHASHKEYHASH"
}

object LOGININFO {
    const val KAKAOKEY = "KAKAO"
    const val NAVERKEY = "NAVER"
    const val NICKNAME = "NICKNAME"
    const val NAVERTOKEN = "NACCESSTOKEN"
}

object PERMISSION_CODE_NUM {
    const val MY_PERMISSIONS_REQUEST_CAMERA = 101
}

const val TAG = "logtag"
const val NULL = "NULL"
const val JWT = "JWT"

const val REQUEST_FIRST = 1000
const val CODE = 1080
const val RESULT_KEY = "keyResult"
const val INTENT_KEY = "intent_key"
const val INTENT_VALUE = 1006

@SuppressLint("DiscouragedApi", "InternalInsetResource")
object Constants {
    fun errorPage(error: Throwable, context: Context) {
        // Exception Handling
        if (error is HttpException) {
            if (error.code() == 404) {
                // HTTP 404 에러 발생 시 처리
                (context as MainActivity).showLoadingDialog(context)
                val finish = Runnable {
                    (context).finish()
                    (context).dismissLoadingDialog()
                }
                (context).showAlertDialog(1, "데이터베이스 연결에 실패하였습니다\n잠시 후, 다시 시도해주세요", "닫기", finish, null)
                Log.e(TAG, "User not found")
            } else if (error.code() == 502) {
                // 다른 HTTP 에러 발생 시 처리
                (context as MainActivity).showLoadingDialog(context)
                val finish = Runnable {
                    (context).finish()
                    (context).dismissLoadingDialog()
                }
                (context).showAlertDialog(1, "서버 연결에 실패하였습니다\n잠시 후, 다시 시도해주세요", "닫기", finish, null)
                Log.e(TAG, "HTTP Error ${error.code()}")
            } else {
                Log.e(TAG, "HTTP Error ${error.code()}")
            }
        } else {
            // 다른 에러 발생 시 처리
            Log.e(TAG, "Not HTTP Error: ${error.message}")
        }
    }

    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    fun Context.statusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }

    fun Context.navigationHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }
}