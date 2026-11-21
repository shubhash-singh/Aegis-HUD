package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD Clock display composable.
 *
 * Renders time as HH:MM:SS with seconds in dimmed cyan.
 * Design from UI_UX_SPECIFICATION.md Section 6 "Clock Display":
 * - Font: JetBrains Mono, `aegis.type.clock`
 * - Color: `aegis.cyan` (hours/minutes), `aegis.cyan.dim` (seconds)
 *
 * @param hours Formatted hours string (e.g. "14")
 * @param minutes Formatted minutes string (e.g. "07")
 * @param seconds Formatted seconds string (e.g. "32")
 */
@Composable
fun HudClock(
    hours: String,
    minutes: String,
    seconds: String,
    modifier: Modifier = Modifier,
    showSeconds: Boolean = true,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    Row(
        modifier = modifier.semantics {
            contentDescription = if (showSeconds) "Clock: $hours:$minutes:$seconds" else "Clock: $hours:$minutes"
        },
        verticalAlignment = Alignment.Bottom,
    ) {
        // Hours and minutes in bright cyan
        Text(
            text = "$hours:$minutes",
            style = typography.clock,
            color = colors.cyan,
        )
        if (showSeconds) {
            // Seconds in dimmed cyan, smaller size
            Text(
                text = ":$seconds",
                style = typography.clockSmall,
                color = colors.cyanDim,
            )
        }
    }
}
