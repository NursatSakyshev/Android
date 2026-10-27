package com.example.pomodoroapp.ui.navigation

import java.time.LocalDate

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object Settings : Screen("settings")
    data object History : Screen("history")

    data object DayDetails : Screen("day/{date}") {
        const val ARG_DATE = "date"
        fun createRoute(date: LocalDate) = "day/$date"
    }
}
