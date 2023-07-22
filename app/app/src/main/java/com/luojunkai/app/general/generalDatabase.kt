package com.luojunkai.app.general

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [general::class], version = 1, exportSchema = false)
abstract class generalDatabase : RoomDatabase() {

    abstract fun generalDao(): generalDao

    companion object {
        @Volatile
        private var INSTANCE: generalDatabase? = null

        fun getViewModel(application: Application): generalViewModel {
            return generalViewModel(generalDatabase.getDatabase(application).generalDao(), application)
        }
        fun getDatabase(context: Context): generalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    generalDatabase::class.java,
                    "general_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}