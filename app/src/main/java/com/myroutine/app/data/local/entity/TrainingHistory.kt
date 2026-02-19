package com.myroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_history",
    indices = [Index(value = ["routineDayNumber"]), Index(value = ["completedDate"])]
)
data class TrainingHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineDayNumber: Int, // Número del día de rutina (1, 2, 3, etc.)
    val completedDate: Long // Timestamp en milisegundos
)

