package com.example.roteiroviagem.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val type: String,
    val username: String
)
