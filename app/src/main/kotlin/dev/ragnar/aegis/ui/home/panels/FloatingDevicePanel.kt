package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.domain.telemetry.ChargingStatus
import dev.ragnar.aegis.ui.components.HudBatteryIcon
import dev.ragnar.aegis.ui.components.HudTempIcon
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Right-side floating panels: TEMP and BATT
 *
 * Each card displays:
 * [Icon] LABEL
 * VALUE
 * [■■■■■□□□] (8-segment bar indicator)
 */
@Composable
fun FloatingDevicePanel(
    batteryTemperatureLabel: String,
    batteryPercentage: Int?,
    chargingStatus: ChargingStatus,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // TEMP Panel
        DeviceStatusCard(
            icon = { HudTempIcon() },
            label = "TEMP",
            valueText = batteryTemperatureLabel,
            fraction = 0.45f, // nominal temperature range
        )

        // BATT Panel
        DeviceStatusCard(
            icon = { HudBatteryIcon() },
            label = "BATT",
            valueText = batteryPercentage?.let { "$it%" } ?: "---",
            fraction = (batteryPercentage ?: 0) / 100f,
        )
    }
}

@Composable
private fun DeviceStatusCard(
    icon: @Composable () -> Unit,
    label: String,
    valueText: String,
    fraction: Float,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val cardWidth = 84.dp

    FloatingHudPanel(
        contentPadding = 6.dp,
    ) {
        Column(modifier = Modifier.width(cardWidth)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = typography.telemetryLabel.copy(fontSize = 11.sp),
                    color = colors.textSecondary,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueText,
                style = typography.telemetry.copy(fontSize = 13.sp),
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(4.dp))
            SegmentedProgressBar(
                fraction = fraction,
                segments = 8,
                modifier = Modifier.width(cardWidth),
            )
        }
    }
}
