package com.example.roteiroviagem.data.repository

import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.dao.RoteiroDao
import com.example.roteiroviagem.entity.Roteiro

class RoteiroRepository(private val roteiroDao: RoteiroDao) {

    suspend fun addRoteiro(roteiro: Roteiro) = roteiroDao.insert(roteiro)



    suspend fun getRoteirosByUsername(username: String): List<Roteiro> {
        return roteiroDao.getRoteirosByUsername(username)
    }

    suspend fun deleteRoteiro(roteiro: Roteiro) {
        roteiroDao.delete(roteiro)
    }

    suspend fun getRoteirosByUsernameAndTripId(username: String, tripId: Long): List<Roteiro> {
        return roteiroDao.getByUsernameAndTripId(username, tripId)
    }
}
