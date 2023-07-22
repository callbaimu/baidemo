package com.luojunkai.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.luojunkai.app.databinding.FragmentUserBinding // 引入 View Binding 类
import com.bumptech.glide.Glide
import com.luojunkai.app.user.User
import com.luojunkai.app.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var avatarUrl: String? = null
    private lateinit var nicknameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 在 onViewCreated 中通过 view 来查找 nicknameTextView
        nicknameTextView = view.findViewById(R.id.nicknameTextView)

        // 设置信息 View 的点击事件
        binding.information.setOnClickListener {
            val intent = Intent(activity, InformationActivity::class.java)
            startActivityForResult(intent, REQUEST_INFORMATION)
        }

        // 获取数据库实例
        val userDatabase = UserDatabase.getDatabase(requireContext())

        // 通过协程在后台线程中获取用户的昵称和头像
        lifecycleScope.launch(Dispatchers.IO) {
            val userDatabase = UserDatabase.getDatabase(requireContext())
            val userId = 1 // 假设用户ID为1
            val user = userDatabase.userDao().getUser(userId)
            avatarUrl = user?.avatarUrl
            withContext(Dispatchers.Main) {
                // 如果用户信息存在，则获取昵称
                val nickname = user?.nickname ?: "默认昵称"
                nicknameTextView.text = nickname
                loadImage(avatarUrl)
            }
        }
    }


    // 添加 loadImage 方法
    private fun loadImage(url: String?) {
        // 使用 Glide 加载头像图片，确保 avatarUrl 不为 null
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(url))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.avatarImageView)
        } else {
            // 当 avatarUrl 为 null 或为空字符串时，加载默认图片
            Glide.with(this)
                .load(R.drawable.error_image) // 这里设置默认头像的资源ID
                .into(binding.avatarImageView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INFORMATION && resultCode == Activity.RESULT_OK && data != null) {
            val nickname = data.getStringExtra("nickname")
            val newAvatarUrl = data.getStringExtra("avatarUrl")

            // 更新或插入用户数据
            val userId = 1 // 假设用户ID为1
            lifecycleScope.launch(Dispatchers.IO) {
                val userDatabase = UserDatabase.getDatabase(requireContext())
                val user = userDatabase.userDao().getUser(userId)

                val updatedUser = User(userId, user?.avatar ?: 0, newAvatarUrl, nickname ?: "")
                userDatabase.userDao().insertOrUpdateUser(updatedUser)

                withContext(Dispatchers.Main) {
                    // 更新 UI 显示新的昵称和头像
                    binding.nicknameTextView.text = nickname ?: "默认昵称"
                    loadImage(newAvatarUrl)
                }
            }
        }
    }
    companion object {
        private const val REQUEST_INFORMATION = 1
    }
}