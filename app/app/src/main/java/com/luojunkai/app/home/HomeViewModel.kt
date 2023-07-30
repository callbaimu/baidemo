package com.luojunkai.app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luojunkai.app.home.general.general
import com.luojunkai.app.home.homeweather.homeweather
import com.luojunkai.app.home.homeweather.parsehomeweatherFromLiveData

class HomeViewModel : ViewModel() {

    // 使用LiveData管理天气信息
    private val _weatherInfo = MutableLiveData<homeweather?>()
    val weatherInfo: LiveData<homeweather?>
        get() = _weatherInfo

    // 使用LiveData管理搜索内容
    private val _searchContent = MutableLiveData<String>()
    val searchContent: LiveData<String>
        get() = _searchContent

    // 使用LiveData管理新闻列表
    private val _newsList = MutableLiveData<List<general>>()
    val newsList: LiveData<List<general>>
        get() = _newsList

    // 更新天气信息的方法
    fun updateWeatherInfo(weatherDescription: String?) {
        // 对可空类型进行安全处理
        val homeweather = weatherDescription?.let { parsehomeweatherFromLiveData(it) }
        _weatherInfo.value = homeweather
    }

    // 更新搜索内容的方法
    fun updateSearchContent(content: String) {
        _searchContent.value = content
    }

    // 更新新闻列表的方法
    fun updateNewsList(newsList: List<general>) {
        _newsList.value = newsList
    }
}