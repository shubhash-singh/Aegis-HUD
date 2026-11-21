package dev.ragnar.aegis.domain.diagnostics

/**
 * Severity levels for the internal AEGIS diagnostics logger.
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR;
}

/**
 * A single timestamped event log entry in the diagnostics ring buffer.
 */
data class LogEntry(
    val timestampMillis: Long,
    val timestampFormatted: String,
    val level: LogLevel,
    val tag: String,
    val message: String,
)

/**
 * Live runtime metrics for the AEGIS launcher process.
 */
data class LauncherDiagnostics(
    val processUptimeMillis: Long,
    val processUptimeFormatted: String,
    val heapAllocatedMb: Float,
    val heapMaxMb: Float,
    val heapPercent: Int,
    val launchableAppsCount: Int,
    val favoriteAppsCount: Int,
)

/**
 * Device platform and hardware information.
 */
data class SystemHardwareInfo(
    val deviceModel: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildId: String,
    val cpuAbi: String,
    val securityPatch: String,
)

/**
 * Status of an individual Android manifest permission.
 */
data class PermissionStatus(
    val permissionName: String,
    val shortName: String,
    val isGranted: Boolean,
)

/**
 * Complete snapshot of developer diagnostics.
 */
data class DiagnosticsSnapshot(
    val launcherDiagnostics: LauncherDiagnostics,
    val systemInfo: SystemHardwareInfo,
    val permissions: List<PermissionStatus>,
    val recentLogs: List<LogEntry>,
) {
    /**
     * Generates a clean markdown formatted diagnostic report.
     */
    fun toFormattedMarkdown(): String = buildString {
        appendLine("# AEGIS HUD // DIAGNOSTIC SNAPSHOT")
        appendLine("Generated: ${launcherDiagnostics.processUptimeFormatted} uptime")
        appendLine()
        appendLine("## 1. Process & Memory")
        appendLine("- Process Uptime: ${launcherDiagnostics.processUptimeFormatted}")
        appendLine("- Heap Allocated: ${String.format(java.util.Locale.ROOT, "%.1f", launcherDiagnostics.heapAllocatedMb)} MB / ${String.format(java.util.Locale.ROOT, "%.1f", launcherDiagnostics.heapMaxMb)} MB (${launcherDiagnostics.heapPercent}%)")
        appendLine("- Launchable Apps Tracked: ${launcherDiagnostics.launchableAppsCount}")
        appendLine("- Pinned Favorites: ${launcherDiagnostics.favoriteAppsCount}")
        appendLine()
        appendLine("## 2. Platform & Hardware")
        appendLine("- Device: ${systemInfo.manufacturer} ${systemInfo.deviceModel}")
        appendLine("- Android Version: Android ${systemInfo.androidVersion} (API ${systemInfo.apiLevel})")
        appendLine("- Build ID: ${systemInfo.buildId}")
        appendLine("- CPU ABI: ${systemInfo.cpuAbi}")
        appendLine("- Security Patch: ${systemInfo.securityPatch}")
        appendLine()
        appendLine("## 3. Permissions Audit")
        permissions.forEach { perm ->
            val status = if (perm.isGranted) "[GRANTED]" else "[DENIED]"
            appendLine("- ${perm.shortName}: $status")
        }
        appendLine()
        appendLine("## 4. Recent Logs (${recentLogs.size} entries)")
        recentLogs.takeLast(25).forEach { entry ->
            appendLine("[${entry.timestampFormatted}] [${entry.level}] ${entry.tag}: ${entry.message}")
        }
    }
}
