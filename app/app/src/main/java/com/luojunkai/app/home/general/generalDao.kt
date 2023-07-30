package com.luojunkai.app.home.general

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface generalDao {
    @Query("SELECT * FROM general")
    fun getAllGenerals(): List<general>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGeneral(general: general)

    @Delete
    fun deleteGeneral(general: general)

    @Query("DELETE FROM general")
    fun deleteAllGenerals()
}