package com.example.pomodoroapp.model

enum class TimerPhase(val title: String, val isBreak: Boolean) {
    FOCUS("Focus", isBreak = false),
    SHORT_BREAK("Short break", isBreak = true),
    LONG_BREAK("Long break", isBreak = true)
}
