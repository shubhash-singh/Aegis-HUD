package dev.ragnar.aegis.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Tactical cyber-HUD pulsing alert banner for context-aware events.
 */
@Composable
fun TacticalAlertBanner(
    message: String,
    modifier: Modifier = Modifier,
    alertColor: Color = AegisTheme.colors.statusWarning,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AlertPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AlertAlpha",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AegisTheme.colors.background.copy(alpha = 0.85f),
                shape = RoundedCornerShape(4.dp),
            )
            .border(
                width = 1.dp,
                color = alertColor.copy(alpha = alpha),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "▲",
                style = AegisTheme.typography.telemetryLabel,
                color = alertColor.copy(alpha = alpha),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = message,
                style = AegisTheme.typography.telemetryLabel,
                color = alertColor,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "▲",
                style = AegisTheme.typography.telemetryLabel,
                color = alertColor.copy(alpha = alpha),
            )
        }
    }
}
