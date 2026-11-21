package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.NetworkState
import dev.ragnar.aegis.domain.telemetry.NetworkType

/**
 * Pure mapping and normalization logic for network connectivity.
 */
object NetworkNormalizer {

    /**
     * Determines the unified [NetworkState] from capability and transport flags.
     */
    fun normalize(
        hasCapabilityInternet: Boolean,
        hasTransportWifi: Boolean,
        hasTransportCellular: Boolean,
        hasTransportEthernet: Boolean,
        hasTransportVpn: Boolean,
        isMetered: Boolean,
    ): NetworkState {
        if (!hasCapabilityInternet) {
            return NetworkState(
                isConnected = false,
                type = NetworkType.None,
                isMetered = isMetered,
                isVpn = hasTransportVpn,
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
        )
    }
}
