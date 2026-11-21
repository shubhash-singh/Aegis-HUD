package dev.ragnar.aegis.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aegis_user_preferences")

/**
 * Implementation of [PreferencesRepository] backed by AndroidX DataStore Preferences.
 */
class DataStorePreferencesRepository(
    private val context: Context,
) : PreferencesRepository {

    private object PreferencesKeys {
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val ANIMATION_SPEED = stringPreferencesKey("animation_speed")
        val SHOW_RADAR = booleanPreferencesKey("show_radar")
        val SHOW_SYSTEM_STATUS = booleanPreferencesKey("show_system_status")
        val SHOW_TELEMETRY = booleanPreferencesKey("show_telemetry")
        val SHOW_QUICK_ACTIONS = booleanPreferencesKey("show_quick_actions")
        val IS_24_HOUR_CLOCK = booleanPreferencesKey("is_24_hour_clock")
        val SHOW_CLOCK_SECONDS = booleanPreferencesKey("show_clock_seconds")
        val FAVORITE_APPS = stringSetPreferencesKey("favorite_apps")
        val APP_SORT_ORDER = stringPreferencesKey("app_sort_order")
        val DRAWER_GRID_COLUMNS = intPreferencesKey("drawer_grid_columns")
    }

    override val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    override suspend fun setAccentColor(accentColor: AccentColor) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCENT_COLOR] = accentColor.id
        }
    }

    override suspend fun setAnimationSpeed(speed: AnimationSpeed) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATION_SPEED] = speed.id
        }
    }

    override suspend fun setShowRadar(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_RADAR] = show
        }
    }

    override suspend fun setShowSystemStatus(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_SYSTEM_STATUS] = show
        }
    }

    override suspend fun setShowTelemetry(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TELEMETRY] = show
        }
    }

    override suspend fun setShowQuickActions(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_QUICK_ACTIONS] = show
        }
    }

    override suspend fun set24HourClock(is24Hour: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_24_HOUR_CLOCK] = is24Hour
        }
    }

    override suspend fun setShowClockSeconds(showSeconds: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_CLOCK_SECONDS] = showSeconds
        }
    }

    override suspend fun toggleFavorite(componentName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FAVORITE_APPS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_APPS] = if (current.contains(componentName)) {
                current - componentName
            } else {
                current + componentName
            }
        }
    }

    override suspend fun setFavorites(favorites: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_APPS] = favorites
        }
    }

    override suspend fun clearFavorites() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_APPS] = emptySet()
        }
    }

    override suspend fun setAppSortOrder(order: AppSortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_SORT_ORDER] = order.id
        }
    }

    override suspend fun setDrawerGridColumns(columns: Int) {
        val clamped = columns.coerceIn(3, 5)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DRAWER_GRID_COLUMNS] = clamped
        }
    }

    override suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val accentId = preferences[PreferencesKeys.ACCENT_COLOR]
        val speedId = preferences[PreferencesKeys.ANIMATION_SPEED]
        val sortId = preferences[PreferencesKeys.APP_SORT_ORDER]
        val columns = preferences[PreferencesKeys.DRAWER_GRID_COLUMNS] ?: 4
        return UserPreferences(
            accentColor = AccentColor.fromId(accentId),
            animationSpeed = AnimationSpeed.fromId(speedId),
            showRadar = preferences[PreferencesKeys.SHOW_RADAR] ?: true,
            showSystemStatus = preferences[PreferencesKeys.SHOW_SYSTEM_STATUS] ?: true,
            showTelemetry = preferences[PreferencesKeys.SHOW_TELEMETRY] ?: true,
            showQuickActions = preferences[PreferencesKeys.SHOW_QUICK_ACTIONS] ?: true,
            is24HourClock = preferences[PreferencesKeys.IS_24_HOUR_CLOCK] ?: true,
            showClockSeconds = preferences[PreferencesKeys.SHOW_CLOCK_SECONDS] ?: true,
            favoriteApps = preferences[PreferencesKeys.FAVORITE_APPS] ?: emptySet(),
            appSortOrder = AppSortOrder.fromId(sortId),
            drawerGridColumns = columns.coerceIn(3, 5),
        )
    }
}
