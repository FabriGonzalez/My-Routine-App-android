package com.myroutine.app.data.local.dao

import androidx.room.*
import com.myroutine.app.data.local.entity.TrainingSession
import com.myroutine.app.data.local.relation.TrainingSessionWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {

    @Insert
    suspend fun insertTrainingHistory(trainingSession: TrainingSession): Long

    @Delete
    suspend fun deleteTrainingHistory(trainingSession: TrainingSession)

    @Query("""
        SELECT * FROM training_session
        ORDER BY completedDate DESC
    """)
    fun getAllTrainingHistory(): Flow<List<TrainingSession>>

    @Query("""
        SELECT completedDate FROM training_session
    """)
    fun getAllTrainedDates(): Flow<List<Long>>

    @Query("""
        SELECT * FROM training_session
        WHERE completedDate BETWEEN :startDate AND :endDate
        ORDER BY completedDate DESC
    """)
    fun getTrainingHistoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TrainingSession>>

    @Transaction
    @Query("""
        SELECT * FROM training_session
        WHERE completedDate BETWEEN :startDate AND :endDate
        ORDER BY completedDate DESC
    """)
    fun getSessionsWithExercisesByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TrainingSessionWithExercises>>
}