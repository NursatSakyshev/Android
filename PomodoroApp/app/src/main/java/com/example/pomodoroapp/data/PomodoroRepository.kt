package com.example.pomodoroapp.data

import com.example.pomodoroapp.model.FocusCategory
import com.example.pomodoroapp.model.FocusSession
import com.example.pomodoroapp.model.TimerPhase
import com.example.pomodoroapp.model.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId

class PomodoroRepository(seedDemoData: Boolean = true) {

    private var nextId = 0L

    private val _sessions = MutableStateFlow(if (seedDemoData) demoSessions() else emptyList())
    val sessions: StateFlow<List<FocusSession>> = _sessions.asStateFlow()

    private val _settings = MutableStateFlow(TimerSettings())
    val settings: StateFlow<TimerSettings> = _settings.asStateFlow()

    fun addSession(
        category: FocusCategory,
        phase: TimerPhase,
        startedAt: Long,
        durationMillis: Long,
        completed: Boolean,
        task: String
    ) {
        val session = FocusSession(nextId++, category, phase, startedAt, durationMillis, completed, task.trim())
        _sessions.update { it + session }
    }

    fun clearHistory() {
        _sessions.value = emptyList()
    }

    fun updateSettings(transform: (TimerSettings) -> TimerSettings) {
        _settings.update(transform)
    }

    private fun demoSessions(): List<FocusSession> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val pomodorosPerDay = listOf(4, 6, 2, 5, 3, 7)
        val categories = listOf(
            FocusCategory.STUDY, FocusCategory.WORK, FocusCategory.STUDY, FocusCategory.READING,
            FocusCategory.WORKOUT, FocusCategory.WORK, FocusCategory.MEDITATION
        )
        val focus = 25 * 60_000L
        val shortBreak = 5 * 60_000L

        return pomodorosPerDay.flatMapIndexed { dayIndex, count ->
            val dayStart = today.minusDays(dayIndex + 1L).atTime(9, 0).atZone(zone).toInstant().toEpochMilli()
            (0 until count).flatMap { i ->
                val start = dayStart + i * (focus + shortBreak)
                val category = categories[(dayIndex + i) % categories.size]
                listOf(
                    FocusSession(nextId++, category, TimerPhase.FOCUS, start, focus, completed = true),
                    FocusSession(nextId++, category, TimerPhase.SHORT_BREAK, start + focus, shortBreak, completed = true)
                )
            }
        }
    }

    companion object {
        val instance: PomodoroRepository by lazy { PomodoroRepository() }
    }
}
