package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.ragnar.aegis.ui.components.StatusIndicator
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Top bar zone of the HUD dashboard.
 *
 * Layout from UI_UX_SPECIFICATION.md Section 5:
 * ```
 * [SYSTEM LABEL]    [CLOCK]
 * RAGNAR // AEGIS   HH:MM:SS
 * [DATE]            [STATUS]
 * ```
 *
 * @param clockState Current clock state with time and date.
 * @param showSeconds Whether to display seconds in the clock.
 * @param onSettingsClick Optional callback when clicking the system title to open HUD settings.
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
    val spacing = AegisTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Top bar" },
    ) {
        // Row 1: System label + Clock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            // System label with optional customization launcher
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
                    style = typography.telemetry,
                    color = colors.cyan,
                )
                if (onSettingsClick != null) {
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = "⚙",
                        style = typography.telemetryLabel,
                        color = colors.cyanDim,
                    )
                }
            }
            // Live clock
            HudClock(
                hours = clockState.hours,
                minutes = clockState.minutes,
                seconds = clockState.seconds,
                showSeconds = showSeconds,
            )
        }

        Spacer(modifier = Modifier.height(spacing.xs))

        // Row 2: Date + Status indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${clockState.dayOfWeek}  ${clockState.dateFormatted}",
                style = typography.telemetryLabel,
                color = colors.textSecondary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                StatusIndicator(status = StatusLevel.Ok, animate = false)
                Text(
                    text = "ONLINE",
                    style = typography.telemetryLabel,
                    color = colors.statusOk,
                )
            }
        }
    }
}
