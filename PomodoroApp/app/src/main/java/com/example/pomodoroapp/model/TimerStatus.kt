package com.example.pomodoroapp.model

sealed interface TimerStatus {
    data object Idle : TimerStatus

    data class Running(val endsAt: Long) : TimerStatus

    data class Paused(val remainingMillis: Long) : TimerStatus
}
