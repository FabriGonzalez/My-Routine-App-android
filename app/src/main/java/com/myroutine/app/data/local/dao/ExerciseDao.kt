package com.myroutine.app.data.local.dao

import androidx.room.*
import com.myroutine.app.data.local.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises WHERE routineDayId = :routineDayId ORDER BY orderIndex ASC")
    fun getExercisesByDay(routineDayId: Long): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE routineDayId = :routineDayId ORDER BY orderIndex ASC")
    suspend fun getExercisesByDayList(routineDayId: Long): List<Exercise>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE routineDayId = :routineDayId")
    suspend fun deleteExercisesByDay(routineDayId: Long)

    @Query("SELECT MAX(orderIndex) FROM exercises WHERE routineDayId = :routineDayId")
    suspend fun getMaxOrderIndex(routineDayId: Long): Int?
}

