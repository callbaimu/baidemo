package com.luojunkai.app.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GeneralViewModel(private val generalDao: generalDao) : ViewModel() {

    // LiveData用于观察数据库操作结果
    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult: LiveData<Boolean>
        get() = _insertResult

    // 在ViewModel中执行数据库插入操作，并通过LiveData更新结果
    fun insertGeneral(general: general) {
        viewModelScope.launch {
            try {
                // 执行数据库插入操作
                generalDao.insertGeneral(general)
                _insertResult.postValue(true)
            } catch (e: Exception) {
                _insertResult.postValue(false)
            }
        }
    }
}
