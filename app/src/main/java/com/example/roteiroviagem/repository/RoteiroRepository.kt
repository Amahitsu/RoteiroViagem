package com.example.roteiroviagem.data.repository

import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.dao.RoteiroDao
import com.example.roteiroviagem.entity.Roteiro

class RoteiroRepository(
    private val roteiroDao: RoteiroDao,
    private val geminiService: GeminiService
) {

    suspend fun obterRoteiro(destino: String, userId: String, dias: Long, orcamento: Double): Roteiro {
        val roteiroSalvo = roteiroDao.buscarUltimoNaoAceito(destino, userId)
        return roteiroSalvo ?: run {
            val sugestao = geminiService.sugerirRoteiroComSalvamento(destino, userId, dias, orcamento, this)
            return Roteiro(destino = destino, sugestao = sugestao, aceito = false, userId = userId)
        }
    }

    suspend fun recusarERetornarOutro(
        destino: String,
        userId: String,
        dias: Long,
        orcamento: Double,
        motivoRecusa: String?
    ): Roteiro {
        // gera uma nova sugestão via Gemini
        val novaSugestao = GeminiService.sugerirRoteiroComSalvamento(destino, userId, dias, orcamento, this)

        val novo = Roteiro(
            destino = destino,
            sugestao = novaSugestao,
            aceito = false,
            userId = userId,
            motivoRecusa = motivoRecusa
        )

        roteiroDao.inserir(novo)
        return novo
    }

    suspend fun salvar(roteiro: Roteiro) {
        roteiroDao.inserir(roteiro)
    }

    suspend fun aceitarRoteiro(roteiro: Roteiro) {
        val roteiroAceito = roteiro.copy(aceito = true)
        roteiroDao.atualizar(roteiroAceito)
    }

    suspend fun listarTodosPorUsuario(userId: String): List<Roteiro> {
        return roteiroDao.buscarTodosPorUsuario(userId)
    }
}
