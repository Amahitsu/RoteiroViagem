package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiroviagem.dao.TripDao
import com.example.roteiroviagem.entity.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(
    private val tripDao: TripDao,
    username: String
) : ViewModel() {

    val trips: StateFlow<List<Trip>> = tripDao.getTripsByUsername(username)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            tripDao.insert(trip)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            tripDao.update(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripDao.delete(trip)
        }
    }
}

