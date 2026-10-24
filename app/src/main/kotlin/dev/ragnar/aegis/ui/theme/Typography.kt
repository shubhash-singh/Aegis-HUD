package dev.ragnar.aegis.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * AEGIS HUD typography tokens.
 *
 * Phase 1 uses system monospace and default sans-serif as placeholders.
 * Phase 2 will bundle JetBrains Mono and Inter font assets.
 *
 * Token names follow the UI/UX Specification (docs/UI_UX_SPECIFICATION.md).
 */
object AegisTypography {

    // Phase 1 placeholder: system monospace. Phase 2 will replace with JetBrains Mono.
    private val MonoFamily = FontFamily.Monospace
    // Phase 1 placeholder: system default sans-serif. Phase 2 will replace with Inter.
    private val SansFamily = FontFamily.Default

    val clock = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
    )

    val clockSmall = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
    )

    val telemetry = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    )

    val telemetryLabel = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
    )

    val appName = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    )

    val body = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    )

    val label = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )

    val sectionHeader = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
    )
}
