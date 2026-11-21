package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.components.StatusIndicator
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Top bar header zone matching the reference design:
 *
 * Line 1: RAGNAR // AEGIS (left)          ● ONLINE (right)
 * Line 2: SATURDAY, 21 NOV 2026 (left)
 * Line 3:               22:20:18 (centered prominent digital clock)
 */
@Composable
fun TopBar(
    clockState: ClockState,
    modifier: Modifier = Modifier,
    showSeconds: Boolean = true,
    onSettingsClick: (() -> Unit)? = null,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val green = Color(0xFF00FF88)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "HUD Top Bar" },
    ) {
        // Line 1: RAGNAR // AEGIS on left, ● ONLINE on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (onSettingsClick != null) {
                    Modifier
                        .clickable(onClick = onSettingsClick)
                        .semantics { contentDescription = "Open HUD Settings" }
                } else Modifier,
            ) {
                Text(
                    text = "RAGNAR // AEGIS",
                    style = typography.telemetry.copy(fontSize = 13.sp, letterSpacing = 1.sp),
                    color = colors.cyan,
                )
                if (onSettingsClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚙",
                        style = typography.telemetryLabel,
                        color = colors.cyanDim,
                    )
                }
            }

            // Right: ● ONLINE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                StatusIndicator(status = StatusLevel.Ok, animate = false)
                Text(
                    text = "ONLINE",
                    style = typography.telemetryLabel.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                    color = green,
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Line 2: Date (e.g. SATURDAY, 21 NOV 2026)
        Text(
            text = "${clockState.dayOfWeek.uppercase()}, ${clockState.dateFormatted.uppercase()}",
            style = typography.telemetryLabel.copy(fontSize = 10.sp, letterSpacing = 0.5.sp),
            color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Line 3: Prominent Centered Clock (e.g. 22:20:18)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            HudClock(
                hours = clockState.hours,
                minutes = clockState.minutes,
                seconds = clockState.seconds,
                showSeconds = showSeconds,
            )
        }
    }
}
