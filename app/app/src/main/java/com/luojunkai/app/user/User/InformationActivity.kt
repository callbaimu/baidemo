package com.luojunkai.app.user.User

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.luojunkai.app.databinding.ActivityInformationBinding
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.luojunkai.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InformationActivity : AppCompatActivity() {

    // 声明一个协程的 Deferred 对象用于在 onActivityResult 中执行协程任务
    private var imageSavingJob: Deferred<Unit>? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private lateinit var binding: ActivityInformationBinding
    private lateinit var commitButton: Button // 添加 commitButton 变量
    private lateinit var avatar: ImageView
    private lateinit var capturedImageUri: Uri
    private var oldAvatarUri: Uri? = null
    private val imageWidth = 512 // 定义较小的图片宽度，用于加载到 ImageView 中
    private val requiredPermissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        avatar = findViewById(R.id.avatar)
        // 通过 ID 查找 commitButton
        commitButton = findViewById(R.id.informationcommitbtn)

        // 设置头像 ImageView 的点击事件
        binding.avatar.setOnClickListener {
            // 在此处处理选择头像的逻辑
            showImageSelectionDialog()
        }

        val oldavatarUrl = intent.getStringExtra("avatarUrl")
        Log.d("InformationActivity", "Received oldAvatarUrl: $oldavatarUrl")
        if (oldavatarUrl != null) {
            oldAvatarUri = Uri.parse(oldavatarUrl)
        } else {
            Log.e("debug","no old avatar")
        }

        // 设置 commitButton 的点击监听器
        commitButton.setOnClickListener {
            val newNickname = binding.editnick.text.toString().trim()
            if (newNickname.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("nickname", newNickname)
                resultIntent.putExtra("avatarUrl", capturedImageUri?.toString() ?: "")
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }


    // 添加请求拍照或选择照片的常量
    private val REQUEST_IMAGE_PICK = 102
    private val REQUEST_IMAGE_CAPTURE = 103

    // 添加头像选择的逻辑
    private fun showImageSelectionDialog() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 已经有权限，执行拍照或选择照片逻辑
            selectImageFromCameraOrGallery()
        } else {
            // 请求相机和存储权限
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_IMAGE_PICK)
        }
    }

    private fun selectImageFromCameraOrGallery() {
        val imageSelectionDialog = AlertDialog.Builder(this)
            .setTitle("选择头像")
            .setMessage("请选择获取头像的方式")
            .setPositiveButton("拍照") { _, _ ->
                openCamera()
            }
            .setNegativeButton("相册") { _, _ ->
                openGallery()
            }
            .create()

        imageSelectionDialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            // 创建用于保存照片的临时文件
            val photoFile: File? = try {
                createImageFileInAvatar()
            } catch (ex: IOException) {
                // 处理文件创建失败的情况
                null
            }

            // 继续只有当文件创建成功时，才进行拍照
            photoFile?.let {
                // 创建文件的父目录
                it.parentFile?.mkdirs()

                // 将图片的URI设置给不同的provider路径
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.luojunkai.app.fileprovider",
                    it
                )

                capturedImageUri = photoURI // 将图片的 URI 设置给 capturedImageUri 变量
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }

        } else {
            Log.e("DEBUG", "No camera app found")
        }
    }

    private fun createImageFileInAvatar(): File {
        val imageFileName =
            "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val avatarDir = File(storageDir, "provider.avatar") // 新增创建存放头像的目录
        avatarDir.mkdirs()

        return File.createTempFile(imageFileName, ".jpg", avatarDir)
    }


    private fun loadImageIntoAvatar(imageUri: Uri?) {
        if (imageUri != null) {
            // 使用 Glide 将图片以较小的尺寸加载到 ImageView 中
            Glide.with(this)
                .load(imageUri)
                .override(imageWidth)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.avatar)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 用户授予了相机和存储权限
                selectImageFromCameraOrGallery()
            } else {
            }
        }
    }

    private fun isNewAvatarFromCameraOrGallery(): Boolean {
        return capturedImageUri.scheme != "file"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        loadImageIntoAvatar(imageUri)

                        if (isNewAvatarFromCameraOrGallery()) {
                            deleteOldFile()
                        }

                        // 取消之前的任务
                        imageSavingJob?.cancel()

                        // 手动创建一个 Deferred 对象，并赋值给 imageSavingJob
                        imageSavingJob = coroutineScope.async {
                            mutex.withLock {
                                val userDatabase = UserDatabase.getDatabase(applicationContext)
                                val userId = 1 // 假设用户ID为1
                                val user = userDatabase.userDao().getUser(userId)
                                user?.let {
                                    val updatedUser = User(it.uid, 0, imageUri.toString(), it.nickname)
                                    userDatabase.userDao().insertOrUpdateUser(updatedUser)
                                }
                            }
                        }
                    }
                }

                REQUEST_IMAGE_CAPTURE -> {
                    // 从相机拍摄的情况
                    // 使用 capturedImageUri 获取拍照后的图片 URI
                    if (capturedImageUri != null) {
                        // 使用 Glide 以较小的尺寸加载图片
                        loadImageIntoAvatar(capturedImageUri)

                        if (isNewAvatarFromCameraOrGallery()) {
                            deleteOldFile()
                        }

                        // 取消之前的任务
                        imageSavingJob?.cancel()

                        // 手动创建一个 Deferred 对象，并赋值给 imageSavingJob
                        imageSavingJob = coroutineScope.async {
                            mutex.withLock {
                                val userDatabase = UserDatabase.getDatabase(applicationContext)
                                val userId = 1 // 假设用户ID为1
                                val user = userDatabase.userDao().getUser(userId)
                                user?.let {
                                    val updatedUser = User(it.uid, 0, capturedImageUri.toString(), it.nickname)
                                    userDatabase.userDao().insertOrUpdateUser(updatedUser)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // 删除旧文件
    // 修改 deleteOldFile() 方法使用 currentPhotoPath 来删除旧文件
    private fun deleteOldFile() {
        oldAvatarUri?.let { oldUri ->
            val contentResolver = contentResolver
            try {
                // 使用 ContentResolver 删除文件
                contentResolver.delete(oldUri, null, null)
                Log.d("DEBUG", "Old avatar file deleted successfully: $oldUri")
            } catch (e: SecurityException) {
                // 如果没有权限删除文件，则输出错误日志
                Log.e("DEBUG", "Failed to delete old avatar file: $oldUri")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消协程任务，确保在 Activity 销毁时不会再执行协程任务
        imageSavingJob?.cancel()
    }
}