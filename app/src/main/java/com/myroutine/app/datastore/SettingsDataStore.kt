package com.myroutine.app.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val DAYS_COUNT_KEY = intPreferencesKey("days_count")
        private val CURRENT_DAY_INDEX_KEY = intPreferencesKey("current_day_index")
        private val PRIMARY_COLOR_KEY = longPreferencesKey("primary_color")
        private val BACKGROUND_COLOR_KEY = longPreferencesKey("background_color")
        private val SURFACE_COLOR_KEY = longPreferencesKey("surface_color")
    }

    val daysCount: Flow<Int> = context.dataStore.data.map {
        it[DAYS_COUNT_KEY] ?: 0
    }

    val currentDayIndex: Flow<Int> = context.dataStore.data.map {
        it[CURRENT_DAY_INDEX_KEY] ?: 0
    }

    suspend fun saveDaysCount(count: Int) {
        context.dataStore.edit {
            it[DAYS_COUNT_KEY] = count
        }
    }

    suspend fun saveCurrentDayIndex(index: Int) {
        context.dataStore.edit {
            it[CURRENT_DAY_INDEX_KEY] = index
        }
    }

    suspend fun saveRoutineSettings(daysCount: Int, currentDayIndex: Int) {
        context.dataStore.edit {
            it[DAYS_COUNT_KEY] = daysCount
            it[CURRENT_DAY_INDEX_KEY] = currentDayIndex
        }
    }

    val primaryColor: Flow<Long> = context.dataStore.data.map {
        it[PRIMARY_COLOR_KEY] ?: 0xFF00E676
    }

    val backgroundColor: Flow<Long> = context.dataStore.data.map {
        it[BACKGROUND_COLOR_KEY] ?: 0xFF121212
    }

    val surfaceColor: Flow<Long> = context.dataStore.data.map {
        it[SURFACE_COLOR_KEY] ?: 0xFF1E1E1E
    }

    suspend fun savePrimaryColor(color: Long) {
        context.dataStore.edit {
            it[PRIMARY_COLOR_KEY] = color
        }
    }

    suspend fun saveBackgroundColor(color: Long) {
        context.dataStore.edit {
            it[BACKGROUND_COLOR_KEY] = color
        }
    }

    suspend fun saveSurfaceColor(color: Long) {
        context.dataStore.edit {
            it[SURFACE_COLOR_KEY] = color
        }
    }
}