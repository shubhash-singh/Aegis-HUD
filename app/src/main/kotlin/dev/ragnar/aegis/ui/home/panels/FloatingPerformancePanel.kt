package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.components.HudCpuIcon
import dev.ragnar.aegis.ui.components.HudRamIcon
import dev.ragnar.aegis.ui.theme.AegisTheme
import kotlin.math.roundToInt

/**
 * Left-side floating panels: CPU and RAM
 *
 * Each card displays:
 * [Icon] LABEL
 * VALUE%
 * [■■■■■□□□] (8-segment bar indicator)
 */
@Composable
fun FloatingPerformancePanel(
    cpuPercent: Int?,
    ramPercent: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // CPU Panel
        PerformanceCard(
            icon = { HudCpuIcon() },
            label = "CPU",
            valueText = cpuPercent?.let { "$it%" } ?: "---",
            fraction = (cpuPercent ?: 0) / 100f,
        )

        // RAM Panel
        PerformanceCard(
            icon = { HudRamIcon() },
            label = "RAM",
            valueText = "$ramPercent%",
            fraction = ramPercent / 100f,
        )
    }
}

@Composable
private fun PerformanceCard(
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

/**
 * 8-segment rectangular progress bar indicator.
 */
@Composable
fun SegmentedProgressBar(
    fraction: Float,
    segments: Int = 8,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF00E5FF),
    inactiveColor: Color = Color(0xFF0F3A5C).copy(alpha = 0.5f),
) {
    val filled = (fraction.coerceIn(0f, 1f) * segments).roundToInt()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (i in 0 until segments) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (i < filled) activeColor else inactiveColor,
                        shape = RoundedCornerShape(1.dp),
                    ),
            )
        }
    }
}
