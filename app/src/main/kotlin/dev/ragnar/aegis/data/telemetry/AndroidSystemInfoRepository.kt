package dev.ragnar.aegis.data.telemetry

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.SystemClock
import android.speech.SpeechRecognizer
import dev.ragnar.aegis.domain.telemetry.IntelligenceState
import dev.ragnar.aegis.domain.telemetry.SystemPerformanceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import java.io.RandomAccessFile

/**
 * Android implementation for reading genuine system performance and intelligence state.
 */
class AndroidSystemInfoRepository(
    private val context: Context,
) : SystemInfoRepository {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    @Suppress("DEPRECATION")
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    override fun observeSystemPerformance(): Flow<SystemPerformanceState> = flow {
        var prevTotal: Long? = null
        var prevIdle: Long? = null
        var prevProcessCpuMillis: Long? = null
        var prevWallMillis: Long? = null

        while (true) {
            // 1. Memory
            var ramUsed = 0L
            var ramTotal = 0L
            activityManager?.let { am ->
                val mi = ActivityManager.MemoryInfo()
                am.getMemoryInfo(mi)
                ramTotal = mi.totalMem
                ramUsed = mi.totalMem - mi.availMem
            }

            // PSS is the OS-reported memory attributable to this launcher, including shared pages.
            val appMemoryBytes = try {
                activityManager
                    ?.getProcessMemoryInfo(intArrayOf(Process.myPid()))
                    ?.firstOrNull()
                    ?.totalPss
                    ?.toLong()
                    ?.times(1024L)
                    ?: 0L
            } catch (_: Exception) {
                0L
            }

            // RSSI is deliberately omitted when Android redacts it or no Wi-Fi transport is active.
            val wifiSignalDbm = try {
                wifiManager?.connectionInfo?.rssi?.takeIf { it in -100..0 }
            } catch (_: SecurityException) {
                null
            }

            // 2. Storage
            var storageUsed = 0L
            var storageTotal = 0L
            try {
                val stat = StatFs(Environment.getDataDirectory().path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                val availBlocks = stat.availableBlocksLong
                storageTotal = totalBlocks * blockSize
                storageUsed = (totalBlocks - availBlocks) * blockSize
            } catch (_: Exception) {
            }

            // 3. CPU utilization
            var cpuPercent: Int? = null
            try {
                val loadLine = RandomAccessFile("/proc/stat", "r").use { it.readLine() }
                if (loadLine != null && loadLine.startsWith("cpu ")) {
                    val tokens = loadLine.split("\\s+".toRegex()).drop(1)
                    if (tokens.size >= 7) {
                        val user = tokens[0].toLong()
                        val nice = tokens[1].toLong()
                        val sys = tokens[2].toLong()
                        val idle = tokens[3].toLong()
                        val iowait = tokens[4].toLong()
                        val irq = tokens[5].toLong()
                        val softirq = tokens[6].toLong()

                        val curIdle = idle + iowait
                        val curTotal = user + nice + sys + idle + iowait + irq + softirq

                        if (prevTotal != null && prevIdle != null) {
                            val diffTotal = curTotal - prevTotal
                            val diffIdle = curIdle - prevIdle
                            if (diffTotal > 0) {
                                cpuPercent = (((diffTotal - diffIdle) * 100) / diffTotal).toInt().coerceIn(0, 100)
                            }
                        }
                        prevTotal = curTotal
                        prevIdle = curIdle
                    }
                }
            } catch (_: Exception) {
                // Some Android builds restrict /proc/stat. Fall through to a real process-level sample.
            }

            if (cpuPercent == null) {
                // This is the launcher process's actual CPU time, not a simulated system value. It keeps
                // the CPU HUD informative on devices that deny access to system-wide /proc statistics.
                val processCpuMillis = Process.getElapsedCpuTime()
                val wallMillis = SystemClock.elapsedRealtime()
                val previousCpu = prevProcessCpuMillis
                val previousWall = prevWallMillis
                if (previousCpu != null && previousWall != null) {
                    val elapsedWall = wallMillis - previousWall
                    if (elapsedWall > 0L) {
                        cpuPercent = (((processCpuMillis - previousCpu) * 100L) / elapsedWall)
                            .toInt()
                            .coerceIn(0, 100)
                    }
                }
                prevProcessCpuMillis = processCpuMillis
                prevWallMillis = wallMillis
            }

            emit(
                SystemPerformanceState(
                    cpuUtilizationPercent = cpuPercent,
                    ramUsedBytes = ramUsed,
                    ramTotalBytes = ramTotal,
                    storageUsedBytes = storageUsed,
                    storageTotalBytes = storageTotal,
                    appMemoryBytes = appMemoryBytes,
                    wifiSignalDbm = wifiSignalDbm,
                )
            )

            delay(2500)
        }
    }.flowOn(Dispatchers.IO)

    override fun getIntelligenceState(): IntelligenceState {
        val hasVoice = try {
            SpeechRecognizer.isRecognitionAvailable(context)
        } catch (_: Exception) {
            false
        }

        val hasCamera = try {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        } catch (_: Exception) {
            false
        }

        return IntelligenceState(
            contextAvailable = true,
            voiceAvailable = hasVoice,
            visionAvailable = hasCamera,
            automationAvailable = false,
        )
    }
}
