package com.example.roteiroviagem.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.entity.Roteiro
import kotlinx.coroutines.launch


class RoteiroViewModel(private val repository: RoteiroRepository) : ViewModel() {
    private val _roteiros = mutableStateListOf<Roteiro>()
    val roteiros: List<Roteiro> get() = _roteiros

    fun carregarRoteiros(username: String) {
        viewModelScope.launch {
            val lista = repository.getRoteirosByUsername(username)
            _roteiros.clear()
            _roteiros.addAll(lista)
        }
    }

    fun carregarRoteirosPorTripId(username: String, tripId: Long) {
        viewModelScope.launch {
            val lista = repository.getRoteirosByUsernameAndTripId(username, tripId)
            _roteiros.clear()
            _roteiros.addAll(lista)
        }
    }

    fun salvarRoteiro(username: String, destino: String, tripId: Int, sugestao: String) {
        viewModelScope.launch {
            val roteiro = Roteiro(username = username, destino = destino, tripId = tripId, sugestao = sugestao)
            repository.addRoteiro(roteiro)
            _roteiros.add(roteiro)
        }
    }

    fun deletarRoteiro(roteiro: Roteiro) {
        viewModelScope.launch {
            repository.deleteRoteiro(roteiro)
            _roteiros.remove(roteiro)
        }
    }
}