package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.dao.UserDao
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.viewmodel.RoteiroViewModel



class RoteiroViewModelFactory(
    private val repository: RoteiroRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoteiroViewModel(repository) as T
    }
}
