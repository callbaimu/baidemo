package com.luojunkai.app.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class generalViewModel(private val generalDao: generalDao) : ViewModel() {

    // 点击事件保存 general 到数据库
    fun insertGeneral(general: general) {
        viewModelScope.launch {
            generalDao.insertGeneral(general)
        }
    }

    // 其他ViewModel逻辑...

}
