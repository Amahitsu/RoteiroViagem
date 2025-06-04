package com.example.roteiroviagem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roteiroviagem.dao.RoteiroDao
import com.example.roteiroviagem.dao.TripDao
import com.example.roteiroviagem.dao.UserDao
import com.example.roteiroviagem.entity.Roteiro
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.entity.User

@Database(
    entities = [User::class, Trip::class, Roteiro::class],
    version = 9, // Aumente sempre que mudar o schema!
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun roteiroDao(): RoteiroDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration() // <-- Adicionado aqui
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
