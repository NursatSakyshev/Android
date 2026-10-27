package com.example.pomodoroapp.ui.timer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoroapp.ui.background
import com.example.pomodoroapp.ui.components.CycleDots
import com.example.pomodoroapp.ui.components.GlassChip
import com.example.pomodoroapp.ui.components.GlassIconButton
import com.example.pomodoroapp.ui.components.TimerRing
import com.example.pomodoroapp.ui.formatClock
import com.example.pomodoroapp.ui.subtitle
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme
import com.example.pomodoroapp.viewmodel.TimerUiState
import com.example.pomodoroapp.viewmodel.TimerViewModel

@Composable
fun TimerScreen(
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: TimerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showCategories by rememberSaveable { mutableStateOf(false) }

    TimerContent(
        state = state,
        onToggle = viewModel::toggle,
        onStop = viewModel::stop,
        onSkipBreak = viewModel::skipBreak,
        onOpenCategories = { showCategories = true },
        onOpenSettings = onOpenSettings,
        onOpenHistory = onOpenHistory
    )

    if (showCategories) {
        CategorySheet(
            selected = state.category,
            task = state.task,
            onSelect = viewModel::selectCategory,
            onTaskChange = viewModel::updateTask,
            onDismiss = { showCategories = false }
        )
    }
}

@Composable
fun TimerContent(
    state: TimerUiState,
    onToggle: () -> Unit,
    onStop: () -> Unit,
    onSkipBreak: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(targetState = state.category, label = "background") { category ->
            Image(
                painter = painterResource(category.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.45f))))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            TopActions(state, onOpenHistory, onOpenCategories, onOpenSettings)

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val isLandscape = maxWidth > maxHeight
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Ring(state, Modifier.fillMaxHeight(0.9f))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            TaskText(state)
                            CycleInfo(state)
                            Controls(state, onToggle, onStop, onSkipBreak)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TaskText(state)
                        Spacer(Modifier.weight(1f))
                        Ring(state, Modifier.fillMaxWidth(0.8f))
                        Spacer(Modifier.height(24.dp))
                        CycleInfo(state)
                        Spacer(Modifier.weight(1f))
                        Controls(state, onToggle, onStop, onSkipBreak)
                        Spacer(Modifier.weight(0.6f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopActions(
    state: TimerUiState,
    onOpenHistory: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(icon = Icons.Outlined.History, contentDescription = "History", onClick = onOpenHistory, size = 48.dp)
        Spacer(Modifier.weight(1f))
        GlassChip(text = state.category.title, icon = Icons.Filled.Edit, onClick = onOpenCategories)
        Spacer(Modifier.weight(1f))
        GlassIconButton(icon = Icons.Outlined.Settings, contentDescription = "Settings", onClick = onOpenSettings, size = 48.dp)
    }
}

@Composable
private fun TaskText(state: TimerUiState) {
    if (state.task.isNotBlank()) {
        Text(
            state.task,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Ring(state: TimerUiState, modifier: Modifier) {
    TimerRing(
        progress = state.progress,
        timeText = formatClock(state.remainingMillis),
        subtitle = state.phase.subtitle,
        modifier = modifier
    )
}

@Composable
private fun CycleInfo(state: TimerUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CycleDots(completed = state.completedInCycle, total = state.sessionsBeforeLongBreak)
        Spacer(Modifier.height(8.dp))
        Text(
            "Today ${state.todayPomodoros} / ${state.dailyGoal} 🍅",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun Controls(state: TimerUiState, onToggle: () -> Unit, onStop: () -> Unit, onSkipBreak: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        GlassIconButton(
            icon = if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (state.isRunning) "Pause" else "Start",
            onClick = onToggle
        )
        GlassIconButton(
            icon = Icons.Filled.Stop,
            contentDescription = "Stop",
            onClick = onStop,
            enabled = !state.isIdle
        )
        if (state.phase.isBreak) {
            GlassIconButton(icon = Icons.Filled.SkipNext, contentDescription = "Skip break", onClick = onSkipBreak)
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun TimerContentPreview() {
    PomodoroAppTheme {
        TimerContent(
            state = TimerUiState(completedInCycle = 2, todayPomodoros = 3),
            onToggle = {}, onStop = {}, onSkipBreak = {}, onOpenCategories = {},
            onOpenSettings = {}, onOpenHistory = {}
        )
    }
}
