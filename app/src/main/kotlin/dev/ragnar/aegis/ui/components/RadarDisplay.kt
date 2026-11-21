package dev.ragnar.aegis.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme
import kotlin.math.min

/**
 * Animated technical radar HUD display.
 *
 * Implements:
 * - Concentric range rings in subtle border color
 * - Measurement ticks around perimeter (major every 90°, minor every 30°)
 * - Center crosshair reticle
 * - Rotating sweep sector (trailing 45° arc with gradient fade)
 * - Rotating sweep line with bright leading edge
 * - Azimuth readout
 * - Pauses rotation when [isPaused] is true (e.g. power-saver mode)
 * - Configurable rotation period via [rotationDurationMillis]
 *
 * Design from UI_UX_SPECIFICATION.md Section 6 "Radar Graphic".
 *
 * @param modifier Modifier for the container.
 * @param isPaused When true, the sweep animation stops to conserve battery.
 * @param rotationDurationMillis Milliseconds per 360° revolution (default 4000ms).
 */
@Composable
fun RadarDisplay(
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    rotationDurationMillis: Int = 4000,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    val sweepAngle = if (isPaused) {
        0f
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = rotationDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "SweepAngle",
        )
        angle
    }

    val shape = RoundedCornerShape(spacing.panelCornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(spacing.panelBorderWidth, colors.border, shape)
            .padding(spacing.sm)
            .semantics { contentDescription = "Technical Radar Display" },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RADAR // SCAN",
                    style = typography.sectionHeader,
                    color = colors.cyanDim,
                )
                Text(
                    text = if (isPaused) "STANDBY" else "AZ ${sweepAngle.toInt().toString().padStart(3, '0')}°",
                    style = typography.telemetryLabel,
                    color = if (isPaused) colors.statusWarning else colors.cyan,
                )
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            // Radar Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f),
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = min(size.width, size.height) / 2f - 4.dp.toPx()

                if (outerRadius <= 0f) return@Canvas

                // 1. Concentric range rings
                drawRangeRings(
                    center = center,
                    outerRadius = outerRadius,
                    borderColor = colors.border,
                )

                // 2. Crosshair reticle lines
                drawReticle(
                    center = center,
                    radius = outerRadius,
                    color = colors.border,
                    cyan = colors.cyan,
                )

                // 3. Perimeter measurement ticks
                drawMeasurementTicks(
                    center = center,
                    outerRadius = outerRadius,
                    borderColor = colors.borderActive,
                    tickLength = 6.dp.toPx(),
                )

                // 4. Rotating sweep sector and sweep line
                if (!isPaused) {
                    drawSweepSector(
                        center = center,
                        radius = outerRadius,
                        sweepAngleDegrees = sweepAngle,
                        cyan = colors.cyan,
                    )
                }
            }
        }
    }
}

/** Draws concentric range rings at 25%, 50%, 75%, and 100% of outer radius. */
private fun DrawScope.drawRangeRings(
    center: Offset,
    outerRadius: Float,
    borderColor: Color,
) {
    val ringRatios = floatArrayOf(0.25f, 0.5f, 0.75f, 1.0f)
    for (ratio in ringRatios) {
        val r = outerRadius * ratio
        drawCircle(
            color = borderColor.copy(alpha = if (ratio == 1.0f) 0.6f else 0.3f),
            radius = r,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )
    }
}

/** Draws subtle technical crosshairs extending through the center. */
private fun DrawScope.drawReticle(
    center: Offset,
    radius: Float,
    color: Color,
    cyan: Color,
) {
    val strokeWidth = 1.dp.toPx()
    val dimColor = color.copy(alpha = 0.35f)

    // Full horizontal and vertical diameter lines
    drawLine(
        color = dimColor,
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = strokeWidth,
    )
    drawLine(
        color = dimColor,
        start = Offset(center.x, center.y - radius),
        end = Offset(center.x, center.y + radius),
        strokeWidth = strokeWidth,
    )

    // Center micro reticle
    val reticleRadius = 4.dp.toPx()
    drawCircle(
        color = cyan,
        radius = reticleRadius,
        center = center,
        style = Stroke(width = strokeWidth),
    )
    drawCircle(
        color = cyan,
        radius = 1.5.dp.toPx(),
        center = center,
    )
}

/** Draws perimeter tick marks every 30 degrees, with major ticks every 90 degrees. */
private fun DrawScope.drawMeasurementTicks(
    center: Offset,
    outerRadius: Float,
    borderColor: Color,
    tickLength: Float,
) {
    for (deg in 0 until 360 step 30) {
        val isMajor = deg % 90 == 0
        val length = if (isMajor) tickLength else tickLength * 0.6f
        val (outer, inner) = RadarMath.calculateTickOffsets(
            radius = outerRadius,
            tickLength = length,
            angleDegrees = deg.toFloat(),
            center = center,
        )
        drawLine(
            color = if (isMajor) borderColor else borderColor.copy(alpha = 0.5f),
            start = outer,
            end = inner,
            strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx(),
        )
    }
}

/** Draws the rotating sweep sector trailing behind the current angle. */
private fun DrawScope.drawSweepSector(
    center: Offset,
    radius: Float,
    sweepAngleDegrees: Float,
    cyan: Color,
) {
    val sectorTrailAngle = 45f

    // Draw the trailing sweep sector
    drawArc(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color.Transparent,
                cyan.copy(alpha = 0.05f),
                cyan.copy(alpha = 0.22f),
            ),
            center = center,
        ),
        startAngle = sweepAngleDegrees - sectorTrailAngle,
        sweepAngle = sectorTrailAngle,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
    )

    // Draw the bright leading sweep line
    val sweepEnd = RadarMath.polarToCartesian(radius, sweepAngleDegrees, center)
    drawLine(
        color = cyan.copy(alpha = 0.85f),
        start = center,
        end = sweepEnd,
        strokeWidth = 1.5.dp.toPx(),
    )
}
