package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD Clock display composable.
 *
 * Renders time as prominent bold HH:MM with attached :SS seconds.
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
        // Hours and minutes in large bold cyan
        Text(
            text = "$hours:$minutes",
            style = typography.clock.copy(fontSize = 44.sp, letterSpacing = 2.sp),
            color = colors.cyan,
        )
        if (showSeconds) {
            // Attached seconds
            Text(
                text = ":$seconds",
                style = typography.clockSmall.copy(fontSize = 22.sp, letterSpacing = 1.sp),
                color = colors.cyanDim,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }
}
