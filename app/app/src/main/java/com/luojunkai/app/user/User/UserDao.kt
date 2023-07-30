package com.luojunkai.app.user.User

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE uid = :userId")
    fun getUser(userId: Int): User?

    @Query("SELECT * FROM User WHERE uid = :userId")
    fun getUserLiveData(userId: Int): LiveData<User?>

    @Update
    fun updateUser(user: User)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(user: User)

    @Query("DELETE FROM User WHERE uid = :userId")
    fun deleteUser(userId: Int)

    @Query("DELETE FROM User")
    fun deleteAllUsers()
}