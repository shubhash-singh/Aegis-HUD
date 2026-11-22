package dev.ragnar.aegis.ui.home.panels

import dev.ragnar.aegis.domain.telemetry.SystemPerformanceState
import dev.ragnar.aegis.ui.telemetry.TelemetryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for formatting and calculation logic used by the floating panels.
 */
class FloatingPanelFormatterTest {

    @Test
    fun formatStorage_validValues_formatsGbAndPercentage() {
        val used = 64L * 1024L * 1024L * 1024L
        val total = 128L * 1024L * 1024L * 1024L
        val formatted = TelemetryViewModel.formatStorage(used, total)
        assertTrue("Must contain free and total GB: $formatted", formatted.contains("64.0/128 GB"))
    }

    @Test
    fun formatStorage_zeroTotal_returnsUnavailableMarker() {
        val formatted = TelemetryViewModel.formatStorage(0L, 0L)
        assertEquals("---", formatted)
    }

    @Test
    fun telemetryFormatters_showOnlyAvailableWifiAndProcessMemory() {
        assertEquals("-58 dBm", TelemetryViewModel.formatWifiSignal(-58))
        assertEquals("---", TelemetryViewModel.formatWifiSignal(null))
        assertEquals("48 MB", TelemetryViewModel.formatMemory(48L * 1024L * 1024L))
        assertEquals("---", TelemetryViewModel.formatMemory(0L))
    }

    @Test
    fun systemPerformanceState_percentageCalculations() {
        val state = SystemPerformanceState(
            cpuUtilizationPercent = 42,
            ramUsedBytes = 4L * 1024L * 1024L * 1024L,
            ramTotalBytes = 8L * 1024L * 1024L * 1024L,
            storageUsedBytes = 100L,
            storageTotalBytes = 400L,
        )

        assertEquals(50, state.ramPercent)
        assertEquals(25, state.storagePercent)
        assertEquals(42, state.cpuUtilizationPercent)
    }

    @Test
    fun systemPerformanceState_zeroDenominatorSafeguards() {
        val emptyState = SystemPerformanceState()
        assertEquals(0, emptyState.ramPercent)
        assertEquals(0, emptyState.storagePercent)
    }
}
