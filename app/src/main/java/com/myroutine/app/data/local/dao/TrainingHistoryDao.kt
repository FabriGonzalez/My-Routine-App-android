package com.myroutine.app.data.local.dao

import androidx.room.*
import com.myroutine.app.data.local.entity.TrainingHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingHistoryDao {

    @Query("SELECT * FROM training_history ORDER BY completedDate DESC")
    fun getAllTrainingHistory(): Flow<List<TrainingHistory>>

    @Query("SELECT * FROM training_history WHERE completedDate BETWEEN :startDate AND :endDate ORDER BY completedDate ASC")
    fun getTrainingHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<TrainingHistory>>

    @Query("SELECT * FROM training_history WHERE completedDate BETWEEN :startDate AND :endDate ORDER BY completedDate ASC")
    suspend fun getTrainingHistoryByDateRangeList(startDate: Long, endDate: Long): List<TrainingHistory>

    @Query("SELECT completedDate FROM training_history")
    fun getAllTrainedDates(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingHistory(trainingHistory: TrainingHistory): Long

    @Delete
    suspend fun deleteTrainingHistory(trainingHistory: TrainingHistory)

    @Query("DELETE FROM training_history")
    suspend fun deleteAllTrainingHistory()
}

