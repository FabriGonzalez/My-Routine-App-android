package com.myroutine.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.repositories.ExerciseRepository
import com.myroutine.app.data.repositories.RoutineDayRepository
import com.myroutine.app.data.repositories.SettingsRepository
import com.myroutine.app.data.repositories.TrainingHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val trainingHistoryRepository: TrainingHistoryRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineDayRepository: RoutineDayRepository
) : ViewModel() {

    val daysCount = settingsRepository.daysCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val currentDayIndex = settingsRepository.currentDayIndex.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private val currentRoutineDayId =
        currentDayIndex.flatMapLatest { index ->
            routineDayRepository.getRoutineDayIdByIndex(index)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val exercisesOfDay: StateFlow<List<Exercise>> =
        currentRoutineDayId.flatMapLatest { dayId ->
            if (dayId == null) flowOf(emptyList())
            else exerciseRepository.getExercisesByDay(dayId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun addExercise(
        name: String,
        sets: Int,
        reps: Int,
        weight: Double?
    ) = viewModelScope.launch {

        val dayId = currentRoutineDayId.firstOrNull() ?: return@launch

        exerciseRepository.insertExercise(
            routineDayId = dayId,
            name = name,
            sets = sets,
            reps = reps,
            weight = weight
        )
    }

    fun deleteExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.deleteExercise(exercise)
    }

    fun saveRoutine(days: Int) = viewModelScope.launch {
        settingsRepository.saveRoutine(days)
        settingsRepository.saveCurrentDay(0)

        routineDayRepository.createRoutine(days)
    }

    fun selectDay(index: Int) = viewModelScope.launch {
        settingsRepository.saveCurrentDay(index)
    }

    fun completeDay(daysSize: Int, currentIndex: Int) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()

            trainingHistoryRepository.saveTrainingHistory(
                routineDayNumber = currentIndex + 1,
                timestamp = timestamp
            )

            val newIndex = (currentIndex + 1) % daysSize
            settingsRepository.saveCurrentDay(newIndex)
        }
    }
}