package com.luojunkai.app.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class generalViewModel(private val generalDao: generalDao, application: Application) : AndroidViewModel(application) {

    // 点击事件保存 general 到数据库
    fun insertGeneral(general: general) {
        viewModelScope.launch {
            generalDao.insertGeneral(general)
        }
    }

    // 无参构造函数不再使用application，改用Application参数
    constructor() : this(generalDatabase.getDatabase(getApplication()).generalDao(), getApplication()) {
        // 通过调用getDatabase方法获取数据库实例
        // 然后再获取generalDao实例并传递给有参构造函数
    }

    // 添加伴生对象来实现ViewModelProvider.Factory接口
    companion object {
        class Factory(private val generalDao: generalDao) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(generalViewModel::class.java)) {
                    return generalViewModel(generalDao, getApplication()) as T // 在此处传递Application参数
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}