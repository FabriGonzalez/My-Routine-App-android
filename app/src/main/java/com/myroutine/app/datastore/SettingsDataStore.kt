package com.myroutine.app.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val DAYS_COUNT_KEY = intPreferencesKey("days_count")
        private val CURRENT_DAY_INDEX_KEY = intPreferencesKey("current_day_index")
    }

    val daysCount: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[DAYS_COUNT_KEY]
    }

    val currentDayIndex: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CURRENT_DAY_INDEX_KEY] ?: 0
    }

    suspend fun saveDaysCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAYS_COUNT_KEY] = count
        }
    }

    suspend fun saveCurrentDayIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_DAY_INDEX_KEY] = index
        }
    }

    suspend fun saveRoutineSettings(daysCount: Int, currentDayIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAYS_COUNT_KEY] = daysCount
            preferences[CURRENT_DAY_INDEX_KEY] = currentDayIndex
        }
    }
}
