package com.example.pomodoroapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoroapp.model.TimerSettings
import com.example.pomodoroapp.ui.components.ScreenTopBar
import com.example.pomodoroapp.ui.components.RowDivider
import com.example.pomodoroapp.ui.components.SectionCard
import com.example.pomodoroapp.ui.components.SliderSettingRow
import com.example.pomodoroapp.ui.components.StepperSettingRow
import com.example.pomodoroapp.ui.components.SwitchSettingRow
import com.example.pomodoroapp.ui.theme.Background
import com.example.pomodoroapp.ui.theme.ErrorRed
import com.example.pomodoroapp.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    SettingsContent(
        settings = settings,
        onBack = onBack,
        onFocusChange = viewModel::setFocusMinutes,
        onShortBreakChange = viewModel::setShortBreakMinutes,
        onLongBreakChange = viewModel::setLongBreakMinutes,
        onSessionsChange = viewModel::setSessionsBeforeLongBreak,
        onGoalChange = viewModel::setDailyGoal,
        onAutoStartChange = viewModel::setAutoStart,
        onResetDefaults = viewModel::resetToDefaults,
        onClearHistory = viewModel::clearHistory
    )
}

private enum class ConfirmDialog { RESET_SETTINGS, CLEAR_HISTORY }

@Composable
fun SettingsContent(
    settings: TimerSettings,
    onBack: () -> Unit,
    onFocusChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
    onLongBreakChange: (Int) -> Unit,
    onSessionsChange: (Int) -> Unit,
    onGoalChange: (Int) -> Unit,
    onAutoStartChange: (Boolean) -> Unit,
    onResetDefaults: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dialog by rememberSaveable { mutableStateOf<ConfirmDialog?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ScreenTopBar(title = "Settings", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionCard("Timer") {
                SliderSettingRow("Focus time", settings.focusMinutes, TimerSettings.FOCUS_RANGE, "min", onFocusChange)
                RowDivider()
                SliderSettingRow("Short break", settings.shortBreakMinutes, TimerSettings.SHORT_BREAK_RANGE, "min", onShortBreakChange)
                RowDivider()
                SliderSettingRow("Long break", settings.longBreakMinutes, TimerSettings.LONG_BREAK_RANGE, "min", onLongBreakChange)
            }

            SectionCard("Cycle") {
                StepperSettingRow(
                    title = "Long break after",
                    subtitle = "focus sessions",
                    value = settings.sessionsBeforeLongBreak,
                    range = TimerSettings.SESSIONS_RANGE,
                    onValueChange = onSessionsChange
                )
                RowDivider()
                StepperSettingRow(
                    title = "Daily goal",
                    subtitle = "pomodoros per day",
                    value = settings.dailyGoal,
                    range = TimerSettings.GOAL_RANGE,
                    onValueChange = onGoalChange
                )
                RowDivider()
                SwitchSettingRow(
                    title = "Auto-start next phase",
                    subtitle = "Start breaks and focus sessions automatically",
                    checked = settings.autoStartNext,
                    onCheckedChange = onAutoStartChange
                )
            }

            SectionCard("Data") {
                TextButton(onClick = { dialog = ConfirmDialog.RESET_SETTINGS }) {
                    Text("Reset settings to defaults", color = Color.White)
                }
                RowDivider()
                TextButton(onClick = { dialog = ConfirmDialog.CLEAR_HISTORY }) {
                    Text("Clear history", color = ErrorRed)
                }
            }
        }
    }

    when (dialog) {
        ConfirmDialog.RESET_SETTINGS -> ConfirmationDialog(
            title = "Reset settings?",
            text = "Focus 25 min, short break 5 min, long break 15 min.",
            confirmText = "Reset",
            onConfirm = { onResetDefaults(); dialog = null },
            onDismiss = { dialog = null }
        )
        ConfirmDialog.CLEAR_HISTORY -> ConfirmationDialog(
            title = "Clear history?",
            text = "All focus sessions and statistics will be deleted.",
            confirmText = "Clear",
            onConfirm = { onClearHistory(); dialog = null },
            onDismiss = { dialog = null }
        )
        null -> Unit
    }
}

@Composable
private fun ConfirmationDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText, color = ErrorRed) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White) } }
    )
}
