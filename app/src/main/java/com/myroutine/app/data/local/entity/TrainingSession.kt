package com.myroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_session",
    indices = [Index(value = ["completedDate"])]
)
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val idTrainingSession: Long = 0,
    val routineDayNumber: Int,
    val completedDate: Long
)