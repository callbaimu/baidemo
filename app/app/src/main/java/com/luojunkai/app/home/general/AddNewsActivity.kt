package com.luojunkai.app.home.general

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*
import android.Manifest
import com.luojunkai.app.MainActivity
import com.luojunkai.app.R


class AddNewsActivity : AppCompatActivity() {
    private val REQUEST_SELECT_IMAGE = 100
    private lateinit var mantleImage: ImageView
    private var selectedImageView: ImageView? = null
    private var capturedImageUri: Uri? = null
    private lateinit var commitButton: Button


    // 检查相机和访问文件（使用相册）的权限
    private fun checkCameraAndStoragePermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionsToRequest = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // 请求权限
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS)
        } else {
            // 权限已经被授予，执行打开相册或相机逻辑
            openGalleryorCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnews_activity)
        // 获取返回图标按钮
        val backButton: ImageButton = findViewById(R.id.newsbackbtn)

        commitButton = findViewById(R.id.commitbtn)

        // 设置返回点击事件监听器
        backButton.setOnClickListener {
            // 创建 Intent 并指定返回到 MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // 添加标志位，用于让 MainActivity 跳转到 fragment_home
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        mantleImage = findViewById(R.id.mantleimage)
        mantleImage.setOnClickListener {
            selectedImageView = mantleImage
            // 在此处调用检查相机和访问文件（使用相册）的权限方法
            checkCameraAndStoragePermissions()
        }
        // 设置提交点击事件监听器
        commitButton.setOnClickListener {
            // 获取edittitle、editfrom、mantleimage、editcontent中的内容
            val title = findViewById<EditText>(R.id.edittitle).text.toString()
            val from = findViewById<EditText>(R.id.editfrom).text.toString()
            val content = findViewById<EditText>(R.id.editcontent).text.toString()

            // 创建Intent并添加数据
            val resultIntent = Intent()
            resultIntent.putExtra("title", title)
            resultIntent.putExtra("from", from)
            resultIntent.putExtra("mantleImageUrl", capturedImageUri?.toString()) // 将选择图片的URI添加到Intent中
            resultIntent.putExtra("content", content)

            // 设置Result，并返回数据给上一个Activity（MainActivity）
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    // 打开相册或相机
    private fun openGalleryorCamera() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (captureImageIntent.resolveActivity(packageManager) != null) {
            capturedImageUri = createImageUri()
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
            val chooserIntent = Intent.createChooser(intent, "选择图片")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureImageIntent))
            startActivityForResult(chooserIntent, REQUEST_SELECT_IMAGE)
        } else {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE)
        }
    }

    // 新增的函数：创建图片URI
    private fun createImageUri(): Uri? {
        val imageFileName =
            "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return storageDir?.let {
            File.createTempFile(imageFileName, ".jpg", it).let { file ->
                FileProvider.getUriForFile(
                    this@AddNewsActivity,
                    "${packageName}.fileprovider",
                    file
                )
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.data != null) {
                    // 从相册选择的情况
                    val selectedImageUri: Uri? = data.data
                    if (selectedImageUri != null) {
                        selectedImageView?.setImageURI(selectedImageUri)
                        capturedImageUri = selectedImageUri
                    }
                } else {
                    // 从相机拍摄的情况，将保存的文件路径显示在mantleImage上
                    capturedImageUri?.let { uri ->
                        selectedImageView?.setImageURI(uri)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED && capturedImageUri != null) {
                // 用户取消拍照时，删除未保存的照片
                contentResolver.delete(capturedImageUri!!, null, null)
                capturedImageUri = null
            }
        }
    }

    override fun onBackPressed() {
        commitButton.performClick()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 101
    }
}