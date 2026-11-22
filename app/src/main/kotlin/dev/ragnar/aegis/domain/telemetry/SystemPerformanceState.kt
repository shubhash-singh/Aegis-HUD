package dev.ragnar.aegis.domain.telemetry

import androidx.compose.runtime.Immutable

/**
 * State holding CPU, RAM, and internal storage usage metrics.
 *
 * Implements genuine data metrics per DEC-009.
 */
@Immutable
data class SystemPerformanceState(
    val cpuUtilizationPercent: Int? = null,
    val ramUsedBytes: Long = 0L,
    val ramTotalBytes: Long = 0L,
    val storageUsedBytes: Long = 0L,
    val storageTotalBytes: Long = 0L,
    /** Proportional Set Size of this launcher process, in bytes. */
    val appMemoryBytes: Long = 0L,
    /** Active Wi-Fi received signal strength in dBm, or null when unavailable/not on Wi-Fi. */
    val wifiSignalDbm: Int? = null,
) {
    val ramPercent: Int
        get() = if (ramTotalBytes > 0) ((ramUsedBytes * 100) / ramTotalBytes).toInt().coerceIn(0, 100) else 0

    val storagePercent: Int
        get() = if (storageTotalBytes > 0) ((storageUsedBytes * 100) / storageTotalBytes).toInt().coerceIn(0, 100) else 0
}
