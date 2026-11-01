package dev.ragnar.aegis.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * AEGIS HUD color scheme.
 *
 * All color tokens from UI_UX_SPECIFICATION.md Section 2.
 * Provided as a CompositionLocal via [AegisTheme] — access with `AegisTheme.colors`.
 * Composables must never use hardcoded Color values.
 */
@Immutable
data class AegisColorScheme(
    // Base colors
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val borderActive: Color,
    val divider: Color,

    // Accent colors
    val cyan: Color,
    val cyanDim: Color,
    val amber: Color,
    val amberDim: Color,

    // Status colors
    val statusOk: Color,
    val statusWarning: Color,
    val statusCritical: Color,
    val statusOffline: Color,
    val statusUnknown: Color,

    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textAccent: Color,
    val textWarning: Color,
    val textMuted: Color,
) {
    companion object {
        /**
         * Default AEGIS HUD color scheme — the dark technical console palette.
         */
        fun default() = AegisColorScheme(
            // Base
            background = Color(0xFF050A0E),
            surface = Color(0xFF0A1520),
            surfaceElevated = Color(0xFF0F1E2E),
            border = Color(0xFF1A3A5C),
            borderActive = Color(0xFF00C8FF),
            divider = Color(0xFF0D2035),
            // Accent
            cyan = Color(0xFF00C8FF),
            cyanDim = Color(0xFF0090C0),
            amber = Color(0xFFFFB830),
            amberDim = Color(0xFFC08020),
            // Status
            statusOk = Color(0xFF00FF8C),
            statusWarning = Color(0xFFFF8C00),
            statusCritical = Color(0xFFFF2A2A),
            statusOffline = Color(0xFF4A6070),
            statusUnknown = Color(0xFF2A4050),
            // Text
            textPrimary = Color(0xFFB0D8F0),
            textSecondary = Color(0xFF607080),
            textAccent = Color(0xFF00C8FF),
            textWarning = Color(0xFFFFB830),
            textMuted = Color(0xFF304050),
        )
    }
}

/**
 * CompositionLocal for AEGIS color scheme.
 * Provided by [AegisTheme]; accessed via `AegisTheme.colors`.
 */
val LocalAegisColors = staticCompositionLocalOf { AegisColorScheme.default() }
