package dev.ragnar.aegis.data.settings

import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing persistent AEGIS HUD user preferences.
 */
interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun setAccentColor(accentColor: AccentColor)
    suspend fun setAnimationSpeed(speed: AnimationSpeed)
    suspend fun setShowRadar(show: Boolean)
    suspend fun setShowSystemStatus(show: Boolean)
    suspend fun setShowTelemetry(show: Boolean)
    suspend fun setShowQuickActions(show: Boolean)
    suspend fun set24HourClock(is24Hour: Boolean)
    suspend fun setShowClockSeconds(showSeconds: Boolean)
    suspend fun resetToDefaults()
}
