package com.myroutine.app.data.local.entity

import androidx.room.*

@Entity(
    tableName = "exercise_history",
    foreignKeys = [
        ForeignKey(
            entity = TrainingSession::class,
            parentColumns = ["idTrainingSession"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ExerciseHistory(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val weight: Float,
    val reps: Int,
    val sets: Int
)