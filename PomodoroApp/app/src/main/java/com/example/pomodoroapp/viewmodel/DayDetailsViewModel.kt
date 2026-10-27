package com.example.pomodoroapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.PomodoroRepository
import com.example.pomodoroapp.model.CategoryTotal
import com.example.pomodoroapp.model.DaySummary
import com.example.pomodoroapp.model.categoryTotals
import com.example.pomodoroapp.model.date
import com.example.pomodoroapp.model.summarizeByDay
import com.example.pomodoroapp.ui.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class DayDetailsUiState(
    val date: LocalDate,
    val today: LocalDate = LocalDate.now(),
    val summary: DaySummary? = null,
    val categories: List<CategoryTotal> = emptyList(),
    val dailyGoal: Int = 0
)

class DayDetailsViewModel @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    repository: PomodoroRepository = PomodoroRepository.instance
) : ViewModel() {

    private val date: LocalDate = LocalDate.parse(checkNotNull(savedStateHandle[Screen.DayDetails.ARG_DATE]))

    val uiState: StateFlow<DayDetailsUiState> =
        combine(repository.sessions, repository.settings) { sessions, settings ->
            val daySessions = sessions.filter { it.date() == date }
            DayDetailsUiState(
                date = date,
                summary = summarizeByDay(daySessions).firstOrNull(),
                categories = categoryTotals(daySessions),
                dailyGoal = settings.dailyGoal
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayDetailsUiState(date))
}
