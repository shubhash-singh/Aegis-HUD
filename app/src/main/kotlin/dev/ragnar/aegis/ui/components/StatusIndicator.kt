package dev.ragnar.aegis.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Status indicator dot for AEGIS HUD.
 *
 * A 6dp filled circle colored by system status.
 * When [status] is [StatusLevel.Ok] and [animate] is true, a subtle pulse
 * animation scales 1.0 → 1.3 → 1.0 over 2 seconds (UI_UX_SPECIFICATION.md Section 7).
 *
 * @param status The current status level.
 * @param animate Whether to apply the pulse animation for OK status.
 * @param modifier Modifier for the dot container.
 * @param label Accessibility content description.
 */
@Composable
fun StatusIndicator(
    status: StatusLevel,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    label: String = status.label,
) {
    val colors = AegisTheme.colors
    val dotSize = AegisTheme.spacing.statusDotSize

    val color: Color = when (status) {
        StatusLevel.Ok -> colors.statusOk
        StatusLevel.Warning -> colors.statusWarning
        StatusLevel.Critical -> colors.statusCritical
        StatusLevel.Offline -> colors.statusOffline
        StatusLevel.Unknown -> colors.statusUnknown
    }

    val scale: Float = if (status == StatusLevel.Ok && animate) {
        val transition = rememberInfiniteTransition(label = "statusPulse")
        val animatedScale by transition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "statusPulseScale",
        )
        animatedScale
    } else {
        1.0f
    }

    Box(
        modifier = modifier
            .size(dotSize)
            .scale(scale)
            .background(color = color, shape = CircleShape)
            .semantics { contentDescription = label },
    )
}

/**
 * Status levels for AEGIS system indicators.
 *
 * Each level maps to a specific color in [AegisColorScheme].
 */
enum class StatusLevel(val label: String) {
    Ok("Status OK"),
    Warning("Status Warning"),
    Critical("Status Critical"),
    Offline("Status Offline"),
    Unknown("Status Unknown"),
}
