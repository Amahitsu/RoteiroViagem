package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.viewmodel.RoteiroViewModel


class RoteiroViewModelFactory(
    private val repository: RoteiroRepository,
    private val geminiService: GeminiService,
    private val userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoteiroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoteiroViewModel(repository, geminiService, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
