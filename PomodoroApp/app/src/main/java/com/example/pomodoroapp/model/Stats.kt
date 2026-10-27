package com.example.pomodoroapp.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DaySummary(
    val date: LocalDate,
    val focusMillis: Long,
    val breakMillis: Long,
    val pomodoros: Int,
    val sessions: List<FocusSession>
)

data class DayBar(val date: LocalDate, val focusMillis: Long)

data class CategoryTotal(val category: FocusCategory, val focusMillis: Long)

fun FocusSession.date(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.ofEpochMilli(startedAt).atZone(zone).toLocalDate()

fun summarizeByDay(sessions: List<FocusSession>): List<DaySummary> =
    sessions.groupBy { it.date() }
        .map { (date, daySessions) ->
            DaySummary(
                date = date,
                focusMillis = daySessions.filter { it.phase == TimerPhase.FOCUS }.sumOf { it.durationMillis },
                breakMillis = daySessions.filter { it.phase.isBreak }.sumOf { it.durationMillis },
                pomodoros = daySessions.count { it.phase == TimerPhase.FOCUS && it.completed },
                sessions = daySessions.sortedByDescending { it.startedAt }
            )
        }
        .sortedByDescending { it.date }

fun lastSevenDays(sessions: List<FocusSession>, today: LocalDate): List<DayBar> {
    val focusByDate = sessions
        .filter { it.phase == TimerPhase.FOCUS }
        .groupBy { it.date() }
        .mapValues { (_, daySessions) -> daySessions.sumOf { it.durationMillis } }

    return (6 downTo 0)
        .map { today.minusDays(it.toLong()) }
        .map { DayBar(it, focusByDate[it] ?: 0L) }
}

fun categoryTotals(sessions: List<FocusSession>): List<CategoryTotal> =
    sessions.filter { it.phase == TimerPhase.FOCUS }
        .groupBy { it.category }
        .map { (category, list) -> CategoryTotal(category, list.sumOf { it.durationMillis }) }
        .sortedByDescending { it.focusMillis }
