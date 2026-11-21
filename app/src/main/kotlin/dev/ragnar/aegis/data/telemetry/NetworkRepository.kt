package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.NetworkState
import kotlinx.coroutines.flow.Flow

/**
 * Access to live network connectivity state.
 */
interface NetworkRepository {
    /**
     * Observes connectivity changes via [android.net.ConnectivityManager.NetworkCallback].
     */
    fun observeNetworkState(): Flow<NetworkState>
}
