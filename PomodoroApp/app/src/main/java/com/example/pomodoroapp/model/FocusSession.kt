package com.example.pomodoroapp.model

data class FocusSession(
    val id: Long,
    val category: FocusCategory,
    val phase: TimerPhase,
    val startedAt: Long,
    val durationMillis: Long,
    val completed: Boolean,
    val task: String = ""
)
