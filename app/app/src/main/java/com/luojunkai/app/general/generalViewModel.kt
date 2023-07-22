package com.luojunkai.app.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class generalViewModel(private val generalDao: generalDao) : ViewModel() {

    // 点击事件保存 general 到数据库
    fun insertGeneral(general: general) {
        viewModelScope.launch {
            generalDao.insertGeneral(general)
        }
    }

    // 添加伴生对象来实现ViewModelProvider.Factory接口
    companion object {
        class Factory(private val generalDao: generalDao) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(generalViewModel::class.java)) {
                    return generalViewModel(generalDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
