package dev.ragnar.aegis.data.telemetry

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dev.ragnar.aegis.domain.telemetry.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of [NetworkRepository] using [ConnectivityManager.NetworkCallback].
 *
 * Fully lifecycle-aware, event-driven, without background polling.
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

        fun updateState() {
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            val state = mapCapabilities(capabilities)
            trySend(state)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateState()
            }

            override fun onLost(network: Network) {
                updateState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                trySend(mapCapabilities(networkCapabilities))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send current status immediately
        updateState()

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private fun mapCapabilities(capabilities: NetworkCapabilities?): NetworkState {
        if (capabilities == null) {
            return NetworkNormalizer.normalize(
                hasCapabilityInternet = false,
                hasTransportWifi = false,
                hasTransportCellular = false,
                hasTransportEthernet = false,
                hasTransportVpn = false,
                isMetered = false,
            )
        }

        return NetworkNormalizer.normalize(
            hasCapabilityInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
            hasTransportWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
            hasTransportCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
            hasTransportEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET),
            hasTransportVpn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN),
            isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
        )
    }
}
