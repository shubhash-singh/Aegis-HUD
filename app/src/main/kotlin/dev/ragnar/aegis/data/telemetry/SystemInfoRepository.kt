package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.IntelligenceState
import dev.ragnar.aegis.domain.telemetry.SystemPerformanceState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for retrieving system performance (CPU, RAM, Storage)
 * and hardware capability intelligence states.
 */
interface SystemInfoRepository {
    fun observeSystemPerformance(): Flow<SystemPerformanceState>
    fun getIntelligenceState(): IntelligenceState
}
