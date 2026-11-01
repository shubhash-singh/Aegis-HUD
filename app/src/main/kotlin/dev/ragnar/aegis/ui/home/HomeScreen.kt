package dev.ragnar.aegis.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.ragnar.aegis.ui.components.HudPanel
import dev.ragnar.aegis.ui.components.StatusIndicator
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD Home Screen — Phase 2 design system demonstration.
 *
 * Displays the AEGIS system label, a HUD panel with status indicators,
 * and a telemetry panel — all using [AegisTheme] design tokens.
 *
 * BackHandler prevents the launcher from finishing on back press (ARCHITECTURE.md Section 6).
 * System insets are handled per UI_UX_SPECIFICATION.md Section 8.
 *
 * Phase 3 will add the full dashboard layout with clock, app grid, etc.
 */
@Composable
fun HomeScreen() {
    // Launcher must not finish on back press — no-op handler.
    BackHandler {
        // Intentionally empty: a home-screen launcher does not exit on back press.
    }

    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = spacing.md)
            .semantics { contentDescription = "AEGIS Home Screen" },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(spacing.lg))

        // System label
        Text(
            text = "AEGIS",
            style = typography.clock,
            color = colors.cyan,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "SYSTEM ONLINE",
            style = typography.telemetryLabel,
            color = colors.cyanDim,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // System status panel
        HudPanel(title = "System Status") {
            Column(modifier = Modifier.padding(top = spacing.xs)) {
                StatusRow(label = "NETWORK", status = StatusLevel.Ok)
                Spacer(modifier = Modifier.height(spacing.xs))
                StatusRow(label = "BATTERY", status = StatusLevel.Ok)
                Spacer(modifier = Modifier.height(spacing.xs))
                StatusRow(label = "STORAGE", status = StatusLevel.Warning)
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Telemetry panel
        HudPanel(title = "Telemetry") {
            Column(modifier = Modifier.padding(top = spacing.xs)) {
                TelemetryRow(label = "CPU TEMP", value = "---")
                Spacer(modifier = Modifier.height(spacing.xs))
                TelemetryRow(label = "MEM USAGE", value = "---")
                Spacer(modifier = Modifier.height(spacing.xs))
                TelemetryRow(label = "UPTIME", value = "---")
            }
        }
    }
}

/**
 * A row displaying a status label with its indicator dot.
 */
@Composable
private fun StatusRow(
    label: String,
    status: StatusLevel,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusIndicator(status = status)
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(
            text = label,
            style = typography.telemetryLabel,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = status.label.uppercase(),
            style = typography.telemetry,
            color = when (status) {
                StatusLevel.Ok -> colors.statusOk
                StatusLevel.Warning -> colors.statusWarning
                StatusLevel.Critical -> colors.statusCritical
                StatusLevel.Offline -> colors.statusOffline
                StatusLevel.Unknown -> colors.statusUnknown
            },
        )
    }
}

/**
 * A row displaying a telemetry label with its value.
 * Values show "---" as placeholder (no fake data per DEC-009).
 */
@Composable
private fun TelemetryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.telemetryLabel,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = typography.telemetry,
            color = colors.textPrimary,
        )
    }
}
