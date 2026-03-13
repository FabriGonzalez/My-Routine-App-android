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
    val reps: Int?,
    val failure: Boolean,
    val measureValue: Double,
    val measureType: MeasureType,
    val orderIndex: Int
)

