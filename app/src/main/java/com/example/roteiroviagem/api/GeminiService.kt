package com.example.roteiroviagem.api

import android.util.Log
import com.example.roteiroviagem.data.repository.RoteiroRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GeminiService {
    private const val apiKey = "AIzaSyCcC-YhtQyixKFMMf6PECC4J2-MNj_q-vg"
    private const val TAG = "GeminiService"

    suspend fun sugerirRoteiroComSalvamento(
        destino: String,
        repository: RoteiroRepository
    ): String = suspendCancellableCoroutine { continuation ->
        val prompt = "Sugira um roteiro de viagem para $destino, incluindo dicas locais, culinária e pontos turísticos."

        // Construção manual do JSON conforme esperado pela API do Gemini
        val partsArray = JSONArray().apply {
            put(JSONObject().apply {
                put("text", prompt)
            })
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
            put("maxOutputTokens", 512)
        }

        val json = JSONObject().apply {
            put("contents", contentsArray)
            put("generationConfig", generationConfig)
        }.toString()

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e(TAG, "Erro na requisição: ${e.message}", e)
                if (continuation.isActive) continuation.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()

                if (!response.isSuccessful || body == null) {
                    if (continuation.isActive)
                        continuation.resumeWithException(Exception("Erro HTTP ${response.code}: ${response.message}"))
                    return
                }

                try {
                    val jsonResponse = JSONObject(body)
                    val resultado = jsonResponse
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text") // texto gerado

                    // salvar no banco de dados assincronamente
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.salvar(destino, resultado)
                    }

                    if (continuation.isActive) continuation.resume(resultado)
                } catch (e: Exception) {
                    if (continuation.isActive)
                        continuation.resumeWithException(Exception("Erro ao interpretar resposta: ${e.message}"))
                }
            }
        })
    }
}
