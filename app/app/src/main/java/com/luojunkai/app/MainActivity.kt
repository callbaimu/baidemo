package com.luojunkai.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    // 其他代码

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home = findViewById<RelativeLayout>(R.id.home)
        val video = findViewById<RelativeLayout>(R.id.video)
        val user = findViewById<RelativeLayout>(R.id.user)
        val homeText = findViewById<TextView>(R.id.hometext)
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val videoText = findViewById<TextView>(R.id.videotext)
        val videoIcon = findViewById<ImageView>(R.id.videoicon)
        val userText = findViewById<TextView>(R.id.usertext)
        val userIcon = findViewById<ImageView>(R.id.usericon)

        // 设置默认显示的 Fragment
        showFragment(HomeFragment())
        setSelectedNavItem(homeText, homeIcon)

        // 导航栏点击事件监听
        home.setOnClickListener {
            showFragment(HomeFragment())
            setSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(videoText, videoIcon)
            clearSelectedNavItem(userText, userIcon)
        }
        video.setOnClickListener {
            showFragment(VideoFragment())
            setSelectedNavItem(videoText, videoIcon)
            clearSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(userText, userIcon)
        }
        user.setOnClickListener {
            showFragment(UserFragment())
            setSelectedNavItem(userText, userIcon)
            clearSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(videoText, videoIcon)
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
    // 其他代码

    private fun setSelectedNavItem(textView: TextView, imageView: ImageView) {
        textView.isSelected = true
        imageView.isSelected = true
    }

    private fun clearSelectedNavItem(textView: TextView, imageView: ImageView) {
        textView.isSelected = false
        imageView.isSelected = false
    }
}