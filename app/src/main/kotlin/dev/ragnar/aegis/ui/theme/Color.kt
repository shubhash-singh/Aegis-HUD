package dev.ragnar.aegis.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AEGIS HUD color palette.
 *
 * Token names follow the UI/UX Specification (docs/UI_UX_SPECIFICATION.md).
 * All colors are defined here; composables must reference these tokens, not hardcoded values.
 */
object AegisColors {

    // Base colors
    val Background = Color(0xFF050A0E)
    val Surface = Color(0xFF0A1520)
    val SurfaceElevated = Color(0xFF0F1E2E)
    val Border = Color(0xFF1A3A5C)
    val BorderActive = Color(0xFF00C8FF)
    val Divider = Color(0xFF0D2035)

    // Accent colors
    val Cyan = Color(0xFF00C8FF)
    val CyanDim = Color(0xFF0090C0)
    val Amber = Color(0xFFFFB830)
    val AmberDim = Color(0xFFC08020)

    // Status colors
    val StatusOk = Color(0xFF00FF8C)
    val StatusWarning = Color(0xFFFF8C00)
    val StatusCritical = Color(0xFFFF2A2A)
    val StatusOffline = Color(0xFF4A6070)
    val StatusUnknown = Color(0xFF2A4050)

    // Text colors
    val TextPrimary = Color(0xFFB0D8F0)
    val TextSecondary = Color(0xFF607080)
    val TextAccent = Color(0xFF00C8FF)
    val TextWarning = Color(0xFFFFB830)
    val TextMuted = Color(0xFF304050)
}
