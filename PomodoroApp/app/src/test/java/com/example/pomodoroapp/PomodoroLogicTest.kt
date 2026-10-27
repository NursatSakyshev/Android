package com.example.pomodoroapp

import com.example.pomodoroapp.model.FocusCategory
import com.example.pomodoroapp.model.FocusSession
import com.example.pomodoroapp.model.TimerPhase
import com.example.pomodoroapp.model.TimerSettings
import com.example.pomodoroapp.model.lastSevenDays
import com.example.pomodoroapp.model.nextPhase
import com.example.pomodoroapp.model.summarizeByDay
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class PomodoroLogicTest {

    private val settings = TimerSettings(sessionsBeforeLongBreak = 4)
    private val today = LocalDate.of(2026, 10, 27)

    private fun session(date: LocalDate, phase: TimerPhase, minutes: Long, completed: Boolean = true) =
        FocusSession(
            id = 0,
            category = FocusCategory.STUDY,
            phase = phase,
            startedAt = date.atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            durationMillis = minutes * 60_000,
            completed = completed
        )

    @Test
    fun focus_isFollowedByShortBreak_untilCycleIsComplete() {
        assertEquals(TimerPhase.SHORT_BREAK, nextPhase(TimerPhase.FOCUS, completedInCycle = 1, settings))
        assertEquals(TimerPhase.SHORT_BREAK, nextPhase(TimerPhase.FOCUS, completedInCycle = 3, settings))
    }

    @Test
    fun fourthFocus_isFollowedByLongBreak() {
        assertEquals(TimerPhase.LONG_BREAK, nextPhase(TimerPhase.FOCUS, completedInCycle = 4, settings))
    }

    @Test
    fun anyBreak_isFollowedByFocus() {
        assertEquals(TimerPhase.FOCUS, nextPhase(TimerPhase.SHORT_BREAK, completedInCycle = 2, settings))
        assertEquals(TimerPhase.FOCUS, nextPhase(TimerPhase.LONG_BREAK, completedInCycle = 4, settings))
    }

    @Test
    fun daySummary_separatesFocusAndBreaks_andCountsOnlyCompletedPomodoros() {
        val sessions = listOf(
            session(today, TimerPhase.FOCUS, 25),
            session(today, TimerPhase.SHORT_BREAK, 5),
            session(today, TimerPhase.FOCUS, 10, completed = false)
        )

        val summary = summarizeByDay(sessions).single()

        assertEquals(35 * 60_000L, summary.focusMillis)
        assertEquals(5 * 60_000L, summary.breakMillis)
        assertEquals(1, summary.pomodoros)
    }

    @Test
    fun lastSevenDays_alwaysReturnsSevenDays_withZeroForEmptyDays() {
        val sessions = listOf(
            session(today, TimerPhase.FOCUS, 25),
            session(today.minusDays(2), TimerPhase.FOCUS, 50),
            session(today.minusDays(2), TimerPhase.SHORT_BREAK, 5)
        )

        val week = lastSevenDays(sessions, today)

        assertEquals(7, week.size)
        assertEquals(today, week.last().date)
        assertEquals(25 * 60_000L, week.last().focusMillis)
        assertEquals(50 * 60_000L, week[4].focusMillis)
        assertEquals(0L, week[5].focusMillis)
    }
}
