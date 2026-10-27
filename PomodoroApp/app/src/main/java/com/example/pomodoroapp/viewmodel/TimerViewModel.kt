package com.example.pomodoroapp.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.PomodoroRepository
import com.example.pomodoroapp.model.FocusCategory
import com.example.pomodoroapp.model.TimerPhase
import com.example.pomodoroapp.model.TimerSettings
import com.example.pomodoroapp.model.TimerStatus
import com.example.pomodoroapp.model.date
import com.example.pomodoroapp.model.nextPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TimerUiState(
    val phase: TimerPhase = TimerPhase.FOCUS,
    val status: TimerStatus = TimerStatus.Idle,
    val remainingMillis: Long = TimerSettings().durationMillis(TimerPhase.FOCUS),
    val totalMillis: Long = TimerSettings().durationMillis(TimerPhase.FOCUS),
    val category: FocusCategory = FocusCategory.WORK,
    val task: String = "",
    val completedInCycle: Int = 0,
    val sessionsBeforeLongBreak: Int = TimerSettings().sessionsBeforeLongBreak,
    val todayPomodoros: Int = 0,
    val dailyGoal: Int = TimerSettings().dailyGoal
) {
    val progress: Float get() = if (totalMillis == 0L) 0f else remainingMillis.toFloat() / totalMillis
    val isRunning: Boolean get() = status is TimerStatus.Running
    val isIdle: Boolean get() = status is TimerStatus.Idle
}

class TimerViewModel(
    private val repository: PomodoroRepository = PomodoroRepository.instance,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() }
) : ViewModel() {

    private data class TimerCore(
        val phase: TimerPhase = TimerPhase.FOCUS,
        val status: TimerStatus = TimerStatus.Idle,
        val remainingMillis: Long = 0L,
        val totalMillis: Long = 0L,
        val startedAt: Long = 0L,
        val completedInCycle: Int = 0,
        val category: FocusCategory = FocusCategory.WORK,
        val task: String = ""
    )

    private val core = MutableStateFlow(TimerCore())
    private var tickJob: Job? = null

    val uiState: StateFlow<TimerUiState> =
        combine(core, repository.settings, repository.sessions) { core, settings, sessions ->
            val idleDuration = settings.durationMillis(core.phase)
            val today = LocalDate.now()
            TimerUiState(
                phase = core.phase,
                status = core.status,
                remainingMillis = if (core.status is TimerStatus.Idle) idleDuration else core.remainingMillis,
                totalMillis = if (core.status is TimerStatus.Idle) idleDuration else core.totalMillis,
                category = core.category,
                task = core.task,
                completedInCycle = core.completedInCycle,
                sessionsBeforeLongBreak = settings.sessionsBeforeLongBreak,
                todayPomodoros = sessions.count {
                    it.phase == TimerPhase.FOCUS && it.completed && it.date() == today
                },
                dailyGoal = settings.dailyGoal
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())

    fun toggle() {
        if (core.value.status is TimerStatus.Running) pause() else start()
    }

    fun start() {
        val current = core.value
        if (current.status is TimerStatus.Running) return

        val (remaining, total, startedAt) = when (val status = current.status) {
            is TimerStatus.Paused -> Triple(status.remainingMillis, current.totalMillis, current.startedAt)
            else -> {
                val duration = repository.settings.value.durationMillis(current.phase)
                Triple(duration, duration, System.currentTimeMillis())
            }
        }

        core.update {
            it.copy(
                status = TimerStatus.Running(endsAt = clock() + remaining),
                remainingMillis = remaining,
                totalMillis = total,
                startedAt = startedAt
            )
        }
        startTicking()
    }

    fun pause() {
        val status = core.value.status as? TimerStatus.Running ?: return
        tickJob?.cancel()
        val remaining = (status.endsAt - clock()).coerceAtLeast(0L)
        core.update { it.copy(status = TimerStatus.Paused(remaining), remainingMillis = remaining) }
    }

    fun stop() {
        val current = core.value
        if (current.status is TimerStatus.Idle) return
        tickJob?.cancel()

        val focusedMillis = current.totalMillis - current.remainingMillis
        if (current.phase == TimerPhase.FOCUS && focusedMillis >= 60_000L) {
            repository.addSession(
                current.category, current.phase, current.startedAt, focusedMillis,
                completed = false, task = current.task
            )
        }
        val cycle = if (current.phase == TimerPhase.LONG_BREAK) 0 else current.completedInCycle
        core.update { it.copy(phase = TimerPhase.FOCUS, status = TimerStatus.Idle, completedInCycle = cycle) }
    }

    fun skipBreak() {
        val current = core.value
        if (!current.phase.isBreak) return
        tickJob?.cancel()
        val cycle = if (current.phase == TimerPhase.LONG_BREAK) 0 else current.completedInCycle
        core.update { it.copy(phase = TimerPhase.FOCUS, status = TimerStatus.Idle, completedInCycle = cycle) }
    }

    fun selectCategory(category: FocusCategory) {
        core.update { it.copy(category = category) }
    }

    fun updateTask(task: String) {
        core.update { it.copy(task = task.take(MAX_TASK_LENGTH)) }
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (true) {
                val status = core.value.status as? TimerStatus.Running ?: return@launch
                val remaining = (status.endsAt - clock()).coerceAtLeast(0L)
                core.update { it.copy(remainingMillis = remaining) }
                if (remaining == 0L) break
                delay(TICK_MILLIS)
            }
            finishPhase()
            if (repository.settings.value.autoStartNext) start()
        }
    }

    private fun finishPhase() {
        val finished = core.value
        val settings = repository.settings.value

        repository.addSession(
            finished.category, finished.phase, finished.startedAt, finished.totalMillis,
            completed = true, task = finished.task
        )

        val completed = if (finished.phase == TimerPhase.FOCUS) finished.completedInCycle + 1 else finished.completedInCycle
        val next = nextPhase(finished.phase, completed, settings)
        val cycle = if (finished.phase == TimerPhase.LONG_BREAK) 0 else completed

        core.update { it.copy(phase = next, status = TimerStatus.Idle, completedInCycle = cycle) }
    }

    private companion object {
        const val TICK_MILLIS = 200L
        const val MAX_TASK_LENGTH = 60
    }
}
