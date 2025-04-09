package com.example.roteiroviagem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roteiroviagem.dao.UserDao
import com.example.roteiroviagem.entity.User

@Database (
    entities = [User::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun userDao() : UserDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java,
                    "user_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}