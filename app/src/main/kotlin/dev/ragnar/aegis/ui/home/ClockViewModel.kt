package dev.ragnar.aegis.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * ViewModel providing live clock and date state for the HUD dashboard.
 *
 * Updates every second via a coroutine scoped to [viewModelScope] (ARCHITECTURE.md Section 9).
 * No unbounded GlobalScope usage. Clock stops automatically when the ViewModel is cleared.
 *
 * The clock state is split into [hours], [minutes], [seconds] to allow the UI
 * to dim seconds independently without recomposing the entire clock (F-003-P2).
 */
class ClockViewModel : ViewModel() {

    private var is24Hour: Boolean = true

    private val _clockState = MutableStateFlow(ClockState.now(is24Hour))
    val clockState: StateFlow<ClockState> = _clockState.asStateFlow()

    fun set24Hour(enabled: Boolean) {
        if (is24Hour != enabled) {
            is24Hour = enabled
            _clockState.value = ClockState.now(is24Hour)
        }
    }

    init {
        viewModelScope.launch {
            while (isActive) {
                _clockState.value = ClockState.now(is24Hour)
                // Calculate delay until the next second boundary for precise ticking
                val now = System.currentTimeMillis()
                val delayMs = 1000L - (now % 1000L)
                delay(delayMs)
            }
        }
    }
}

/**
 * Immutable snapshot of the current time and date for the HUD clock display.
 */
data class ClockState(
    val hours: String,
    val minutes: String,
    val seconds: String,
    val dateFormatted: String,
    val dayOfWeek: String,
) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)

        fun now(is24Hour: Boolean = true): ClockState {
            val time = LocalTime.now()
            val date = LocalDate.now()
            val hourFormatted = if (is24Hour) {
                String.format(Locale.ROOT, "%02d", time.hour)
            } else {
                val h12 = time.hour % 12
                String.format(Locale.ROOT, "%02d", if (h12 == 0) 12 else h12)
            }
            return ClockState(
                hours = hourFormatted,
                minutes = String.format(Locale.ROOT, "%02d", time.minute),
                seconds = String.format(Locale.ROOT, "%02d", time.second),
                dateFormatted = date.format(dateFormatter).uppercase(Locale.ENGLISH),
                dayOfWeek = date.format(dayFormatter).uppercase(Locale.ENGLISH),
            )
        }
    }
}
