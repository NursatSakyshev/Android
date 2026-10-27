package com.example.pomodoroapp.ui.history

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
import androidx.compose.foundation.lazy.items
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
import com.example.pomodoroapp.ui.components.DayCard
import com.example.pomodoroapp.ui.components.ScreenTopBar
import com.example.pomodoroapp.ui.components.SectionCard
import com.example.pomodoroapp.ui.components.StatTile
import com.example.pomodoroapp.ui.components.WeeklyChart
import com.example.pomodoroapp.ui.formatDuration
import com.example.pomodoroapp.ui.theme.Background
import com.example.pomodoroapp.ui.theme.TextSecondary
import com.example.pomodoroapp.viewmodel.HistoryUiState
import com.example.pomodoroapp.viewmodel.HistoryViewModel
import java.time.LocalDate

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenDay: (LocalDate) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryContent(state, onBack, onOpenDay)
}

@Composable
fun HistoryContent(
    state: HistoryUiState,
    onBack: () -> Unit,
    onOpenDay: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ScreenTopBar(title = "History", onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(formatDuration(state.todayFocusMillis), "Focus today", Modifier.weight(1f))
                    StatTile("${state.todayPomodoros} / ${state.dailyGoal}", "Daily goal", Modifier.weight(1f))
                }
            }

            item {
                SectionCard("Last 7 days · minutes") {
                    WeeklyChart(state.week, state.today)
                }
            }

            if (state.weekCategories.isNotEmpty()) {
                item {
                    SectionCard("This week by category") {
                        CategoryBreakdown(state.weekCategories)
                    }
                }
            }

            if (state.days.isEmpty()) {
                item {
                    Text(
                        "No sessions yet.\nStart your first focus on the timer!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                    )
                }
            } else {
                items(state.days, key = { it.date.toEpochDay() }) { day ->
                    DayCard(day, state.today, onClick = { onOpenDay(day.date) })
                }
            }
        }
    }
}
