package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.NetworkState
import dev.ragnar.aegis.domain.telemetry.NetworkType
import java.util.Locale

/**
 * Pure mapping, calculation, and normalization logic for network connectivity and throughput.
 */
object NetworkNormalizer {

    /**
     * Determines the unified [NetworkState] from capability and transport flags and live throughput.
     */
    fun normalize(
        hasCapabilityInternet: Boolean,
        hasTransportWifi: Boolean,
        hasTransportCellular: Boolean,
        hasTransportEthernet: Boolean,
        hasTransportVpn: Boolean,
        isMetered: Boolean,
        downBytesPerSecond: Long = 0L,
        upBytesPerSecond: Long = 0L,
    ): NetworkState {
        if (!hasCapabilityInternet) {
            return NetworkState(
                isConnected = false,
                type = NetworkType.None,
                isMetered = isMetered,
                isVpn = hasTransportVpn,
                downBytesPerSecond = 0L,
                upBytesPerSecond = 0L,
            )
        }

        val type = when {
            hasTransportWifi -> NetworkType.Wifi
            hasTransportCellular -> NetworkType.Cellular
            hasTransportEthernet -> NetworkType.Ethernet
            else -> NetworkType.Unknown
        }

        return NetworkState(
            isConnected = true,
            type = type,
            isMetered = isMetered,
            isVpn = hasTransportVpn,
            downBytesPerSecond = downBytesPerSecond.coerceAtLeast(0L),
            upBytesPerSecond = upBytesPerSecond.coerceAtLeast(0L),
        )
    }

    /**
     * Calculates transfer speed (bytes/sec) given byte count deltas and elapsed seconds.
     */
    fun calculateBandwidth(
        currentRxBytes: Long,
        previousRxBytes: Long,
        currentTxBytes: Long,
        previousTxBytes: Long,
        elapsedSeconds: Double,
    ): Pair<Long, Long> {
        if (elapsedSeconds <= 0.0) return Pair(0L, 0L)
        if (currentRxBytes < previousRxBytes || currentTxBytes < previousTxBytes) {
            // Counter wrapped or device rebooted
            return Pair(0L, 0L)
        }
        val rxSpeed = ((currentRxBytes - previousRxBytes) / elapsedSeconds).toLong().coerceAtLeast(0L)
        val txSpeed = ((currentTxBytes - previousTxBytes) / elapsedSeconds).toLong().coerceAtLeast(0L)
        return Pair(rxSpeed, txSpeed)
    }

    /**
     * Formats bytes per second into a human-readable HUD string, e.g. "190kb/s" or "2.4mb/s".
     */
    fun formatSpeed(bytesPerSec: Long): String {
        val safeBytes = bytesPerSec.coerceAtLeast(0L)
        return when {
            safeBytes < 1024 * 1024 -> "${safeBytes / 1024}kb/s"
            else -> String.format(Locale.ROOT, "%.1fmb/s", safeBytes / (1024.0 * 1024.0))
        }
    }
}
