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
}
