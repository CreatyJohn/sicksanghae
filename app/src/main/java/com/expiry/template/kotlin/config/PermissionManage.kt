package com.expiry.template.kotlin.config

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PermissionManage: AppCompatActivity() {

    /**
     * 1005 권준우 권한/ 팝업 설정
     */
    // sharedPreferences
    private val PREF_NAME = "PermissionPrefs"
    private val KEY_CAMERA_PERMISSION_DENIED_COUNT = "CameraPermissionDeniedCount"
    private val KEY_NOTIFY_PERMISSION_DENIED_COUNT = "NotifyPermissionDeniedCount"
    private val KEY_IMAGE_PERMISSION_DENIED_COUNT = "ImagePermissionDeniedCount"
    private val KEY_WSTORAGE_PERMISSION_DENIED_COUNT = "StoragePermissionDeniedCount"

    var REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA, // 카메라 권한
            Manifest.permission.READ_EXTERNAL_STORAGE, // 저장소 권한
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 저장소 권한
            Manifest.permission.READ_MEDIA_IMAGES, // 저장소 권한
            Manifest.permission.READ_MEDIA_VIDEO, // 저장소 권한
            Manifest.permission.POST_NOTIFICATIONS // 알림 권한
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA, // 카메라 권한
            Manifest.permission.READ_EXTERNAL_STORAGE, // 저장소 권한
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 저장소 권한
            Manifest.permission.POST_NOTIFICATIONS // 알림 권한
        )
    }
}