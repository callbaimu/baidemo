package com.luojunkai.app.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.luojunkai.app.databinding.FragmentUserBinding // 引入 View Binding 类
import com.bumptech.glide.Glide
import com.luojunkai.app.user.User.InformationActivity
import com.luojunkai.app.R
import com.luojunkai.app.user.User.User

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var avatarUrl: String? = null
    private lateinit var nicknameTextView: TextView
    private val REQUEST_INFORMATION = 102
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nicknameTextView = view.findViewById(R.id.nicknameTextView)

        binding.information.setOnClickListener {
            val intent = Intent(activity, InformationActivity::class.java)
            intent.putExtra("avatarUrl", avatarUrl)
            startActivityForResult(intent, REQUEST_INFORMATION)
        }

        viewModel.getUserLiveData().observe(viewLifecycleOwner, Observer { user ->
            avatarUrl = user?.avatarUrl
            val nickname = user?.nickname ?: "默认昵称"
            nicknameTextView.text = nickname
            loadImage(avatarUrl)
        })
    }

    private fun loadImage(url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(url))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.avatarImageView)
        } else {
            Glide.with(this)
                .load(R.drawable.error_image)
                .into(binding.avatarImageView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INFORMATION && resultCode == Activity.RESULT_OK && data != null) {
            val nickname = data.getStringExtra("nickname")
            val newAvatarUrl = data.getStringExtra("avatarUrl")

            val user = viewModel.getUserLiveData().value
            val updatedUser = User(user?.uid ?: 1, user?.avatar, newAvatarUrl, nickname ?: "")
            viewModel.updateUser(updatedUser)

            loadImage(newAvatarUrl)
            nicknameTextView.text = nickname ?: "默认昵称"
        }
    }
}