package com.myroutine.app.data.repositories

import com.myroutine.app.data.local.dao.ExerciseHistoryDao
import com.myroutine.app.data.local.entity.ExerciseHistory

class ExerciseHistoryRepository(
    private val dao: ExerciseHistoryDao
) {

    suspend fun insertExercises(exercises: List<ExerciseHistory>) {
        dao.insertExercises(exercises)
    }

}