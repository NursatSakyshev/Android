package com.example.pomodoroapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pomodoroapp.ui.day.DayDetailsScreen
import com.example.pomodoroapp.ui.history.HistoryScreen
import com.example.pomodoroapp.ui.settings.SettingsScreen
import com.example.pomodoroapp.ui.timer.TimerScreen

private const val ANIMATION_MILLIS = 300

@Composable
fun PomodoroApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(ANIMATION_MILLIS)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(ANIMATION_MILLIS)) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(ANIMATION_MILLIS)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(ANIMATION_MILLIS)) }
    ) {
        composable(Screen.Timer.route) {
            TimerScreen(
                onOpenSettings = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                onOpenHistory = { navController.navigate(Screen.History.route) { launchSingleTop = true } }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.navigateUp() })
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.navigateUp() },
                onOpenDay = { date -> navController.navigate(Screen.DayDetails.createRoute(date)) }
            )
        }
        composable(
            route = Screen.DayDetails.route,
            arguments = listOf(navArgument(Screen.DayDetails.ARG_DATE) { type = NavType.StringType })
        ) {
            DayDetailsScreen(onBack = { navController.navigateUp() })
        }
    }
}
