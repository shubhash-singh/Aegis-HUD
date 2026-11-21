package dev.ragnar.aegis.domain.diagnostics

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [DiagnosticsSnapshot] formatting — F-014 (Developer Console).
 */
class DiagnosticsSnapshotTest {

    @Test
    fun `UT-014-04 snapshot toFormattedMarkdown produces complete markdown report`() {
        val snapshot = DiagnosticsSnapshot(
            launcherDiagnostics = LauncherDiagnostics(
                processUptimeMillis = 3600000L,
                processUptimeFormatted = "01H 00M 00S",
                heapAllocatedMb = 24.5f,
                heapMaxMb = 256.0f,
                heapPercent = 10,
                launchableAppsCount = 42,
                favoriteAppsCount = 5,
            ),
            systemInfo = SystemHardwareInfo(
                deviceModel = "Pixel 9 Pro",
                manufacturer = "GOOGLE",
                androidVersion = "15",
                apiLevel = 35,
                buildId = "AP2A.240805.005",
                cpuAbi = "arm64-v8a",
                securityPatch = "2026-11-05",
            ),
            permissions = listOf(
                PermissionStatus("android.permission.ACCESS_NETWORK_STATE", "ACCESS_NETWORK_STATE", true),
                PermissionStatus("android.permission.CAMERA", "CAMERA", false),
            ),
            recentLogs = listOf(
                LogEntry(1000L, "12:00:00.000", LogLevel.INFO, "AEGIS", "System ready"),
            ),
        )

        val markdown = snapshot.toFormattedMarkdown()

        assertTrue(markdown.contains("# AEGIS HUD // DIAGNOSTIC SNAPSHOT"))
        assertTrue(markdown.contains("Pixel 9 Pro"))
        assertTrue(markdown.contains("GOOGLE"))
        assertTrue(markdown.contains("Android 15 (API 35)"))
        assertTrue(markdown.contains("24.5 MB"))
        assertTrue(markdown.contains("42"))
        assertTrue(markdown.contains("[GRANTED]"))
        assertTrue(markdown.contains("[DENIED]"))
        assertTrue(markdown.contains("System ready"))
    }
}
