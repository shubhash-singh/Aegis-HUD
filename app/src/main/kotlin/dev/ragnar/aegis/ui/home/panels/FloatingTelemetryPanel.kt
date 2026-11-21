package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.components.HudBatteryIcon
import dev.ragnar.aegis.ui.components.HudClockIcon
import dev.ragnar.aegis.ui.components.HudTelemetryIcon
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Lower-right floating panel: TELEMETRY
 *
 * Visual layout matching reference:
 * [WaveIcon] TELEMETRY
 * [BattIcon]  BATT TEMP   37.0°C
 * [ClockIcon] UPTIME      2H 49M
 */
@Composable
fun FloatingTelemetryPanel(
    batteryTemperatureLabel: String,
    uptimeFormatted: String,
    modifier: Modifier = Modifier,
) {
    val panelWidth = 125.dp

    FloatingHudPanel(
        modifier = modifier,
        title = "TELEMETRY",
        icon = { HudTelemetryIcon() },
        contentPadding = 8.dp,
    ) {
        Column(
            modifier = Modifier.width(panelWidth),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TelemetryItem(
                icon = { HudBatteryIcon(modifier = Modifier.width(12.dp)) },
                label = "BATT TEMP",
                value = batteryTemperatureLabel,
            )
            TelemetryItem(
                icon = { HudClockIcon(modifier = Modifier.width(12.dp)) },
                label = "UPTIME",
                value = uptimeFormatted,
            )
        }
    }
}

@Composable
private fun TelemetryItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    Row(
        modifier = Modifier.width(125.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = typography.telemetryLabel.copy(fontSize = 10.sp),
                color = colors.textSecondary,
            )
        }
        Text(
            text = value,
            style = typography.telemetry.copy(fontSize = 11.sp),
            color = Color.White,
        )
    }
}
