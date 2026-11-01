package dev.ragnar.aegis.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier

/**
 * AEGIS HUD theme.
 *
 * Provides [AegisColorScheme], [AegisTypographyScheme], and [AegisSpacing]
 * through CompositionLocals as described in ARCHITECTURE.md Section 7.
 *
 * Usage in composables:
 * ```
 * AegisTheme.colors.cyan
 * AegisTheme.typography.clock
 * AegisTheme.spacing.md
 * ```
 */
@Composable
fun AegisTheme(
    colors: AegisColorScheme = AegisColorScheme.default(),
    typography: AegisTypographyScheme = AegisTypographyScheme.default(),
    spacing: AegisSpacing = AegisSpacing.default(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAegisColors provides colors,
        LocalAegisTypography provides typography,
        LocalAegisSpacing provides spacing,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            content()
        }
    }
}

/**
 * Accessor object for AEGIS design tokens.
 *
 * Composables should use `AegisTheme.colors`, `AegisTheme.typography`, and
 * `AegisTheme.spacing` to access theme values — never hardcoded literals.
 */
object AegisTheme {
    /** Current AEGIS color scheme from the nearest [AegisTheme] provider. */
    val colors: AegisColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAegisColors.current

    /** Current AEGIS typography scheme from the nearest [AegisTheme] provider. */
    val typography: AegisTypographyScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAegisTypography.current

    /** Current AEGIS spacing tokens from the nearest [AegisTheme] provider. */
    val spacing: AegisSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAegisSpacing.current
}
