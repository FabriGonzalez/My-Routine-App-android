package com.myroutine.app.data.repositories

import com.myroutine.app.data.local.dao.RoutineDayDao
import com.myroutine.app.data.local.entity.RoutineDay
import kotlinx.coroutines.flow.Flow

class RoutineDayRepository(
    private val routineDayDao: RoutineDayDao
) {

    fun getAllDays(): Flow<List<RoutineDay>> =
        routineDayDao.getAllRoutineDays()

    suspend fun getRoutineDaysCount(): Int =
        routineDayDao.getRoutineDaysCount()

    suspend fun createRoutine(days: Int) {
        routineDayDao.deleteAllRoutineDays()

        val list = (1..days).map { index ->
            RoutineDay(
                name = "Día $index",
                orderIndex = index
            )
        }

        list.forEach { routineDayDao.insertRoutineDay(it) }
    }

    fun getRoutineDayIdByIndex(index: Int): Flow<Long?> =
        routineDayDao.getRoutineDayIdByOrder(index + 1)
}