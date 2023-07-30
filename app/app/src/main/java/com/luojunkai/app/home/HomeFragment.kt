package com.luojunkai.app.home

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luojunkai.app.home.general.AddNewsActivity
import com.luojunkai.app.R
import com.luojunkai.app.home.weather.WeatherActivity
import com.luojunkai.app.home.web.WebSearchActivity
import com.luojunkai.app.home.general.general
import com.luojunkai.app.home.general.generalAdapter
import com.luojunkai.app.home.general.generalDao
import com.luojunkai.app.home.general.generalDatabase
import com.luojunkai.app.home.key.key
import com.luojunkai.app.home.key.keyAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private val keylist = ArrayList<key>()
    private val generallist = ArrayList<general>()
    private lateinit var logo: ImageView
    private lateinit var msetting: ImageView
    private var generalAdapter: generalAdapter? = null
    private lateinit var generalDao: generalDao
    private val ADD_NEWS_REQUEST_CODE = 101


    // 使用viewModels()函数创建ViewModel实例
    private val homeViewModel: HomeViewModel by viewModels()
    private var inputMethodManager: InputMethodManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val i_view = view.findViewById<ImageView>(R.id.weatherimage)
        i_view.alpha = 0f
        val fadeInAnimation = ObjectAnimator.ofFloat(i_view, "alpha", 0f, 1f)
        fadeInAnimation.duration = 5000
        fadeInAnimation.start()
        val loadingDuration: Long = 300
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        i_view.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                loadingProgressBar.visibility = View.GONE
                val intent = Intent(requireContext(), WeatherActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }, loadingDuration)
        }

        // 处理搜索逻辑
        val textInputEditText = view.findViewById<EditText>(R.id.textInputEditText)
        val searchmagnifier = view.findViewById<ImageView>(R.id.searchmagnifier)
        searchmagnifier.setOnClickListener {
            val searchText = textInputEditText.text.toString().trim()
            if (searchText.isNotEmpty()) {
                // 跳转到WebSearchActivity，并传递搜索内容和默认的page为2
                val intent = Intent(requireContext(), WebSearchActivity::class.java)
                intent.putExtra("search_text", searchText)
                intent.putExtra("page", 2)
                startActivity(intent)
            } else {
                // 直接跳转到百度首页（page为1）
                val intent = Intent(requireContext(), WebSearchActivity::class.java)
                intent.putExtra("page", 1)
                startActivity(intent)
            }
            // 清空输入框内容
            textInputEditText.text = null
        }

        // 初始化keylist
        initkey()
        val keylayoutManager = LinearLayoutManager(requireContext())
        val keyrecyclerView: RecyclerView = view.findViewById(R.id.hotspot)
        keyrecyclerView.layoutManager = keylayoutManager
        keyrecyclerView.adapter = keyAdapter(keylist)

        // 获取GeneralDatabase实例
        val generalDatabase = generalDatabase.getDatabase(requireContext())
        generalDao = generalDatabase.generalDao()

        // 初始化generallist并从数据库中加载数据
        initgeneral()
        generalAdapter = generalAdapter(generallist, generalDao)
        val generallayoutManager = LinearLayoutManager(requireContext())
        val generalrecyclerView: RecyclerView = view.findViewById(R.id.general)
        generalrecyclerView.layoutManager = generallayoutManager
        generalrecyclerView.adapter = generalAdapter

        logo = view.findViewById(R.id.logo)
        logo.setOnClickListener {
            onImageClick()
        }

        msetting = view.findViewById(R.id.msetting)
        msetting.setOnClickListener {
            showPopupMenu()
        }

        // 观察天气信息的LiveData并更新UI
        homeViewModel.weatherInfo.observe(viewLifecycleOwner, Observer { weather ->
            // 在这里更新天气信息的UI
            if (weather != null) {
                // 假设界面上有对应的TextView用于显示天气信息
                val cityTextView = view?.findViewById<TextView>(R.id.city)
                val weatherTextView = view?.findViewById<TextView>(R.id.weather)
                val airTextView = view?.findViewById<TextView>(R.id.air)
                val airqualityTextView = view?.findViewById<TextView>(R.id.airquality)
                val temperatureTextView = view?.findViewById<TextView>(R.id.temperature)

                // 将LiveData中的天气信息显示到对应的TextView上
                cityTextView?.text = weather.city
                weatherTextView?.text = weather.weather
                airTextView?.text = weather.air
                airqualityTextView?.text = weather.airQuality
                temperatureTextView?.text = weather.temperature
            }
        })
        // 观察新闻列表的LiveData并更新UI
        homeViewModel.newsList.observe(viewLifecycleOwner, Observer { newsList ->
            // 在这里更新新闻列表UI
        })

        // 初始化 InputMethodManager
        inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        // 设置根布局的触摸监听器，当点击搜索栏以外的区域时隐藏键盘
        view?.setOnClickListener {
            hideSoftKeyboard()
        }

        return view
    }

    private fun hideSoftKeyboard() {
        val currentFocus = activity?.currentFocus
        if (currentFocus != null) {
            inputMethodManager?.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }


    // 初始化keylist
    private fun initkey() {
        repeat(1) {
            keylist.add(key("重要内容11", "置顶", "XXX新闻"))
            keylist.add(key("重要内容22", "置顶", "XXX社"))
        }
    }

    // 初始化generallist，并从数据库中加载数据
    private fun initgeneral() {
        // 从数据库中加载数据到 generallist，仅加载一次数据
        if (generallist.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val generalListFromDB = generalDao.getAllGenerals()
                generallist.clear()
                generallist.addAll(generalListFromDB)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }


    private fun deleteSpecificGeneral() {
        val inputEditText = EditText(requireContext())
        inputEditText.hint = "输入指定ID"
        AlertDialog.Builder(requireContext())
            .setTitle("删除指定新闻")
            .setMessage("请输入要删除的新闻ID:")
            .setView(inputEditText)
            .setPositiveButton("确定") { _, _ ->
                val idToDelete = inputEditText.text.toString().toIntOrNull()
                if (idToDelete != null) {
                    val generalToDelete = generallist.find { it.id == idToDelete }
                    if (generalToDelete != null) {
                        generallist.remove(generalToDelete)
                        generalAdapter?.notifyDataSetChanged()

                        CoroutineScope(Dispatchers.IO).launch {
                            generalDao.deleteGeneral(generalToDelete)

                            val imageUrl = generalToDelete.imageUrl
                            if (imageUrl.isNotEmpty()) {
                                val imageUri = Uri.parse(imageUrl)
                                deleteImageFile(imageUri)
                            }
                        }
                    } else {
                        showToast("找不到ID为 $idToDelete 的新闻")
                    }
                } else {
                    showToast("请输入有效的ID")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }


    // 新增的函数：删除全部general新闻
    private fun deleteAllGenerals() {
        AlertDialog.Builder(requireContext())
            .setTitle("删除所有新闻")
            .setMessage("确定要删除全部新闻吗？")
            .setPositiveButton("确定") { _, _ ->
                if (generallist.isNotEmpty()) {
                    val copyOfGenerals = ArrayList(generallist) // Make a copy of the list

                    generallist.clear()
                    generalAdapter?.notifyDataSetChanged()

                    // 使用 CoroutineScope 替代 GlobalScope，方便取消任务
                    CoroutineScope(Dispatchers.IO).launch {
                        generalDao.deleteAllGenerals()

                        // 删除所有相关图像文件
                        for (general in copyOfGenerals) {
                            val imageUrl = general.imageUrl
                            if (imageUrl.isNotEmpty()) {
                                val imageUri = Uri.parse(imageUrl)
                                deleteImageFile(imageUri)
                            }
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // 新增的函数：删除对应的图像文件
    private fun deleteImageFile(imageUri: Uri) {
        val contentResolver: ContentResolver = requireContext().contentResolver
        contentResolver.delete(imageUri, null, null)
    }



    // 启动AddNewsActivity
    private fun startAddNewsActivity() {
        val intent = Intent(requireContext(), AddNewsActivity::class.java)
        startActivityForResult(intent, ADD_NEWS_REQUEST_CODE)
    }

    // 处理从AddNewsActivity返回的数据
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NEWS_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // 获取从 AddNewsActivity 传回来的数据
            val title = data.getStringExtra("title")
            val from = data.getStringExtra("from")
            val mantleImageUrl = data.getStringExtra("mantleImageUrl")
            val content = data.getStringExtra("content")

            // 创建新的 general 对象，并设置图片URL
            val newGeneral = general(
                title = title ?: "",
                content = content ?: "",
                iconResource = R.drawable.ic_fire,
                label = "热点",
                source = from ?: "",
                imageUrl = mantleImageUrl ?: ""
            )

            // 将新的 general 对象插入数据库，并更新 generallist
            GlobalScope.launch(Dispatchers.IO) {
                generalDao.insertGeneral(newGeneral)
                withContext(Dispatchers.Main) {
                    generalAdapter?.insertGeneralAtTop(newGeneral)
                }
            }
        }
    }

    // 图片点击事件处理
    private fun onImageClick() {
        val fadeInAnimation = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        fadeInAnimation.duration = 1000
        fadeInAnimation.start()
    }

    // 显示PopupMenu
    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), msetting, Gravity.END, 0, 0)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.login -> {
                    showToast("点击了登录")
                    true
                }
                R.id.sign -> {
                    showToast("点击了注册")
                    true
                }
                R.id.addnews -> {
                    startAddNewsActivity() // 启动AddNewsActivity
                    true
                }
                R.id.set -> {
                    showToast("点击了设置")
                    true
                }
                R.id.quit -> {
                    // 退出应用
                    showToast("点击了退出")
                    requireActivity().finishAffinity()
                    true
                }
                R.id.deleteitem -> {
                    deleteSpecificGeneral() // 删除指定的general新闻
                    true
                }
                R.id.deleteall -> {
                    deleteAllGenerals() // 删除全部general新闻
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    // 显示Toast消息
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        generallist.clear()
        generalAdapter = null
    }
}
