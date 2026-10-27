package com.example.pomodoroapp.ui.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoroapp.ui.components.CategoryBreakdown
import com.example.pomodoroapp.ui.components.ScreenTopBar
import com.example.pomodoroapp.ui.components.SectionCard
import com.example.pomodoroapp.ui.components.SessionRow
import com.example.pomodoroapp.ui.components.StatTile
import com.example.pomodoroapp.ui.formatDay
import com.example.pomodoroapp.ui.formatDuration
import com.example.pomodoroapp.ui.theme.Background
import com.example.pomodoroapp.ui.theme.TextSecondary
import com.example.pomodoroapp.viewmodel.DayDetailsUiState
import com.example.pomodoroapp.viewmodel.DayDetailsViewModel

@Composable
fun DayDetailsScreen(
    onBack: () -> Unit,
    viewModel: DayDetailsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DayDetailsContent(state, onBack)
}

@Composable
fun DayDetailsContent(state: DayDetailsUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ScreenTopBar(title = formatDay(state.date, state.today), onBack = onBack)

        val summary = state.summary
        if (summary == null) {
            Text(
                "No sessions on this day.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(formatDuration(summary.focusMillis), "Focus time", Modifier.weight(1f))
                    StatTile(formatDuration(summary.breakMillis), "Break time", Modifier.weight(1f))
                    StatTile("🍅 ${summary.pomodoros} / ${state.dailyGoal}", "Pomodoros", Modifier.weight(1f))
                }
            }

            if (state.categories.isNotEmpty()) {
                item {
                    SectionCard("By category") {
                        CategoryBreakdown(state.categories)
                    }
                }
            }

            item {
                SectionCard("Sessions · ${summary.sessions.size}") {
                    summary.sessions.sortedBy { it.startedAt }.forEach { SessionRow(it) }
                }
            }
        }
    }
}
