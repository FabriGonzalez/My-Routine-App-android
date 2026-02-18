package com.myroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineDay::class,
            parentColumns = ["id"],
            childColumns = ["routineDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["routineDayId"])]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineDayId: Long,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Double? = null,
    val orderIndex: Int
)

