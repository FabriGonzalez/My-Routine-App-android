package com.myroutine.app.data.local.dao

import androidx.room.*
import com.myroutine.app.data.local.entity.RoutineDay
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDayDao {

    @Query("SELECT * FROM routine_days ORDER BY orderIndex ASC")
    fun getAllRoutineDays(): Flow<List<RoutineDay>>

    @Query("SELECT * FROM routine_days ORDER BY orderIndex ASC")
    suspend fun getAllRoutineDaysList(): List<RoutineDay>

    @Query("SELECT * FROM routine_days WHERE id = :id")
    suspend fun getRoutineDayById(id: Long): RoutineDay?

    @Query("SELECT * FROM routine_days WHERE orderIndex = :orderIndex")
    suspend fun getRoutineDayByOrder(orderIndex: Int): RoutineDay?

    @Query("SELECT COUNT(*) FROM routine_days")
    suspend fun getRoutineDaysCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDay(routineDay: RoutineDay): Long

    @Update
    suspend fun updateRoutineDay(routineDay: RoutineDay)

    @Delete
    suspend fun deleteRoutineDay(routineDay: RoutineDay)

    @Query("DELETE FROM routine_days")
    suspend fun deleteAllRoutineDays()

    @Query("SELECT id FROM routine_days WHERE orderIndex = :orderIndex LIMIT 1")
    fun getRoutineDayIdByOrder(orderIndex: Int): Flow<Long?>
}

