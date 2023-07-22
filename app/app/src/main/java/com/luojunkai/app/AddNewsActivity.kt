package com.luojunkai.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*

class AddNewsActivity : AppCompatActivity() {
    private val REQUEST_SELECT_IMAGE = 100
    private lateinit var mantleImage: ImageView
    private var selectedImageView: ImageView? = null
    private var capturedImageUri: Uri? = null //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnews_activity)
        // 获取返回图标按钮
        val backButton: ImageButton = findViewById(R.id.backButton)
        // 获取提交按钮
        val commitButton: Button = findViewById(R.id.commitbtn)
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
            openGallery() // 修改为打开相册
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

    // 新增的函数：打开相册
    private fun openGallery() {
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
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null && selectedImageView != null) {
                selectedImageView!!.setImageURI(selectedImageUri)
                capturedImageUri = selectedImageUri // 将相册选择的图片URI赋值给capturedImageUri
            }
        } else if (resultCode == Activity.RESULT_OK) {
            // 将图片显示在mantleImage上
            capturedImageUri?.let { uri ->
                selectedImageView?.setImageURI(uri)
            }
        }
    }

    private fun getMantleImageUri(): String {
        // 这里根据实际情况获取mantleimage的Uri，并返回Uri对应的字符串
        // 例如，如果mantleimage是ImageView类型的，可以使用mantleImage.getTag()获取Uri字符串
        // 如果使用其他方式加载图片，请根据实际情况获取mantleimage的Uri
        return ""
    }
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("title", findViewById<EditText>(R.id.edittitle).text.toString())
        intent.putExtra("from", findViewById<EditText>(R.id.editfrom).text.toString())
        intent.putExtra("mantleImageUrl", getMantleImageUri())
        intent.putExtra("content", findViewById<EditText>(R.id.editcontent).text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}