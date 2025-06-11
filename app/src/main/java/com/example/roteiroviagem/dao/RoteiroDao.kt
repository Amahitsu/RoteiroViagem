package com.example.roteiroviagem.dao

import androidx.room.*
import com.example.roteiroviagem.entity.Roteiro

@Dao
interface RoteiroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roteiro: Roteiro)

    @Query("SELECT * FROM Roteiro WHERE username = :username")
    suspend fun getRoteirosByUsername(username: String): List<Roteiro>

    @Query("SELECT * FROM Roteiro WHERE tripId = :tripId")
    suspend fun getRoteirosByTripId(tripId: Int): List<Roteiro>

    @Query("SELECT EXISTS(SELECT 1 FROM Roteiro WHERE tripId = :tripId)")
    suspend fun existsByTripId(tripId: Int): Boolean

    @Delete
    suspend fun delete(roteiro: Roteiro)

    @Query("SELECT * FROM roteiro WHERE username = :username AND tripId = :tripId")
    suspend fun getByUsernameAndTripId(username: String, tripId: Long): List<Roteiro>

}