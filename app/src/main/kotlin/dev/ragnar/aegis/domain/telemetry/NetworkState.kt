package dev.ragnar.aegis.domain.telemetry

/**
 * Type of the active network connection.
 */
enum class NetworkType {
    Wifi,
    Cellular,
    Ethernet,
    None,
    Unknown,
}

/**
 * Platform-neutral snapshot of system network connectivity and throughput.
 */
data class NetworkState(
    val isConnected: Boolean = false,
    val type: NetworkType = NetworkType.Unknown,
    val isMetered: Boolean = false,
    val isVpn: Boolean = false,
    val downBytesPerSecond: Long = 0L,
    val upBytesPerSecond: Long = 0L,
)
