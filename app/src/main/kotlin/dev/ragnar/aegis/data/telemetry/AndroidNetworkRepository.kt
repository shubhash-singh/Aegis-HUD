package dev.ragnar.aegis.data.telemetry

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.TrafficStats
import android.os.SystemClock
import dev.ragnar.aegis.domain.telemetry.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Implementation of [NetworkRepository] using [ConnectivityManager.NetworkCallback] and [TrafficStats].
 *
 * Fully lifecycle-aware, event-driven, with periodic bandwidth sampling active only while subscribed.
 */
class AndroidNetworkRepository(
    private val context: Context,
) : NetworkRepository {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    override fun observeNetworkState(): Flow<NetworkState> = callbackFlow {
        if (connectivityManager == null) {
            trySend(NetworkState())
            close()
            return@callbackFlow
        }

        var currentCapabilities: NetworkCapabilities? =
            connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        var currentDownSpeed = 0L
        var currentUpSpeed = 0L

        fun emitState() {
            val state = mapCapabilities(currentCapabilities, currentDownSpeed, currentUpSpeed)
            trySend(state)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                currentCapabilities = connectivityManager.getNetworkCapabilities(network)
                emitState()
            }

            override fun onLost(network: Network) {
                currentCapabilities = null
                currentDownSpeed = 0L
                currentUpSpeed = 0L
                emitState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                currentCapabilities = networkCapabilities
                emitState()
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send initial state immediately
        emitState()

        // Background bandwidth sampling coroutine active only while collected
        var previousRxBytes = TrafficStats.getTotalRxBytes()
        var previousTxBytes = TrafficStats.getTotalTxBytes()
        var previousTimeMs = SystemClock.elapsedRealtime()

        val tickerJob = launch {
            while (isActive) {
                delay(1000)
                val now = SystemClock.elapsedRealtime()
                val currentRx = TrafficStats.getTotalRxBytes()
                val currentTx = TrafficStats.getTotalTxBytes()
                val elapsedSec = (now - previousTimeMs) / 1000.0

                if (elapsedSec > 0.5 &&
                    previousRxBytes != TrafficStats.UNSUPPORTED.toLong() &&
                    currentRx != TrafficStats.UNSUPPORTED.toLong()
                ) {
                    val (down, up) = NetworkNormalizer.calculateBandwidth(
                        currentRxBytes = currentRx,
                        previousRxBytes = previousRxBytes,
                        currentTxBytes = currentTx,
                        previousTxBytes = previousTxBytes,
                        elapsedSeconds = elapsedSec,
                    )
                    currentDownSpeed = down
                    currentUpSpeed = up
                    previousRxBytes = currentRx
                    previousTxBytes = currentTx
                    previousTimeMs = now
                    emitState()
                }
            }
        }

        awaitClose {
            tickerJob.cancel()
            runCatching {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }

    private fun mapCapabilities(
        capabilities: NetworkCapabilities?,
        downSpeed: Long,
        upSpeed: Long,
    ): NetworkState {
        if (capabilities == null) {
            return NetworkNormalizer.normalize(
                hasCapabilityInternet = false,
                hasTransportWifi = false,
                hasTransportCellular = false,
                hasTransportEthernet = false,
                hasTransportVpn = false,
                isMetered = false,
                downBytesPerSecond = 0L,
                upBytesPerSecond = 0L,
            )
        }

        return NetworkNormalizer.normalize(
            hasCapabilityInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
            hasTransportWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
            hasTransportCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
            hasTransportEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET),
            hasTransportVpn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN),
            isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
            downBytesPerSecond = downSpeed,
            upBytesPerSecond = upSpeed,
        )
    }
}
