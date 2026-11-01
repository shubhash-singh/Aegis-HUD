package dev.ragnar.aegis.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AEGIS HUD spacing tokens.
 *
 * All values from UI_UX_SPECIFICATION.md Section 4.
 * Provided as a CompositionLocal via [AegisTheme] — access with `AegisTheme.spacing`.
 * Composables must use these tokens for all padding, margins, and sizing.
 */
@Immutable
data class AegisSpacing(
    /** 4dp — tight padding inside components */
    val xs: Dp,
    /** 8dp — standard inner padding */
    val sm: Dp,
    /** 16dp — standard section padding */
    val md: Dp,
    /** 24dp — large section spacing */
    val lg: Dp,
    /** 32dp — major layout divisions */
    val xl: Dp,
    /** 48dp — top-level screen margins */
    val xxl: Dp,

    // Panel-specific tokens
    /** 1dp — all HUD panel borders */
    val panelBorderWidth: Dp,
    /** 2dp — minimal rounding for technical feel */
    val panelCornerRadius: Dp,
    /** 6dp — corner tick mark length */
    val panelTickLength: Dp,
    /** 6dp — status indicator dot size */
    val statusDotSize: Dp,
    /** 48dp — minimum touch target per accessibility */
    val minTouchTarget: Dp,
    /** 48dp — standard launcher icon size */
    val appIconSize: Dp,
) {
    companion object {
        fun default() = AegisSpacing(
            xs = 4.dp,
            sm = 8.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp,
            xxl = 48.dp,
            panelBorderWidth = 1.dp,
            panelCornerRadius = 2.dp,
            panelTickLength = 6.dp,
            statusDotSize = 6.dp,
            minTouchTarget = 48.dp,
            appIconSize = 48.dp,
        )
    }
}

/**
 * CompositionLocal for AEGIS spacing tokens.
 * Provided by [AegisTheme]; accessed via `AegisTheme.spacing`.
 */
val LocalAegisSpacing = staticCompositionLocalOf { AegisSpacing.default() }
