package com.myroutine.app.data.repositories

import com.myroutine.app.data.local.dao.ExerciseDao
import com.myroutine.app.data.local.entity.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseRepository (
    private val exerciseDao: ExerciseDao
){
    fun getExercisesByDay(dayId: Long): Flow<List<Exercise>> =
        exerciseDao.getExercisesByDay(dayId)

    suspend fun insertExercise(
        routineDayId: Long,
        name: String,
        sets: Int,
        reps: Int,
        weight: Double?
    ) {
        val exercise = Exercise(
            routineDayId = routineDayId,
            name = name,
            sets = sets,
            reps = reps,
            weight = weight,
            orderIndex = getNextOrderIndex(routineDayId)
        )

        exerciseDao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.deleteExercise(exercise)

    private suspend fun getNextOrderIndex(routineDayId: Long): Int {
        val max = exerciseDao.getMaxOrderIndex(routineDayId) ?: 0
        return max + 1
    }
}