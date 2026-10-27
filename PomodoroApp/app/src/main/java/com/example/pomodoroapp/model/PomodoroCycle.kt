package com.example.pomodoroapp.model

fun nextPhase(finished: TimerPhase, completedInCycle: Int, settings: TimerSettings): TimerPhase =
    when (finished) {
        TimerPhase.FOCUS ->
            if (completedInCycle >= settings.sessionsBeforeLongBreak) TimerPhase.LONG_BREAK
            else TimerPhase.SHORT_BREAK
        TimerPhase.SHORT_BREAK, TimerPhase.LONG_BREAK -> TimerPhase.FOCUS
    }
