package com.luojunkai.app

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.luojunkai.app.databinding.ActivityInformationBinding // 引入 View Binding 类
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Environment
import android.util.Log
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.luojunkai.app.user.User
import com.luojunkai.app.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationBinding
    private lateinit var commitButton: Button // 添加 commitButton 变量
    private var capturedImageUri: Uri? = null // 添加 capturedImageUri 变量
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                binding.avatar.setImageURI(imageUri)
                capturedImageUri = imageUri // 将选择或拍照得到的图片URI保存到 capturedImageUri 变量中
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 通过 ID 查找 commitButton
        commitButton = findViewById(R.id.informationcommitbtn)

        // 设置头像 ImageView 的点击事件
        binding.avatar.setOnClickListener {
            // 在此处处理选择头像的逻辑
            showImageSelectionDialog()
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

        // 获取从AddNewsActivity传递的数据
        val nickname = intent.getStringExtra("nickname")
        val avatarUrl = intent.getStringExtra("avatarUrl")

        // 将头像URL显示在头像ImageView上
        if (avatarUrl != null && avatarUrl.isNotEmpty()) {
            val imageUri = Uri.parse(avatarUrl)
            binding.avatar.setImageURI(imageUri)
            capturedImageUri = imageUri // 将头像URL赋值给capturedImageUri
        }
    }

    // 添加请求拍照或选择照片的常量
    private val REQUEST_IMAGE_PICK = 102
    private val REQUEST_IMAGE_CAPTURE = 103

    // 添加头像选择的逻辑
    private fun showImageSelectionDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
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
        takePictureLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                val photoFile: File? = createImageFile()
                photoFile?.let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.luojunkai.app.fileprovider",  // 这里需要替换为你的 FileProvider authority
                        it
                    )
                    capturedImageUri = photoURI // 设置拍摄的照片URI给 capturedImageUri 变量

                    takePictureLauncher.launch(intent)
                }
            } catch (ex: IOException) {
                // 处理文件创建失败的情况
                Log.e("InformationActivity", "Error creating image file", ex)
            }
        } else {
            Log.d("InformationActivity", "No camera app found")
        }
    }




    // 创建用于保存照片的临时文件
    private fun createImageFile(): File {
        // 创建文件名称，可以根据需要定义
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 用户授予了相机和存储权限
                selectImageFromCameraOrGallery()
            } else {
                // 用户拒绝了相机或存储权限，你可以在这里给出一个提示或其他处理
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // 处理从相机拍照的图片
                val imageUri: Uri? = capturedImageUri
                // 在这里检查 capturedImageUri 的值，确保图片的URI正确保存
                Log.d("InformationActivity", "Captured image URI: $imageUri")

                // 在这里您可以将头像设置为拍摄的图片
                if (imageUri != null) {
                    // 将图片的 URI 设置给 capturedImageUri 变量
                    capturedImageUri = imageUri

                    // 将头像地址保存到 Room 数据库
                    lifecycleScope.launch(Dispatchers.IO) {
                        val userDatabase = UserDatabase.getDatabase(applicationContext)
                        val userId = 1 // 假设用户ID为1
                        val user = userDatabase.userDao().getUser(userId)
                        user?.let {
                            val updatedUser = User(it.uid, 0, imageUri.toString(), it.nickname)
                            userDatabase.userDao().insertOrUpdateUser(updatedUser)
                        }
                    }

                    // 设置头像图片
                    binding.avatar.setImageURI(imageUri)
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // 处理从相册选择的图片
                val imageUri: Uri? = data?.data
                // 在这里检查 imageUri 的值，确保图片的URI正确保存
                Log.d("InformationActivity", "Selected image URI: $imageUri")

                // 在这里您可以将头像设置为选择的图片
                if (imageUri != null) {
                    // 将图片的 URI 设置给 capturedImageUri 变量
                    capturedImageUri = imageUri

                    // 将头像地址保存到 Room 数据库
                    lifecycleScope.launch(Dispatchers.IO) {
                        val userDatabase = UserDatabase.getDatabase(applicationContext)
                        val userId = 1 // 假设用户ID为1
                        val user = userDatabase.userDao().getUser(userId)
                        user?.let {
                            val updatedUser = User(it.uid, 0, imageUri.toString(), it.nickname)
                            userDatabase.userDao().insertOrUpdateUser(updatedUser)
                        }
                    }

                    // 设置头像图片
                    binding.avatar.setImageURI(imageUri)
                }
            }
        }
    }
}