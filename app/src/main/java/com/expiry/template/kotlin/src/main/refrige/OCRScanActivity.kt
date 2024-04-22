package com.expiry.template.kotlin.src.main.refrige

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityOcrscanBinding
import com.expiry.template.kotlin.util.CODE
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.PERMISSION_CODE_NUM.MY_PERMISSIONS_REQUEST_CAMERA
import com.expiry.template.kotlin.util.RESULT_KEY
import com.expiry.template.kotlin.util.TAG
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


@SuppressLint("ObsoleteSdkInt", "SdCardPath", "CheckResult", "MissingPermission", "CheckResult")
class OCRScanActivity : BaseActivity<ActivityOcrscanBinding>(ActivityOcrscanBinding::inflate) {

    private lateinit var mCameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        showLoadingDialog(this)

        requestForPermission()

        textRecognizer = TextRecognizer.Builder(this).build()
        if (!textRecognizer.isOperational) {
            showCustomToast("조금 이따가 다시 해주세요")
            Log.e(TAG, "download failure")
            return
        }

        // 고해상도 및 자동 초점을 사용하도록 카메라 소스 초기화
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1920, 1920)
            .setAutoFocusEnabled(true)
            .setRequestedFps(30.0f)
            .build()

        binding.surfaceCameraPreview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(p0: SurfaceHolder) {
                mCameraSource.stop()
            }

            override fun surfaceCreated(p0: SurfaceHolder) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(binding.surfaceCameraPreview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "surfaceCreated: ${e.message}")
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                //TODO
            }
        })

        tRecognizer.invoke()
    }

    // 인식된 텍스트를 TextView에 실시간으로 전달하는 고차함수
    private val tRecognizer: () -> Unit = {
        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                binding.tvResult.post {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    val result = stringBuilder.toString()

                    // 추출된 텍스트에서 숫자만 추출
                    val extractedNumbers = result.replace("\\D".toRegex(), "")

                    // 숫자가 8자리인 경우 (YYYYMMDD 형식)
                    if (extractedNumbers.length == 8) {
                        val result1 = extractedNumbers.substring(0, 4)
                        val result2 = extractedNumbers.substring(4, 6)
                        val result3 = extractedNumbers.substring(6, 8)
                        val veryResult = "$result1.$result2.$result3"
                        binding.tvResult.text = veryResult
                        binding.btCalculate.setOnClickListener { calculate.invoke() }
                    } else {
                        // 숫자가 8자리가 아닌 경우 잘못된 형식이라고 판단
                        binding.tvResult.text = "날짜 형식이 아닙니다"
                        binding.btCalculate.setOnClickListener {
                            showCustomToast("다시 시도해주세요")
                        }
                    }
                }
            }
        })
    }

    private val calculate : () -> Unit = {
        showLoadingDialog(this@OCRScanActivity)

        // RxJava Observable 생성
        Observable.just(binding.tvResult.text.toString())
            .subscribeOn(Schedulers.io()) // IO 스레드에서 실행
            .observeOn(AndroidSchedulers.mainThread()) // 메인 스레드에서 결과 처리
            .subscribe { textCh ->
                if (textCh.isEmpty() || textCh.length == 2) {
                    showCustomToast("소비기한을 다시 스캔해주세요")
                    dismissLoadingDialog()
                } else {
                    val i = Intent(this@OCRScanActivity, EditEatsActivity::class.java)
                    i.putExtra(RESULT_KEY, textCh)
                    setResult(CODE, i)
                    dismissLoadingDialog()
                    showCustomToast("소비기한 스캔 완료")
                    finish()
                }
            }
    }

    /** NEW OCR */
    private fun requestForPermission() {

        // 여기 액티비티의 this 는 현재의 액티비티로..
        if (ContextCompat.checkSelfPermission(this@OCRScanActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 현재 액티비티에서 카메라 권한이 부여
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@OCRScanActivity, Manifest.permission.CAMERA)) {
                // 사용자에게 *비동기적으로* 설명 표시 - 사용자의 응답을 기다리는 동안 이 스레드를 차단하지 않고 사용자가 설명을 본 후 다시 권한 요청
                finish()
                dismissLoadingDialog()
            } else {
                // 허가를 요청할 수 있음
                ActivityCompat.requestPermissions(this@OCRScanActivity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
                // MY_PERMISSION_REQUEST_CAMERA 는 앱으로 정의된 정수이므로 콜백 메서드는 요청의 결과를 가져옴
                dismissLoadingDialog()
            }
        } else {
            // 권한이 이미 허가됨
            dismissLoadingDialog()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@OCRScanActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    //for handling permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // 권한 요청이 취소되었거나 결과 배열이 비어 있는 경우입니다.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 부여되었을 경우, 원하는 작업 넣기
                } else {
                    // 권한이 거부되었을 경우, 설정 창으로 이동하기 팝업
                    finish()
                    showAlertDialog(1, "카메라 권한을\n허용해주세요", "확인", null, null)
                }
                return
            }

            // 다른 'when' 라인을 추가하여 앱이 요청할 수 있는 다른 권한을 확인할 수 있음
            else -> {
                // 다른 모든 요청을 무시
            }

        }
    }
}