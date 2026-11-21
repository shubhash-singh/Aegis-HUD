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
 * Platform-neutral snapshot of system network connectivity.
 */
data class NetworkState(
    val isConnected: Boolean = false,
    val type: NetworkType = NetworkType.Unknown,
    val isMetered: Boolean = false,
    val isVpn: Boolean = false,
)
