package com.myroutine.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myroutine.app.data.local.entity.TrainingHistory
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
    val trainingsForSelectedDate: List<TrainingHistory> = emptyList()
)

class CalendarViewModel(
    private val repository: TrainingHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadTrainedDates()
    }

    private fun loadTrainedDates() {
        viewModelScope.launch {
            repository.getAllTrainedDates().collect { dates ->
                // Normalizar las fechas a inicio del día para comparación
                val normalizedDates = dates.map { normalizeToStartOfDay(it) }.toSet()
                _uiState.value = _uiState.value.copy(trainedDates = normalizedDates)
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
        val normalizedTimestamp = normalizeToStartOfDay(timestamp)
        _uiState.value = _uiState.value.copy(selectedDate = normalizedTimestamp)
        loadTrainingsForDate(normalizedTimestamp)
    }

    private fun loadTrainingsForDate(timestamp: Long) {
        viewModelScope.launch {
            val startOfDay = timestamp
            val endOfDay = timestamp + 24 * 60 * 60 * 1000 - 1
            repository.getTrainingHistoryByDateRange(startOfDay, endOfDay).collect { trainings ->
                _uiState.value = _uiState.value.copy(trainingsForSelectedDate = trainings)
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

    class Factory(private val repository: TrainingHistoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

