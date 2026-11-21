package dev.ragnar.aegis.domain.telemetry

import androidx.compose.runtime.Immutable

/**
 * Genuine capability availability state for the Aegis AI command interface.
 */
@Immutable
data class IntelligenceState(
    val contextAvailable: Boolean = true,
    val voiceAvailable: Boolean = false,
    val visionAvailable: Boolean = false,
    val automationAvailable: Boolean = false,
)
