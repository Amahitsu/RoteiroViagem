package com.example.roteiroviagem.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.roteiroviagem.entity.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)

    @Query("SELECT * FROM Trip WHERE username = :username ORDER BY startDate DESC")
    fun getTripsByUsername(username: String): Flow<List<Trip>>

    @Query("SELECT * FROM Trip WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Trip
}
