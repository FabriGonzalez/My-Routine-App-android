package com.myroutine.app.data.repositories

import com.myroutine.app.data.local.dao.TrainingSessionDao
import com.myroutine.app.data.local.entity.TrainingSession
import com.myroutine.app.data.local.relation.TrainingSessionWithExercises
import kotlinx.coroutines.flow.Flow

class TrainingHistoryRepository(
    private val trainingSessionDao: TrainingSessionDao
) {

    fun getAllTrainingHistory(): Flow<List<TrainingSession>> {
        return trainingSessionDao.getAllTrainingHistory()
    }

    fun getAllTrainedDates(): Flow<List<Long>> {
        return trainingSessionDao.getAllTrainedDates()
    }

    fun getTrainingHistoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TrainingSession>> {
        return trainingSessionDao.getTrainingHistoryByDateRange(startDate, endDate)
    }

    fun getTrainingSessionsWithExercisesByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TrainingSessionWithExercises>> {
        return trainingSessionDao.getSessionsWithExercisesByDateRange(startDate, endDate)
    }

    suspend fun saveTrainingHistory(
        routineDayNumber: Int,
        timestamp: Long
    ): Long {

        val trainingSession = TrainingSession(
            routineDayNumber = routineDayNumber,
            completedDate = timestamp
        )

        return trainingSessionDao.insertTrainingHistory(trainingSession)
    }

    suspend fun deleteTrainingHistory(trainingSession: TrainingSession) {
        trainingSessionDao.deleteTrainingHistory(trainingSession)
    }
}