package com.luojunkai.app.home.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.luojunkai.app.R
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebSearchActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var currentPage = 1
    var CACHE_SIZE = 100L * 1024 * 1024 // 100 MB

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_websearch)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // Enable local storage support
        webView.settings.loadsImagesAutomatically = true
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("SetJavaScriptEnabled", "OverridingDeprecatedMember")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 拦截URL加载请求，判断是否为百度搜索结果页面
                if (url?.startsWith("https://m.baidu.com/") == true) {
                    // 使用 OkHttp 加载 URL 内容
                    loadUrlWithOkHttp(url)
                } else {
                    // 对于其他URL scheme，让WebView处理加载请求
                    if (url != null) {
                        view?.loadUrl(url)
                    }
                }
                return true
            }
        }

        val backButton = findViewById<ImageView>(R.id.webbackicon)
        backButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
                currentPage--
            } else {
                // 返回到HomeFragment
                finish()
            }
        }

        val homeButton = findViewById<ImageView>(R.id.webbackhomeicon)
        homeButton.setOnClickListener {
            // 返回到百度首页，将page重置为1
            webView.loadUrl("https://m.baidu.com/")
            currentPage = 1
        }

        val forwardButton = findViewById<ImageView>(R.id.webforwardicon)
        forwardButton.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
                currentPage++
            }
        }

        // 获取传递的page值，默认为1
        currentPage = intent.getIntExtra("page", 1)

        // 根据传递的page值加载对应的页面
        when (currentPage) {
            1 -> webView.loadUrl("https://m.baidu.com/")
            2 -> {
                val searchText = intent.getStringExtra("search_text")
                if (!searchText.isNullOrEmpty()) {
                    webView.loadUrl("https://m.baidu.com/s?word=$searchText")
                }
            }

            else -> {
                // 默认加载百度首页
                webView.loadUrl("https://m.baidu.com/")
                currentPage = 1
            }
        }
    }

    // 使用 OkHttp 加载 URL 内容
    private fun loadUrlWithOkHttp(url: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .cache(Cache(cacheDir, CACHE_SIZE))
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理请求失败
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (responseBody != null) {
                        webView.loadData(responseBody, "text/html", "UTF-8")
                    }
                }
                response.close() // 关闭响应，确保资源被释放
                client.connectionPool.evictAll() // 清空连接池，确保连接被释放
            }
        })
    }

}