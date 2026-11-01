package dev.ragnar.aegis.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.R

/**
 * AEGIS HUD typography tokens.
 *
 * Bundled fonts: JetBrains Mono (technical mono), Inter (UI labels/body).
 * Token names and sizes follow UI_UX_SPECIFICATION.md Section 3.
 *
 * Provided as a CompositionLocal via [AegisTheme] — access with `AegisTheme.typography`.
 * Composables must never use ad-hoc TextStyle definitions.
 */

// --- Font Families (bundled assets — no network loading) ---

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold),
)

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

@Immutable
data class AegisTypographyScheme(
    /** Main clock display — JetBrains Mono 64sp Bold */
    val clock: TextStyle,
    /** Secondary time elements — JetBrains Mono 32sp Regular */
    val clockSmall: TextStyle,
    /** Telemetry values — JetBrains Mono 14sp Regular */
    val telemetry: TextStyle,
    /** Telemetry labels — JetBrains Mono 11sp Regular */
    val telemetryLabel: TextStyle,
    /** Application names — Inter 15sp Medium */
    val appName: TextStyle,
    /** General body text — Inter 14sp Regular */
    val body: TextStyle,
    /** Small labels and captions — Inter 12sp Medium */
    val label: TextStyle,
    /** Section headers (uppercase) — Inter 11sp SemiBold */
    val sectionHeader: TextStyle,
) {
    companion object {
        fun default() = AegisTypographyScheme(
            clock = TextStyle(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp,
            ),
            clockSmall = TextStyle(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
            ),
            telemetry = TextStyle(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
            telemetryLabel = TextStyle(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
            ),
            appName = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            ),
            body = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
            label = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
            sectionHeader = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            ),
        )
    }
}

/**
 * CompositionLocal for AEGIS typography scheme.
 * Provided by [AegisTheme]; accessed via `AegisTheme.typography`.
 */
val LocalAegisTypography = staticCompositionLocalOf { AegisTypographyScheme.default() }
