package com.luojunkai.app.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.luojunkai.app.user.User.User
import com.luojunkai.app.user.User.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDatabase = UserDatabase.getDatabase(application)
    private val userDao = userDatabase.userDao()

    fun getUserLiveData(): LiveData<User?> {
        // 假设用户ID为1，这将返回ID为1的用户的 LiveData<User>
        return userDao.getUserLiveData(1)
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertOrUpdateUser(user)
        }
    }
}