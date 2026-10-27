package com.example.pomodoroapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pomodoroapp.model.CategoryTotal
import com.example.pomodoroapp.model.DayBar
import com.example.pomodoroapp.model.DaySummary
import com.example.pomodoroapp.model.FocusSession
import com.example.pomodoroapp.model.TimerPhase
import com.example.pomodoroapp.ui.color
import com.example.pomodoroapp.ui.formatDay
import com.example.pomodoroapp.ui.formatDuration
import com.example.pomodoroapp.ui.formatTime
import com.example.pomodoroapp.ui.icon
import com.example.pomodoroapp.ui.shortDayName
import com.example.pomodoroapp.ui.theme.SurfaceDark
import com.example.pomodoroapp.ui.theme.SurfaceHigh
import com.example.pomodoroapp.ui.theme.TextSecondary
import com.example.pomodoroapp.ui.theme.Tomato
import java.time.LocalDate

@Composable
fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
fun WeeklyChart(bars: List<DayBar>, today: LocalDate, modifier: Modifier = Modifier) {
    val max = (bars.maxOfOrNull { it.focusMillis } ?: 0L).coerceAtLeast(1L)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        bars.forEach { bar ->
            val isToday = bar.date == today
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    if (bar.focusMillis > 0) "${bar.focusMillis / 60_000}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(0.75f * bar.focusMillis / max + 0.02f)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (isToday) Tomato else Color.White.copy(alpha = 0.7f))
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    shortDayName(bar.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isToday) Color.White else TextSecondary
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdown(totals: List<CategoryTotal>, modifier: Modifier = Modifier) {
    val max = (totals.maxOfOrNull { it.focusMillis } ?: 0L).coerceAtLeast(1L)

    Column(modifier = modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        totals.forEach { total ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(total.category.icon, contentDescription = null, tint = total.category.color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    total.category.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.width(96.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SurfaceHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(total.focusMillis.toFloat() / max)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(total.category.color)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    formatDuration(total.focusMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.width(56.dp)
                )
            }
        }
    }
}

@Composable
fun DayCard(summary: DaySummary, today: LocalDate, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                formatDay(summary.date, today),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text("🍅 ${summary.pomodoros}", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open day",
                tint = TextSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        TotalRow("Focus time", formatDuration(summary.focusMillis))
        TotalRow("Break time", formatDuration(summary.breakMillis))
    }
}

@Composable
private fun TotalRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.White, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}

@Composable
fun SessionRow(session: FocusSession, modifier: Modifier = Modifier) {
    val isFocus = session.phase == TimerPhase.FOCUS
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isFocus) session.category.color.copy(alpha = 0.2f) else SurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            if (isFocus) {
                Icon(session.category.icon, contentDescription = null, tint = session.category.color, modifier = Modifier.size(18.dp))
            } else {
                Text("☕", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            val title = when {
                !isFocus -> session.phase.title
                session.task.isNotBlank() -> "${session.category.title} · ${session.task}"
                else -> session.category.title
            }
            Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val status = if (session.completed) "" else " · stopped early"
            Text("${formatTime(session.startedAt)}$status", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Text(formatDuration(session.durationMillis), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
