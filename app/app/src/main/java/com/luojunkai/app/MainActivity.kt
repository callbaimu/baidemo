package com.luojunkai.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.luojunkai.app.home.HomeFragment
import com.luojunkai.app.user.UserFragment
import com.luojunkai.app.video.VideoFragment

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var homeFragment: HomeFragment
    private lateinit var videoFragment: VideoFragment
    private lateinit var userFragment: UserFragment

    private lateinit var homeLayout: RelativeLayout
    private lateinit var videoLayout: RelativeLayout
    private lateinit var userLayout: RelativeLayout

    private lateinit var homeText: TextView
    private lateinit var homeIcon: ImageView
    private lateinit var videoText: TextView
    private lateinit var videoIcon: ImageView
    private lateinit var userText: TextView
    private lateinit var userIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager

        // 恢复之前保存的Fragment实例，如果有的话
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            videoFragment = VideoFragment()
            userFragment = UserFragment()
        } else {
            homeFragment = fragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as? HomeFragment
                ?: HomeFragment()
            videoFragment = fragmentManager.findFragmentByTag(VideoFragment::class.java.simpleName) as? VideoFragment
                ?: VideoFragment()
            userFragment = fragmentManager.findFragmentByTag(UserFragment::class.java.simpleName) as? UserFragment
                ?: UserFragment()
        }

        // 初始化视图
        initView()

        // 设置默认显示的 Fragment
        showFragment(homeFragment)
        setSelectedNavItem(homeText, homeIcon)

        // 导航栏点击事件监听
        homeLayout.setOnClickListener {
            showFragment(homeFragment)
            setSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(videoText, videoIcon)
            clearSelectedNavItem(userText, userIcon)
        }
        videoLayout.setOnClickListener {
            showFragment(videoFragment)
            setSelectedNavItem(videoText, videoIcon)
            clearSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(userText, userIcon)
        }
        userLayout.setOnClickListener {
            showFragment(userFragment)
            setSelectedNavItem(userText, userIcon)
            clearSelectedNavItem(homeText, homeIcon)
            clearSelectedNavItem(videoText, videoIcon)
        }
    }

    private fun initView() {
        homeLayout = findViewById(R.id.home)
        videoLayout = findViewById(R.id.video)
        userLayout = findViewById(R.id.user)
        homeText = findViewById(R.id.hometext)
        homeIcon = findViewById(R.id.homeicon)
        videoText = findViewById(R.id.videotext)
        videoIcon = findViewById(R.id.videoicon)
        userText = findViewById(R.id.usertext)
        userIcon = findViewById(R.id.usericon)
    }

    private fun showFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        hideFragments(transaction)
        if (!fragment.isAdded) {
            transaction.add(R.id.fragment, fragment, fragment::class.java.simpleName)
        } else {
            transaction.show(fragment)
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        if (::homeFragment.isInitialized && homeFragment.isAdded) {
            transaction.hide(homeFragment)
        }
        if (::videoFragment.isInitialized && videoFragment.isAdded) {
            transaction.hide(videoFragment)
        }
        if (::userFragment.isInitialized && userFragment.isAdded) {
            transaction.hide(userFragment)
        }
    }

    private fun setSelectedNavItem(textView: TextView, imageView: ImageView) {
        textView.isSelected = true
        imageView.isSelected = true
    }

    private fun clearSelectedNavItem(textView: TextView, imageView: ImageView) {
        textView.isSelected = false
        imageView.isSelected = false
    }
}
