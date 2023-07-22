package com.luojunkai.app.general

import android.os.AsyncTask
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface generalDao {
    @Query("SELECT * FROM general")
    fun getAllGenerals(): List<general>

    @Insert
    fun insertGeneral(general: general)

    // 定义异步任务类
    private class InsertAsyncTask(private val dao: generalDao) : AsyncTask<general, Void, Void>() {
        override fun doInBackground(vararg params: general): Void? {
            // 执行数据库插入操作
            dao.insertGeneral(params[0])
            return null
        }
    }
}
