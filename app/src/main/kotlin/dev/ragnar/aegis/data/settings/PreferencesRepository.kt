package dev.ragnar.aegis.data.settings

import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing persistent AEGIS HUD user preferences.
 */
interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun setOperatorName(name: String)
    suspend fun setAccentColor(accentColor: AccentColor)
    suspend fun setAnimationSpeed(speed: AnimationSpeed)
    suspend fun setShowRadar(show: Boolean)
    suspend fun setShowSystemStatus(show: Boolean)
    suspend fun setShowTelemetry(show: Boolean)
    suspend fun setShowQuickActions(show: Boolean)
    suspend fun set24HourClock(is24Hour: Boolean)
    suspend fun setShowClockSeconds(showSeconds: Boolean)
    suspend fun toggleFavorite(componentName: String)
    suspend fun setFavorites(favorites: Set<String>)
    suspend fun clearFavorites()
    suspend fun setAppSortOrder(order: AppSortOrder)
    suspend fun setDrawerGridColumns(columns: Int)
    suspend fun setContextAwareHudEnabled(enabled: Boolean)
    suspend fun toggleContextRule(ruleId: String)
    suspend fun setEnabledContextRules(ruleIds: Set<String>)
    suspend fun setSwipeUpEnabled(enabled: Boolean)
    suspend fun setSwipeDownEnabled(enabled: Boolean)
    suspend fun setLongPressEnabled(enabled: Boolean)
    suspend fun resetToDefaults()
}
