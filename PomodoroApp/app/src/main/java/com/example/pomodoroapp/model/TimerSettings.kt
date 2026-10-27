package com.example.pomodoroapp.model

data class TimerSettings(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val dailyGoal: Int = 8,
    val autoStartNext: Boolean = false
) {
    fun durationMillis(phase: TimerPhase): Long {
        val minutes = when (phase) {
            TimerPhase.FOCUS -> focusMinutes
            TimerPhase.SHORT_BREAK -> shortBreakMinutes
            TimerPhase.LONG_BREAK -> longBreakMinutes
        }
        return minutes * 60_000L
    }

    companion object {
        val FOCUS_RANGE = 1..90
        val SHORT_BREAK_RANGE = 1..30
        val LONG_BREAK_RANGE = 5..60
        val SESSIONS_RANGE = 2..8
        val GOAL_RANGE = 1..16
    }
}
