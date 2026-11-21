package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.components.HudStackIcon
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Bottom floating panel: SYSTEM STATUS
 *
 * Visual layout matching reference:
 * [StackIcon] SYSTEM STATUS
 * ───────────────────────────────────────
 * ● NETWORK           │ ● STORAGE
 * WIFI                │ 42.3/128 GB
 * 0kb/s↓ 0kb/s↑       │
 */
@Composable
fun FloatingSystemStatusPanel(
    networkLabel: String,
    networkStatusLevel: StatusLevel,
    storageStatusLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val green = Color(0xFF00FF88)

    FloatingHudPanel(
        modifier = modifier.fillMaxWidth(),
        title = "SYSTEM STATUS",
        icon = { HudStackIcon() },
        contentPadding = 8.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Column 1: Network
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(green, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NETWORK",
                        style = typography.telemetryLabel.copy(fontSize = 10.sp),
                        color = colors.textSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                // Network type (e.g. WIFI)
                val typeOnly = networkLabel.substringBefore(" -").ifEmpty { "WIFI" }
                val speedOnly = if (networkLabel.contains("- ")) networkLabel.substringAfter("- ") else "0kb/s↓  0kb/s↑"

                Text(
                    text = typeOnly,
                    style = typography.telemetry.copy(fontSize = 12.sp),
                    color = green,
                )
                Text(
                    text = speedOnly,
                    style = typography.telemetryLabel.copy(fontSize = 9.sp),
                    color = colors.textSecondary,
                )
            }

            // Vertical divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(colors.border.copy(alpha = 0.5f)),
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column 2: Storage
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(green, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STORAGE",
                        style = typography.telemetryLabel.copy(fontSize = 10.sp),
                        color = colors.textSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = storageStatusLabel,
                    style = typography.telemetry.copy(fontSize = 12.sp),
                    color = if (storageStatusLabel == "---") colors.textMuted else green,
                )
            }
        }
    }
}
