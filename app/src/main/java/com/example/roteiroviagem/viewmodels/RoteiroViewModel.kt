package com.example.roteiroviagem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.entity.Roteiro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoteiroViewModel(
    private val repository: RoteiroRepository,
    private val geminiService: GeminiService,
    private val userId: String
) : ViewModel() {

    private val _roteiro = MutableStateFlow<Roteiro?>(null)
    val roteiro: StateFlow<Roteiro?> get() = _roteiro

    private val _roteiros = MutableStateFlow<List<Roteiro>>(emptyList())
    val roteiros: StateFlow<List<Roteiro>> get() = _roteiros
//
//    fun carregarRoteiro(destino: String, dias: Long, orcamento: Double) {
//        viewModelScope.launch {
//            val roteiroCarregado = repository.obterRoteiro(destino, userId, dias, orcamento)
//            _roteiro.value = roteiroCarregado
//        }
//    }
//    fun gerarNovoRoteiro(destino: String, dias: Long) {
//        viewModelScope.launch {
//            try {
//                val textoRoteiro = geminiService.sugerirRoteiro(
//                    destino = destino,
//                    userId = userId,
//                    repository = repository,
//                    dias = dias
//                )
//                val novoRoteiro = Roteiro(
//                    id = 0,
//                    destino = destino,
//                    sugestao = textoRoteiro,
//                    aceito = false,
//                    userId = userId
//                )
//                _roteiro.value = novoRoteiro
//            } catch (e: Exception) {
//                // TODO: tratar erro de forma apropriada (exibir toast/snackbar/log)
//            }
//        }
//    }

    fun inserirRoteiro(roteiro: Roteiro) {
        viewModelScope.launch {
            repository.salvar(roteiro)
            carregarTodosRoteirosAceitos()
        }
    }


    fun aceitarRoteiro(roteiro: Roteiro) {
        viewModelScope.launch {
            val roteiroParaSalvar = roteiro.copy(aceito = true)
            repository.salvar(roteiroParaSalvar)
            _roteiro.value = roteiroParaSalvar
            carregarTodosRoteirosAceitos()
        }
    }

    // Ajuste aqui: agora recebe 'dias' e passa ao repository
    fun recusarERetornarOutro(destino: String,  dias: Long, orcamento: Double, motivoRecusa: String?) {
        viewModelScope.launch {
            val roteiroRecusado = repository.recusarERetornarOutro(destino, userId,dias, orcamento, motivoRecusa)
            _roteiro.value = roteiroRecusado
        }
    }

    fun carregarTodosRoteirosAceitos() {
        viewModelScope.launch {
            try {
                val lista = repository.listarTodosPorUsuario(userId)
                _roteiros.value = lista.filter { it.aceito }
            } catch (e: Exception) {
                _roteiros.value = emptyList()
            }
        }
    }
}
