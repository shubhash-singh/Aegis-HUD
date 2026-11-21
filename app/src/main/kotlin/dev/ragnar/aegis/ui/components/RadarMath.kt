package dev.ragnar.aegis.ui.components

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure mathematical functions for radar geometry and coordinates.
 *
 * Designed to be entirely decoupled from Android framework APIs so that all
 * calculations can be thoroughly verified with JVM unit tests.
 */
object RadarMath {

    /**
     * Converts a polar coordinate (radius, angleDegrees) into a Cartesian [Offset],
     * centered at [center]. Angle 0° points to the right (+X), 90° points down (+Y).
     */
    fun polarToCartesian(
        radius: Float,
        angleDegrees: Float,
        center: Offset = Offset.Zero,
    ): Offset {
        val angleRadians = (angleDegrees * PI / 180.0).toFloat()
        val x = center.x + radius * cos(angleRadians)
        val y = center.y + radius * sin(angleRadians)
        return Offset(x, y)
    }

    /**
     * Calculates the start and end [Offset] for a radial tick mark on a circle of [radius],
     * with length [tickLength] pointing inwards toward the center.
     */
    fun calculateTickOffsets(
        radius: Float,
        tickLength: Float,
        angleDegrees: Float,
        center: Offset = Offset.Zero,
    ): Pair<Offset, Offset> {
        val outer = polarToCartesian(radius, angleDegrees, center)
        val inner = polarToCartesian((radius - tickLength).coerceAtLeast(0f), angleDegrees, center)
        return Pair(outer, inner)
    }

    /**
     * Normalizes an angle in degrees to the standard range `[0°, 360°)`.
     */
    fun normalizeAngle(degrees: Float): Float {
        var normalized = degrees % 360f
        if (normalized < 0f) {
            normalized += 360f
        }
        return normalized
    }

    /**
     * Maps an animation progress value in `[0.0, 1.0]` to degrees in `[0°, 360°]`.
     */
    fun calculateSweepAngle(progress: Float): Float {
        val clampedProgress = progress.coerceIn(0f, 1f)
        return (clampedProgress * 360f) % 360f
    }
}
