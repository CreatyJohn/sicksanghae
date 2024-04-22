package com.expiry.template.kotlin.src.main.refrige

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityEditeatsBinding
import com.expiry.template.kotlin.src.reqres.*
import com.expiry.template.kotlin.util.*
import com.expiry.template.kotlin.util.CODE
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.KEY.EATS_IDX
import com.expiry.template.kotlin.util.KEY.PAGE_NUM_EATS
import com.expiry.template.kotlin.util.KEY.PRODUCTID
import com.expiry.template.kotlin.util.KEY.REFRIGE_IDX
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("WrongThread, SetTextI18n, CheckResult, Deprecated in Java, Recycle, IntentReset, Range, InlinedApi, De")
@SuppressWarnings("deprecation")
class EditEatsActivity : BaseActivity<ActivityEditeatsBinding>(ActivityEditeatsBinding::inflate) {

    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private lateinit var getImageContent: ActivityResultLauncher<Intent>
    private var clickCount = 0

    private lateinit var imageUri: Uri
    private lateinit var imageMP: MultipartBody.Part
    private var originalImageMP: MultipartBody.Part? = null // 기존 이미지를 저장할 변수

    // Date Picking Instance
    private var myCalendar = Calendar.getInstance()
    private var myDatePicker =
        OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateLabel()
        }

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceUtil(applicationContext)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        val eatsIDX = prefs.getInt(EATS_IDX, -1)
        val refrigeIDX = prefs.getInt(REFRIGE_IDX, -1)
        val pageNum = prefs.getInt(PAGE_NUM_EATS, -1)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        // 공통 함수 소스
        textWatcher()
        initAddImageButton()
        pickupDate()

        binding.appbarlayout.bringToFront()

        when (pageNum) {
            0 -> {
                // 식품 등록 0
                try{
                    binding.etEatsExpiry.text = ""

                    initSubmitItemButton(refrigeIDX)
                } catch (e: Exception) {
                    Log.e(TAG, "EditEatsActivity 0: ${e.message}")
                }
            }
            1 -> {
                // 식품 수정 1
                try{
                    getEatsInfo(eatsIDX)

                    initSubmitPatchItemButton(refrigeIDX)
                } catch (e: Exception) {
                    Log.e(TAG, "EditEatsActivity 1: ${e.message}")
                }
            }
            else -> {
                showCustomToast("앱이 충돌하였습니다")
                finish()
            }
        }

        binding.btnOcr.setOnClickListener {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }

        /** GO back */
        binding.btnGoBack.setOnClickListener { finish() }

        //--------------------------------ResultForActivity----------------------------------

        // OCR 결과 처리
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CODE) {
                // 여기에서 결과 처리
                val data = result.data
                if (data != null) {
                    // 결과 데이터를 처리
                    val receivedData = data.getStringExtra(RESULT_KEY)
                    if (receivedData != null) {
                        binding.etEatsExpiry.text = receivedData
                    }
                }
            }
        }

        // 카메라 권한 요청을 처리하는 ActivityResultLauncher를 등록합니다.
        requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용된 경우의 동작
                val intent = Intent(this, OCRScanActivity::class.java)
                startForResult.launch(intent)
                clickCount = 0
            } else {
                // 권한이 거부된 경우의 동작
                when (clickCount) {
                    0, 1 -> {
                        // 처음 클릭 시 처리
                        showCustomToast("카메라 권한을 허용해주세요")
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                        clickCount++
                    }
                    else -> {
                        val openSettingsRunnable = Runnable {
                            openAppSettings(this)
                        }
                        showAlertDialog(2, "카메라 권한을 허용해주세요\n설정으로 이동합니다", "확인", openSettingsRunnable, null)
                    }
                }
            }
        }

        // 이미지 가져오기를 처리하는 ActivityResultLauncher를 등록합니다.
        getImageContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectImageURI: Uri? = data?.data
                if (selectImageURI != null) {
                    val imageView = binding.ivEatsImg
                    imageView.setImageURI(selectImageURI)
                    imageUri = selectImageURI

                    val imagePath = data.data

                    // 이미지 파일로 변환
                    val imageFile = File(absolutelyPath(imagePath, this))

                    // 이미지 파일을 RequestBody로 변환
                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

                    val fileName = getFileNameFromUri(selectImageURI)

                    imageMP = MultipartBody.Part.createFormData("file", fileName, requestBody)

                    showCustomToast("이미지를 불러옵니다")
                } else {
                    showCustomToast("이미지를 가져오지 못했습니다")
                    Log.e(TAG, "image missed ?????")
                }
            } else {
                Log.e(TAG, "EditEatsActivity: ERROR 1234")
            }
        }
    }

    /** normal functions */
    private fun textWatcher() {

        val maxLength = 500
        binding.etEatsInformation.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

        /** edittext의 사이즈를 재주는 메서드 사용 (addTextChangedListener) */
        binding.etEatsInformation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input: String = binding.etEatsInformation.text.toString()
                binding.tvInformationLength.text = input.length.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun pickupDate() {
        val etDate = binding.etEatsExpiry as Button
        etDate.setOnClickListener {
            DatePickerDialog(
                this,
                myDatePicker,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun initAddImageButton() {
        binding.ivEatsImg.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                        // 권한이 존재하는 경우
                        getImageFromAlbum()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        // 권한이 거부되어 있는 경우
                        showCustomToast("갤러리 권한을 허용해주세요")
                    }
                    else -> {
                        // 처음 권한을 시도했을 때 요청
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_FIRST
                        )
                    }
                }
            } else {
                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                        // 권한이 존재하는 경우
                        getImageFromAlbum()
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                        // 권한이 거부 되어 있는 경우
                        showCustomToast("갤러리 권한을 허용해주세요")
                    }

                    else -> {
                        // 처음 권한을 시도했을 때 띄움(
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            REQUEST_FIRST
                        )
                    }
                }
            }
        }
    }

    private fun initSubmitItemButton(v1: Int) {
        // 이후에 예외처리 반드시 해줄 것.
        binding.btnFinish.setOnClickListener {

            val jsonObject = JSONObject()
            val dateWithDots = binding.etEatsExpiry.text.toString().replace(".", "")

            try {
                jsonObject.put("productName", binding.etEatsName.text.toString())
                jsonObject.put("date", dateWithDots)
                jsonObject.put("description", binding.etEatsInformation.text.toString())
                jsonObject.put("refrigeratorId", v1.toLong())
            } catch (e: Exception) {
                Log.e(TAG, "initSubmitItemButton: ${e.message}")
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            // 이미지 파일을 멀티파트로 변환
            if (::imageMP.isInitialized) { // 이미 초기화된 경우
                sendFIles(imageMP, requestBody) // 식품 등록
            } else { // 초기화되지 않은 경우
                showCustomToast("이미지를 선택해주세요") // 이미지가 선택되지 않은 경우 사용자에게 메시지 표시
            }
        }
    }

    private fun initSubmitPatchItemButton(v1: Int) {
        // 이후에 예외처리 반드시 해줄 것.
        binding.btnFinish.setOnClickListener {

            val jsonObject = JSONObject()
            val dateRemoveDots = binding.etEatsExpiry.text.toString().replace(".", "")
            val eatsIdx = prefs.getInt(EATS_IDX, -1)

            try {
                jsonObject.put("productName", binding.etEatsName.text.toString())
                jsonObject.put("date", dateRemoveDots)
                jsonObject.put("description", binding.etEatsInformation.text.toString())
                jsonObject.put("refrigeratorId", v1)
                jsonObject.put("productId", eatsIdx)
            } catch (e: Exception) {
                Log.e(TAG, "initSubmitItemButton: ${e.message}")
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            // 이미지 파일을 멀티파트로 변환
            if (::imageMP.isInitialized) { // 이미지가 선택된 경우
                sendFIles(imageMP, requestBody) // 식품 등록
            } else if (originalImageMP != null) { // 이미지가 선택되지 않았지만 기존 이미지가 있는 경우
                sendFIles(originalImageMP!!, requestBody) // 기존 이미지를 사용하여 식품 등록
            } else { // 이미지가 선택되지 않고 기존 이미지도 없는 경우
                showCustomToast("이미지를 선택해주세요") // 이미지가 선택되지 않은 경우 사용자에게 메시지 표시
            }
        }
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getImageContent.launch(intent)
    }

    private fun sendFIles(file: MultipartBody.Part, product: RequestBody) {
        showLoadingDialog(this)
        try {

            /** API 파싱단계 */
            RetrofitClient.apiService.postFoods(file, product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { res ->
                        val intent = Intent(this, InfoEatsActivity::class.java)
                        intent.putExtra(INTENT_KEY, INTENT_VALUE).toString()
                        setResult(RESULT_OK, intent)
                        showCustomToast("식품 업로드 완료")
                        Log.d(TAG, "EditEatsActivity| sendFIles-postFoods: ${res.message}")
                        dismissLoadingDialog()
                        finish()
                    },
                    { error ->
                        val intent = Intent(this, InfoEatsActivity::class.java)
                        intent.putExtra(INTENT_KEY, INTENT_VALUE).toString()
                        setResult(RESULT_OK, intent)
//                        showCustomToast("식품 업로드 실패")
                        showCustomToast("식품 업로드 완료!")
                        dismissLoadingDialog()
                        finish()
                        errorPage(error, this)
                    })

        } catch (e: Exception) {
            // 기타 예외 처리
            // 예상치 못한 다른 예외 상황을 처리
            Log.e(TAG, "예외 발생: ${e.message}")
            // 사용자에게 오류 메시지 표시 등의 처리 추가
            dismissLoadingDialog()
        }
    }

    // 픽업 이미지 절대경로 변환
    private fun absolutelyPath(path: Uri?, context : Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }

    private fun updateLabel() {
        val myFormat = "yyyy.MM.dd" // 출력형식 : 2018.11.28

        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        val etDate = binding.etEatsExpiry as Button
        etDate.text = sdf.format(myCalendar.time)
    }

    // Uri에서 파일 이름을 추출하는 함수
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                return displayName ?: "image.jpeg"
            }
        }
        return "image.jpeg"
    }

    // 권한 설정 창으로 이동하는 함수
    private fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = context.packageName
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    private fun getEatsInfo(eatsIDX: Int) {
        /** 식품 조회 */
        RetrofitClient.apiService.getFood(eatsIDX)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "EditEatsActivity| getEatsInfo-getFood: ${it.message}")
                try {
                    binding.etEatsName.setText(it.result.productName)
                    binding.etEatsInformation.setText(it.result.description)
                    prefs.setInt(PRODUCTID, it.result.productId)

                    showLoadingDialog(this)
                    Glide.with(applicationContext)
                        .load(it.result.productImg)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                // 이미지 로딩이 완료되면 호출되는 콜백
                                binding.ivEatsImg.setImageDrawable(resource)
                                dismissLoadingDialog() // 이미지 로딩이 완료되면 로딩 바를 숨김
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // 이미지 로딩이 취소되었을 때 호출되는 콜백
                                // 여기에서도 로딩 바를 숨기거나 관련 작업을 수행할 수 있습니다.
                            }
                        })
                    dismissLoadingDialog()

                    // 숫자가 8자리인 경우 (YYYYMMDD 형식)
                    if (it.result.date.length == 8) {
                        val result1 = it.result.date.substring(0, 4)
                        val result2 = it.result.date.substring(4, 6)
                        val result3 = it.result.date.substring(6, 8)
                        val veryResult = "$result1.$result2.$result3"
                        binding.etEatsExpiry.text = veryResult

//                        Log.d(TAG, "getEatsInfo: \n\tproductName ${it.result.productName}" +
//                                "\n\tproductInformation ${it.result.description}\n\tproductDate $veryResult" +
//                                "\n\tproductImage ${it.result.productImg}")

                        // Glide를 사용하여 이미지 다운로드 및 저장
                        Glide.with(this@EditEatsActivity)
                            .load(it.result.productImg)
                            .downloadOnly(object : SimpleTarget<File>() {
                                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                    // 저장된 이미지 파일을 가져온 후 멀티파트로 변환
                                    val imageFile = File(resource.path)
                                    val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                                    val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

                                    // 이제 imagePart를 사용하여 서버에 업로드할 수 있습니다.
                                    originalImageMP = imagePart
                                }
                            })

                    } else {
                        // 숫자가 8자리가 아닌 경우 잘못된 형식이라고 판단
                        Log.e(TAG, "getEatsInfo: 숫자가 8자리가 아님")
                    }

                }catch(e: Exception){
                    Log.e(TAG,"error: $e")
                }

            }, { error ->
                errorPage(error, this)
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        prefs.getInt(EATS_IDX, -1)
    }
}