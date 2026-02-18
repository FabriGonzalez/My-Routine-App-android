package com.myroutine.app.data.repository

import com.myroutine.app.data.local.dao.TrainingHistoryDao
import com.myroutine.app.data.local.entity.TrainingHistory
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val trainingHistoryDao: TrainingHistoryDao
) {
    fun getAllTrainingHistory(): Flow<List<TrainingHistory>> {
        return trainingHistoryDao.getAllTrainingHistory()
    }

    fun getAllTrainedDates(): Flow<List<Long>> {
        return trainingHistoryDao.getAllTrainedDates()
    }

    fun getTrainingHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<TrainingHistory>> {
        return trainingHistoryDao.getTrainingHistoryByDateRange(startDate, endDate)
    }

    suspend fun saveTrainingHistory(routineDayNumber: Int, timestamp: Long) {
        val trainingHistory = TrainingHistory(
            routineDayId = routineDayNumber.toLong(),
            completedDate = timestamp
        )
        trainingHistoryDao.insertTrainingHistory(trainingHistory)
    }

    suspend fun deleteTrainingHistory(trainingHistory: TrainingHistory) {
        trainingHistoryDao.deleteTrainingHistory(trainingHistory)
    }
}

