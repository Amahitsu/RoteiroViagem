package com.example.roteiroviagem.api

import android.util.Log
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.entity.Roteiro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GeminiService {
    private const val apiKey = "AIzaSyCcC-YhtQyixKFMMf6PECC4J2-MNj_q-vg"
    private const val TAG = "GeminiService"

    // Agora a lista tem prompt base, mas o prompt real vai incluir os dias
    private val promptBases = listOf(
        "Sugira um roteiro de viagem para DESTINO, incluindo dicas locais, culinária e pontos turísticos.",
        "Monte um roteiro turístico detalhado para DESTINO em uma viagem de DIAS dias, com atividades diárias claramente separadas",
        "O que visitar em DESTINO? Sugira um roteiro para aproveitar ao máximo a cidade."
    )

    suspend fun sugerirRoteiro(
        destino: String,
        userId: String,
        dias: Long,
        orcamento: Double,
        repository: RoteiroRepository
    ): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .cache(null)
            .build()
        val maxTentativas = promptBases.size

        val roteirosExistentes = repository.listarTodosPorUsuario(userId)
            .filter { it.destino == destino }
            .map { it.sugestao.trim() }

        suspendCancellableCoroutine<String> { continuation ->
            fun tentarPrompt(index: Int) {
                if (index >= maxTentativas) {
                    if (continuation.isActive)
                        continuation.resumeWithException(Exception("Não foi possível obter um roteiro novo."))
                    return
                }

                val prompt = promptBases[index]
                    .replace("DESTINO", destino)
                    .replace("DIAS", dias.toString())

                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                }

                val userContent = JSONObject().apply {
                    put("role", "user")
                    put("parts", partsArray)
                }

                val contentsArray = JSONArray().apply {
                    put(userContent)
                }

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 100000)
                }

                val json = JSONObject().apply {
                    put("contents", contentsArray)
                    put("generationConfig", generationConfig)
                }.toString()

                val requestBody = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val body = response.body?.string()
                        if (!response.isSuccessful || body == null) {
                            tentarPrompt(index + 1)
                            return
                        }

                        try {
                            val jsonResponse = JSONObject(body)
                            val texto = jsonResponse
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")
                                .trim()

                            if (texto.isBlank() || texto.length < 50 || roteirosExistentes.contains(texto)) {
                                tentarPrompt(index + 1)
                            } else {
                                if (continuation.isActive) continuation.resume(texto)
                            }

                        } catch (e: Exception) {
                            tentarPrompt(index + 1)
                        }
                    }
                })
            }

            tentarPrompt(0)
        }
    }

    // Agora o salvar também recebe dias e passa para sugerirRoteiro
    suspend fun sugerirRoteiroComSalvamento(
        destino: String,
        userId: String,
        dias: Long,
        orcamento: Double,
        repository: RoteiroRepository
    ): String {
        val texto = sugerirRoteiro(destino, userId, dias, orcamento, repository)
        val roteiro = Roteiro(
            destino = destino,
            sugestao = texto,
            aceito = false,
            userId = userId
        )
        repository.salvar(roteiro)
        return texto
    }

}
