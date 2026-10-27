package com.example.pomodoroapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.PomodoroRepository
import com.example.pomodoroapp.model.CategoryTotal
import com.example.pomodoroapp.model.DayBar
import com.example.pomodoroapp.model.DaySummary
import com.example.pomodoroapp.model.categoryTotals
import com.example.pomodoroapp.model.date
import com.example.pomodoroapp.model.lastSevenDays
import com.example.pomodoroapp.model.summarizeByDay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class HistoryUiState(
    val today: LocalDate = LocalDate.now(),
    val todayFocusMillis: Long = 0L,
    val todayPomodoros: Int = 0,
    val dailyGoal: Int = 0,
    val week: List<DayBar> = emptyList(),
    val weekCategories: List<CategoryTotal> = emptyList(),
    val days: List<DaySummary> = emptyList()
)

class HistoryViewModel(
    repository: PomodoroRepository = PomodoroRepository.instance
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> =
        combine(repository.sessions, repository.settings) { sessions, settings ->
            val today = LocalDate.now()
            val days = summarizeByDay(sessions)
            val todaySummary = days.firstOrNull { it.date == today }
            val weekStart = today.minusDays(6)

            HistoryUiState(
                today = today,
                todayFocusMillis = todaySummary?.focusMillis ?: 0L,
                todayPomodoros = todaySummary?.pomodoros ?: 0,
                dailyGoal = settings.dailyGoal,
                week = lastSevenDays(sessions, today),
                weekCategories = categoryTotals(sessions.filter { !it.date().isBefore(weekStart) }),
                days = days
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
}
