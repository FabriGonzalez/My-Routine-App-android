package com.myroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_history",
    foreignKeys = [
        ForeignKey(
            entity = RoutineDay::class,
            parentColumns = ["id"],
            childColumns = ["routineDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["routineDayId"]), Index(value = ["completedDate"])]
)
data class TrainingHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineDayId: Long,
    val completedDate: Long // Timestamp en milisegundos
)

