package dev.ragnar.aegis.data.diagnostics

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import dev.ragnar.aegis.domain.diagnostics.DiagnosticsSnapshot
import dev.ragnar.aegis.domain.diagnostics.LauncherDiagnostics
import dev.ragnar.aegis.domain.diagnostics.PermissionStatus
import dev.ragnar.aegis.domain.diagnostics.SystemHardwareInfo
import java.util.Locale

/**
 * Android implementation of [DiagnosticsRepository] using genuine system APIs (DEC-009).
 */
class AndroidDiagnosticsRepository(
    private val context: Context,
    private val launchableAppsCountProvider: () -> Int = { 0 },
    private val favoriteAppsCountProvider: () -> Int = { 0 },
) : DiagnosticsRepository {

    private val appContext = context.applicationContext

    override fun getSnapshot(): DiagnosticsSnapshot {
        val launcher = readLauncherDiagnostics()
        val system = readSystemHardwareInfo()
        val permissions = readPermissions()
        val logs = AegisLogBuffer.getEntries()

        return DiagnosticsSnapshot(
            launcherDiagnostics = launcher,
            systemInfo = system,
            permissions = permissions,
            recentLogs = logs,
        )
    }

    override fun copyToClipboard(text: String): Boolean = runCatching {
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            ?: return false
        val clip = ClipData.newPlainText("AEGIS Diagnostics", text)
        clipboard.setPrimaryClip(clip)
        AegisLogBuffer.info("DEV_CONSOLE", "Diagnostic snapshot copied to clipboard")
        true
    }.getOrDefault(false)

    private fun readLauncherDiagnostics(): LauncherDiagnostics {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val allocatedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        val allocatedMb = allocatedMemory / (1024f * 1024f)
        val maxMb = maxMemory / (1024f * 1024f)
        val percent = if (maxMemory > 0) ((allocatedMemory.toDouble() / maxMemory) * 100).toInt() else 0

        val uptimeMillis = SystemClock.elapsedRealtime()
        val hours = (uptimeMillis / 1000) / 3600
        val minutes = ((uptimeMillis / 1000) % 3600) / 60
        val seconds = (uptimeMillis / 1000) % 60
        val uptimeFormatted = String.format(Locale.ROOT, "%02dH %02dM %02dS", hours, minutes, seconds)

        return LauncherDiagnostics(
            processUptimeMillis = uptimeMillis,
            processUptimeFormatted = uptimeFormatted,
            heapAllocatedMb = allocatedMb,
            heapMaxMb = maxMb,
            heapPercent = percent,
            launchableAppsCount = launchableAppsCountProvider(),
            favoriteAppsCount = favoriteAppsCountProvider(),
        )
    }

    private fun readSystemHardwareInfo(): SystemHardwareInfo {
        val abis = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
        val patch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            "N/A"
        }

        return SystemHardwareInfo(
            deviceModel = Build.MODEL,
            manufacturer = Build.MANUFACTURER.uppercase(Locale.ROOT),
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            cpuAbi = abis,
            securityPatch = patch,
        )
    }

    private fun readPermissions(): List<PermissionStatus> {
        val tracked = listOf(
            Manifest.permission.ACCESS_NETWORK_STATE to "ACCESS_NETWORK_STATE",
            Manifest.permission.CAMERA to "CAMERA (Flashlight)",
            "android.permission.RECEIVE_BOOT_COMPLETED" to "RECEIVE_BOOT_COMPLETED",
        )

        return tracked.map { (permission, shortName) ->
            val granted = ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
            PermissionStatus(
                permissionName = permission,
                shortName = shortName,
                isGranted = granted,
            )
        }
    }
}
