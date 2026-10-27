package com.example.pomodoroapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.FocusCategory
import com.example.pomodoroapp.model.TimerPhase

@get:DrawableRes
val FocusCategory.background: Int
    get() = when (this) {
        FocusCategory.WORK -> R.drawable.bg_work
        FocusCategory.STUDY -> R.drawable.bg_study
        FocusCategory.WORKOUT -> R.drawable.bg_workout
        FocusCategory.READING -> R.drawable.bg_reading
        FocusCategory.MEDITATION -> R.drawable.bg_meditation
        FocusCategory.OTHERS -> R.drawable.bg_others
    }

val FocusCategory.color: Color
    get() = when (this) {
        FocusCategory.WORK -> Color(0xFF4FB3BF)
        FocusCategory.STUDY -> Color(0xFF9FA8DA)
        FocusCategory.WORKOUT -> Color(0xFFFFA726)
        FocusCategory.READING -> Color(0xFFE57373)
        FocusCategory.MEDITATION -> Color(0xFF81C784)
        FocusCategory.OTHERS -> Color(0xFF64B5F6)
    }

val FocusCategory.icon: ImageVector
    get() = when (this) {
        FocusCategory.WORK -> Icons.Filled.Work
        FocusCategory.STUDY -> Icons.Filled.School
        FocusCategory.WORKOUT -> Icons.Filled.FitnessCenter
        FocusCategory.READING -> Icons.Filled.MenuBook
        FocusCategory.MEDITATION -> Icons.Filled.SelfImprovement
        FocusCategory.OTHERS -> Icons.Filled.MoreHoriz
    }

val TimerPhase.subtitle: String
    get() = when (this) {
        TimerPhase.FOCUS -> "Focus on your task"
        TimerPhase.SHORT_BREAK -> "Take a short break"
        TimerPhase.LONG_BREAK -> "Time for a long rest"
    }
