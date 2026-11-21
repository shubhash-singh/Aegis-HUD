package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import dev.ragnar.aegis.ui.theme.AegisTheme

enum class AiStatus(val label: String) {
    IDLE("IDLE"),
    PROCESSING("PROCESSING"),
    ACTIVE("ACTIVE"),
}

/**
 * Top-right floating panel: AI STATUS
 *
 * Visual layout matching reference:
 * AI STATUS
 * ● IDLE
 */
@Composable
fun FloatingAiStatusPanel(
    status: AiStatus,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    val (statusColor, statusDotColor) = when (status) {
        AiStatus.IDLE -> Pair(Color(0xFF00FF88), Color(0xFF00FF88))
        AiStatus.PROCESSING -> Pair(colors.cyan, colors.cyan)
        AiStatus.ACTIVE -> Pair(Color(0xFF00FF88), Color(0xFF00FF88))
    }

    FloatingHudPanel(
        modifier = modifier,
        contentPadding = 6.dp,
    ) {
        Text(
            text = "AI STATUS",
            style = typography.sectionHeader.copy(fontSize = 10.sp),
            color = colors.textSecondary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(statusDotColor, CircleShape),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.label,
                style = typography.telemetry.copy(fontSize = 12.sp),
                color = statusColor,
            )
        }
    }
}
