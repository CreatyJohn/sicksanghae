package com.expiry.template.kotlin.src.main.myPage

import android.Manifest
import android.annotation.SuppressLint
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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.expiry.template.kotlin.R
import com.expiry.template.kotlin.config.BaseActivity
import com.expiry.template.kotlin.databinding.ActivityUploadUsersBinding
import com.expiry.template.kotlin.src.main.MainActivity
import com.expiry.template.kotlin.src.reqres.RetrofitClient
import com.expiry.template.kotlin.util.Constants.errorPage
import com.expiry.template.kotlin.util.Constants.navigationHeight
import com.expiry.template.kotlin.util.JWT
import com.expiry.template.kotlin.util.NULL
import com.expiry.template.kotlin.util.PreferenceUtil
import com.expiry.template.kotlin.util.REQUEST_FIRST
import com.expiry.template.kotlin.util.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CheckResult", "SetTextI18n", "MissingSuperCall", "Range", "Recycle")
class UploadUsersActivity : BaseActivity<ActivityUploadUsersBinding>(ActivityUploadUsersBinding::inflate) {

    private lateinit var getImageContent: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private lateinit var imageMP: MultipartBody.Part
    private var originalImageMP: MultipartBody.Part? = null // 기존 이미지를 저장할 변수
    //    private var clickCount = 0

    private lateinit var jwtData: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.clParentView.setPadding(0,0,0,this.navigationHeight())

        prefs = PreferenceUtil(applicationContext)
        jwtData = prefs.getString(JWT, NULL)

        getUsersInfo(jwtData)

        if (jwtData.isNotEmpty()) {
            binding.etNickname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val isTextEmpty = s.isNullOrEmpty()

                    if (!isTextEmpty) {
                        // 텍스트가 비어 있지 않으면 버튼 활성화
                        binding.btnEnter.setBackgroundResource(R.drawable.radius4_background)
                        binding.btnEnter.isEnabled = true

                        binding.btnEnter.setOnClickListener {
                            saveUsersInfo()
                        }
                    } else {
                        // 텍스트가 비어 있으면 버튼 비활성화
                        binding.btnEnter.setBackgroundResource(R.drawable.radius4_background_stop)
                        binding.btnEnter.isEnabled = false
                        showCustomToast("닉네임을 적어주세요")
                        binding.btnEnter.setOnClickListener { showCustomToast("닉네임을 적어주세요") }
                    }
                }
            })
        }

        binding.ivSetImg.setOnClickListener { initAddImageButton() }
        binding.btnGoBack.setOnClickListener { finish() }

        // 이미지 가져오기를 처리하는 ActivityResultLauncher를 등록합니다.
        getImageContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectImageURI: Uri? = data?.data
                if (selectImageURI != null) {
                    val imageView = binding.ivSetImg
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
                Log.e(TAG, "EditEatsActivity: ERROR 159")
            }
        }
    }

    fun saveUsersInfo () {
        /** 수정 된, 유저정보 저장하기 */
        val nameValue = binding.etNickname.text.toString()
        val nameRequestBody = nameValue.toRequestBody(MultipartBody.FORM)

        if (::imageMP.isInitialized) { // 이미지가 선택된 경우
            sendFiles(jwtData, imageMP, nameRequestBody) // 식품 등록
        } else if (originalImageMP != null) { // 이미지가 선택되지 않았지만 기존 이미지가 있는 경우
            sendFiles(jwtData, originalImageMP!!, nameRequestBody) // 기존 이미지를 사용하여 식품 등록
        } else { // 이미지가 선택되지 않고 기존 이미지도 없는 경우
            showCustomToast("이미지를 선택해주세요") // 이미지가 선택되지 않은 경우 사용자에게 메시지 표시
        }
    }

    private fun sendFiles(jwt: String, file: MultipartBody.Part, product: RequestBody) {
        showLoadingDialog(this)
        RetrofitClient.apiService.uploadUsersInfo(jwt, file, product)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "UploadUsersActivity| sendFiles-uploadUsersInfo: ${it.message}")
                showCustomToast("닉네임 설정 성공")
                dismissLoadingDialog()
                startMainActivity()
            }, { error ->
                errorPage(error, this)
                showCustomToast("닉네임 설정 실패")
                dismissLoadingDialog()
            })
    }

    /** 이미지 넣기 */
    private fun initAddImageButton() {
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

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getImageContent.launch(intent)
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

    private fun getUsersInfo(jwt: String) {
        RetrofitClient.apiService.getUsers(jwt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                Log.d(TAG, "UploadUsersActivity| getUsersInfo-getUsers: ${it.message}")
                try {
                    showLoadingDialog(this)
                    binding.etNickname.setText(it.result.name)
                    Glide.with(this@UploadUsersActivity)
                        .load(it.result.profileImg)
                        .listener(object: RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                dismissLoadingDialog()
                                return false
                            }

                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                dismissLoadingDialog()
                                return false
                            }
                        })
                        .into(binding.ivSetImg)

                    // Glide를 사용하여 이미지 다운로드 및 저장
                    Glide.with(this@UploadUsersActivity)
                        .load(it.result.profileImg)
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

                    dismissLoadingDialog()

                }catch(e: Exception){
                    errorPage(e, this)
                }

            }, { error ->
                errorPage(error, this)
            })
    }
    /** 여까지 */

    private fun startMainActivity() {
        val intent = Intent(this@UploadUsersActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}