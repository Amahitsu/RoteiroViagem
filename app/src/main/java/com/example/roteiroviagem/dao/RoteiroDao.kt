package com.example.roteiroviagem.dao

import androidx.room.*
import com.example.roteiroviagem.entity.Roteiro

@Dao
interface RoteiroDao {

    @Query("SELECT * FROM roteiros WHERE destino = :destino AND aceito = 0 AND userId = :userId ORDER BY id DESC LIMIT 1")
    suspend fun buscarUltimoNaoAceito(destino: String, userId: String): Roteiro?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(roteiro: Roteiro)

    @Update
    suspend fun atualizar(roteiro: Roteiro)

    @Query("SELECT * FROM roteiros WHERE userId = :userId ORDER BY id DESC")
    suspend fun buscarTodosPorUsuario(userId: String): List<Roteiro>
}
