package com.luojunkai.app.general

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface generalDao {
    @Query("SELECT * FROM general")
    fun getAllGenerals(): List<general>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGeneral(general: general)
}