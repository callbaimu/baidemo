package com.luojunkai.app.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE uid = :userId")
    fun getUser(userId: Int): User?

    @Update
    fun updateUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(user: User)
}