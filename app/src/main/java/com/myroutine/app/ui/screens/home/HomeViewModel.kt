package com.myroutine.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.local.entity.ExerciseHistory
import com.myroutine.app.data.local.entity.MeasureType
import com.myroutine.app.data.repositories.ExerciseHistoryRepository
import com.myroutine.app.data.repositories.ExerciseRepository
import com.myroutine.app.data.repositories.RoutineDayRepository
import com.myroutine.app.data.repositories.SettingsRepository
import com.myroutine.app.data.repositories.TrainingHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val routineDayRepository: RoutineDayRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository
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
        measureValue: Double,
        measureType: MeasureType,
        dayIndex: Int
    ) = viewModelScope.launch {
        val dayId = routineDayRepository
            .getRoutineDayIdByIndex(dayIndex)
            .firstOrNull()

        if (dayId == null) return@launch

        exerciseRepository.insertExercise(
            routineDayId = dayId,
            name = name,
            sets = sets,
            reps = reps,
            measureValue = measureValue,
            measureType = measureType
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

            val dayId = routineDayRepository
                .getRoutineDayIdByIndex(currentIndex)
                .firstOrNull()

            if (dayId == null) return@launch

            val exercises = exerciseRepository
                .getExercisesByDay(dayId)
                .firstOrNull()
                ?: emptyList()

            val sessionId = trainingHistoryRepository
                .saveTrainingHistory(
                    routineDayNumber = currentIndex + 1,
                    timestamp = timestamp
                )

            val historyExercises = exercises.map {

                ExerciseHistory(
                    sessionId = sessionId,
                    exerciseName = it.name,
                    weight = it.measureValue.toFloat(),
                    reps = it.reps,
                    sets = it.sets
                )
            }

            exerciseHistoryRepository.insertExercises(historyExercises)

            val newIndex = (currentIndex + 1) % daysSize

            settingsRepository.saveCurrentDay(newIndex)
        }
    }

    fun reorderExercises(
        fromIndex: Int,
        toIndex: Int,
        exercises: List<Exercise>
    ){
        viewModelScope.launch {
            val mutable = exercises.toMutableList()
            val moved = mutable.removeAt(fromIndex)
            mutable.add(toIndex, moved)

            mutable.forEachIndexed { index, exercise ->
                exerciseRepository.updateExercise(
                    exercise.copy(orderIndex = index)
                )
            }
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.updateExercise(exercise)
        }
    }
}