package com.myroutine.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myroutine.app.data.local.relation.TrainingSessionWithExercises
import com.myroutine.app.data.local.stats.calculateBestStreak
import com.myroutine.app.data.local.stats.calculateCurrentStreak
import com.myroutine.app.data.local.stats.calculateCurrentWeekProgress
import com.myroutine.app.data.repositories.RoutineDayRepository
import com.myroutine.app.data.repositories.TrainingHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val trainedDates: Set<Long> = emptySet(),
    val selectedDate: Long? = null,
    val trainingsForSelectedDate: List<TrainingSessionWithExercises> = emptyList(),
    val currentStreakWeeks: Int = 0,
    val bestStreakWeeks: Int = 0,
    val weekProgress: Int = 0,
    val routineDaysPerWeek: Int = 0
)

class CalendarViewModel(
    private val trainingRepository: TrainingHistoryRepository,
    private val routineDayRepository: RoutineDayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadRoutineDays()
        loadTrainedDates()
    }

    private fun loadRoutineDays() {

        viewModelScope.launch {

            val days = routineDayRepository.getRoutineDaysCount()

            _uiState.value = _uiState.value.copy(
                routineDaysPerWeek = days
            )
        }
    }

    private fun loadTrainedDates() {

        viewModelScope.launch {

            trainingRepository.getAllTrainedDates().collect { dates ->

                val normalizedDates =
                    dates.map { normalizeToStartOfDay(it) }

                val routineDays = _uiState.value.routineDaysPerWeek

                val currentStreak =
                    calculateCurrentStreak(normalizedDates, routineDays)

                val bestStreak =
                    calculateBestStreak(normalizedDates, routineDays)

                val weekProgress =
                    calculateCurrentWeekProgress(normalizedDates)

                _uiState.value = _uiState.value.copy(

                    trainedDates = normalizedDates.toSet(),

                    currentStreakWeeks = currentStreak,
                    bestStreakWeeks = bestStreak,
                    weekProgress = weekProgress
                )
            }
        }
    }

    fun goToPreviousMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _uiState.value.currentYear)
            set(Calendar.MONTH, _uiState.value.currentMonth)
            add(Calendar.MONTH, -1)
        }

        _uiState.value = _uiState.value.copy(
            currentYear = calendar.get(Calendar.YEAR),
            currentMonth = calendar.get(Calendar.MONTH)
        )
    }

    fun goToNextMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _uiState.value.currentYear)
            set(Calendar.MONTH, _uiState.value.currentMonth)
            add(Calendar.MONTH, 1)
        }

        _uiState.value = _uiState.value.copy(
            currentYear = calendar.get(Calendar.YEAR),
            currentMonth = calendar.get(Calendar.MONTH)
        )
    }

    fun selectDate(timestamp: Long) {

        val normalized = normalizeToStartOfDay(timestamp)

        if (_uiState.value.selectedDate == normalized) {

            _uiState.value = _uiState.value.copy(
                selectedDate = null,
                trainingsForSelectedDate = emptyList()
            )

            return
        }

        _uiState.value = _uiState.value.copy(
            selectedDate = normalized
        )

        loadTrainingsForDate(normalized)
    }

    private fun loadTrainingsForDate(timestamp: Long) {

        viewModelScope.launch {

            val startOfDay = timestamp
            val endOfDay = timestamp + 24 * 60 * 60 * 1000 - 1

            trainingRepository.getTrainingSessionsWithExercisesByDateRange(
                startOfDay,
                endOfDay
            ).collect { sessions ->

                _uiState.value = _uiState.value.copy(
                    trainingsForSelectedDate = sessions
                )
            }
        }
    }

    private fun normalizeToStartOfDay(timestamp: Long): Long {

        val calendar = Calendar.getInstance().apply {

            timeInMillis = timestamp

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calendar.timeInMillis
    }

    fun isDayTrained(dayTimestamp: Long): Boolean {

        val normalized = normalizeToStartOfDay(dayTimestamp)

        return _uiState.value.trainedDates.contains(normalized)
    }

    class Factory(
        private val trainingRepository: TrainingHistoryRepository,
        private val routineDayRepository: RoutineDayRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(
                    trainingRepository,
                    routineDayRepository
                ) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}