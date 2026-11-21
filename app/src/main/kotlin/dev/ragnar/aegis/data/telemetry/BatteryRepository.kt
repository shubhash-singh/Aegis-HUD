package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.BatteryState
import kotlinx.coroutines.flow.Flow

/**
 * Access to live device battery telemetry.
 */
interface BatteryRepository {
    /**
     * Observes battery state updates via sticky broadcast without long-running services.
     */
    fun observeBatteryState(): Flow<BatteryState>
}
