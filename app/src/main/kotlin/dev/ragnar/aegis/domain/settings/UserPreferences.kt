package dev.ragnar.aegis.domain.settings

import dev.ragnar.aegis.domain.apps.AppSortOrder

/**
 * Supported accent colors for the AEGIS HUD.
 */
enum class AccentColor(
    val id: String,
    val displayName: String,
    val primaryColorArgb: Long,
    val dimColorArgb: Long,
) {
    Cyan(
        id = "cyan",
        displayName = "AEGIS CYAN",
        primaryColorArgb = 0xFF00C8FF,
        dimColorArgb = 0xFF0090C0,
    ),
    Amber(
        id = "amber",
        displayName = "ALERT AMBER",
        primaryColorArgb = 0xFFFFB830,
        dimColorArgb = 0xFFC08020,
    ),
    Green(
        id = "green",
        displayName = "MATRIX GREEN",
        primaryColorArgb = 0xFF00FF8C,
        dimColorArgb = 0xFF00B360,
    ),
    Crimson(
        id = "crimson",
        displayName = "TACTICAL CRIMSON",
        primaryColorArgb = 0xFFFF3344,
        dimColorArgb = 0xFFB31926,
    );

    companion object {
        fun fromId(id: String?): AccentColor {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Cyan
        }
    }
}

/**
 * Radar rotation speed and animation modes.
 */
enum class AnimationSpeed(
    val id: String,
    val displayName: String,
    val durationMillis: Int,
    val isEnabled: Boolean,
) {
    Fast("fast", "FAST (2S)", 2000, true),
    Normal("normal", "NORMAL (4S)", 4000, true),
    Slow("slow", "SLOW (8S)", 8000, true),
    Disabled("off", "OFF (STATIC)", 0, false);

    companion object {
        fun fromId(id: String?): AnimationSpeed {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Normal
        }
    }
}

/**
 * Immutable user configuration snapshot for the AEGIS HUD launcher.
 */
data class UserPreferences(
    val accentColor: AccentColor = AccentColor.Cyan,
    val animationSpeed: AnimationSpeed = AnimationSpeed.Normal,
    val showRadar: Boolean = true,
    val showSystemStatus: Boolean = true,
    val showTelemetry: Boolean = true,
    val showQuickActions: Boolean = true,
    val is24HourClock: Boolean = true,
    val showClockSeconds: Boolean = true,
    val favoriteApps: Set<String> = emptySet(),
    val appSortOrder: AppSortOrder = AppSortOrder.AZ,
    val drawerGridColumns: Int = 4,
)
