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
import dev.ragnar.aegis.ui.components.HudIntelligenceIcon
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Lower-left floating panel: INTELLIGENCE
 *
 * Visual layout matching reference:
 * [NetworkIcon] INTELLIGENCE
 * • CONTEXT       ✓
 * • VOICE         ✓
 * • VISION        ✓
 * • AUTOMATION    ✓
 */
@Composable
fun FloatingIntelligencePanel(
    contextAvailable: Boolean,
    voiceAvailable: Boolean,
    visionAvailable: Boolean,
    automationAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    val panelWidth = 120.dp

    FloatingHudPanel(
        modifier = modifier,
        title = "INTELLIGENCE",
        icon = { HudIntelligenceIcon() },
        contentPadding = 8.dp,
    ) {
        Column(
            modifier = Modifier.width(panelWidth),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IntelligenceRow("• CONTEXT", contextAvailable)
            IntelligenceRow("• VOICE", voiceAvailable)
            IntelligenceRow("• VISION", visionAvailable)
            IntelligenceRow("• AUTOMATION", automationAvailable)
        }
    }
}

@Composable
private fun IntelligenceRow(label: String, available: Boolean) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val green = Color(0xFF00FF88)

    Row(
        modifier = Modifier.width(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.telemetryLabel.copy(fontSize = 11.sp),
            color = colors.textSecondary,
        )
        Text(
            text = if (available) "✓" else "—",
            style = typography.telemetry.copy(fontSize = 12.sp),
            color = if (available) green else colors.textMuted,
        )
    }
}
