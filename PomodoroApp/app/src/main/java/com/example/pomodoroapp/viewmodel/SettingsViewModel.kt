package com.example.pomodoroapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pomodoroapp.data.PomodoroRepository
import com.example.pomodoroapp.model.TimerSettings
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: PomodoroRepository = PomodoroRepository.instance
) : ViewModel() {

    val settings: StateFlow<TimerSettings> = repository.settings

    fun setFocusMinutes(value: Int) =
        repository.updateSettings { it.copy(focusMinutes = value.coerceIn(TimerSettings.FOCUS_RANGE)) }

    fun setShortBreakMinutes(value: Int) =
        repository.updateSettings { it.copy(shortBreakMinutes = value.coerceIn(TimerSettings.SHORT_BREAK_RANGE)) }

    fun setLongBreakMinutes(value: Int) =
        repository.updateSettings { it.copy(longBreakMinutes = value.coerceIn(TimerSettings.LONG_BREAK_RANGE)) }

    fun setSessionsBeforeLongBreak(value: Int) =
        repository.updateSettings { it.copy(sessionsBeforeLongBreak = value.coerceIn(TimerSettings.SESSIONS_RANGE)) }

    fun setDailyGoal(value: Int) =
        repository.updateSettings { it.copy(dailyGoal = value.coerceIn(TimerSettings.GOAL_RANGE)) }

    fun setAutoStart(enabled: Boolean) =
        repository.updateSettings { it.copy(autoStartNext = enabled) }

    fun resetToDefaults() = repository.updateSettings { TimerSettings() }

    fun clearHistory() = repository.clearHistory()
}
