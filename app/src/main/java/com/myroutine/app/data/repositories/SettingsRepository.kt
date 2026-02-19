package com.myroutine.app.data.repositories

import com.myroutine.app.datastore.SettingsDataStore

class SettingsRepository(
    private val dataStore: SettingsDataStore
) {
    val daysCount = dataStore.daysCount
    val currentDayIndex = dataStore.currentDayIndex

    suspend fun saveRoutine(days: Int) =
        dataStore.saveRoutineSettings(days, 0)

    suspend fun saveCurrentDay(index: Int) =
        dataStore.saveCurrentDayIndex(index)
}
