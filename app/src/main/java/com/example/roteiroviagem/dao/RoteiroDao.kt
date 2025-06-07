package com.example.roteiroviagem.dao

import androidx.room.*
import com.example.roteiroviagem.entity.Roteiro

@Dao
interface RoteiroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roteiro: Roteiro)

    @Query("SELECT * FROM Roteiro WHERE username = :username")
    suspend fun getRoteirosByUsername(username: String): List<Roteiro>

    @Delete
    suspend fun delete(roteiro: Roteiro)

}
