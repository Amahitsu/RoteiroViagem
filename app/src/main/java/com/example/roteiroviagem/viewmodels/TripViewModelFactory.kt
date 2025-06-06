package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roteiroviagem.dao.TripDao

class TripViewModelFactory(
    private val tripDao: TripDao,
    private val username: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripViewModel(tripDao, username) as T
    }
}

