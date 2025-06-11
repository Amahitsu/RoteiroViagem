package com.example.roteiroviagem.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class Roteiro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int,  // FK para Trip
    val username: String,
    val destino: String,
    val sugestao: String
)