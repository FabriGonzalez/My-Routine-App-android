package com.myroutine.app.data.repositories

import com.myroutine.app.data.local.dao.TrainingHistoryDao
import com.myroutine.app.data.local.entity.TrainingHistory
import kotlinx.coroutines.flow.Flow

class TrainingHistoryRepository(
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
            routineDayNumber = routineDayNumber,
            completedDate = timestamp
        )
        trainingHistoryDao.insertTrainingHistory(trainingHistory)
    }

    suspend fun deleteTrainingHistory(trainingHistory: TrainingHistory) {
        trainingHistoryDao.deleteTrainingHistory(trainingHistory)
    }
}

