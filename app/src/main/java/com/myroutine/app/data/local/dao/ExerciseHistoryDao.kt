package com.myroutine.app.data.local.dao

import androidx.room.*
import com.myroutine.app.data.local.entity.ExerciseHistory

@Dao
interface ExerciseHistoryDao {

    @Insert
    suspend fun insertExercise(exercise: ExerciseHistory)

    @Insert
    suspend fun insertExercises(exercises: List<ExerciseHistory>)

    @Query("""
        SELECT * FROM exercise_history
        WHERE sessionId = :sessionId
    """)
    suspend fun getExercisesForSession(sessionId: Long): List<ExerciseHistory>
}