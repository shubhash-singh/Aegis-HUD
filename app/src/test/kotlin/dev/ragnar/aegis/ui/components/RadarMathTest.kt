package dev.ragnar.aegis.ui.components

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class RadarMathTest {

    private val tolerance = 0.001f

    @Test
    fun `polarToCartesian at 0 degrees points along positive X axis`() {
        val offset = RadarMath.polarToCartesian(radius = 100f, angleDegrees = 0f, center = Offset(50f, 50f))

        assertEquals(150f, offset.x, tolerance)
        assertEquals(50f, offset.y, tolerance)
    }

    @Test
    fun `polarToCartesian at 90 degrees points along positive Y axis`() {
        val offset = RadarMath.polarToCartesian(radius = 100f, angleDegrees = 90f, center = Offset(50f, 50f))

        assertEquals(50f, offset.x, tolerance)
        assertEquals(150f, offset.y, tolerance)
    }

    @Test
    fun `polarToCartesian at 180 degrees points along negative X axis`() {
        val offset = RadarMath.polarToCartesian(radius = 100f, angleDegrees = 180f, center = Offset(50f, 50f))

        assertEquals(-50f, offset.x, tolerance)
        assertEquals(50f, offset.y, tolerance)
    }

    @Test
    fun `polarToCartesian at 270 degrees points along negative Y axis`() {
        val offset = RadarMath.polarToCartesian(radius = 100f, angleDegrees = 270f, center = Offset(50f, 50f))

        assertEquals(50f, offset.x, tolerance)
        assertEquals(-50f, offset.y, tolerance)
    }

    @Test
    fun `calculateTickOffsets computes correct inner and outer bounds`() {
        val (outer, inner) = RadarMath.calculateTickOffsets(
            radius = 100f,
            tickLength = 10f,
            angleDegrees = 0f,
            center = Offset.Zero,
        )

        assertEquals(100f, outer.x, tolerance)
        assertEquals(0f, outer.y, tolerance)
        assertEquals(90f, inner.x, tolerance)
        assertEquals(0f, inner.y, tolerance)
    }

    @Test
    fun `normalizeAngle keeps values within zero to 360`() {
        assertEquals(0f, RadarMath.normalizeAngle(0f), tolerance)
        assertEquals(180f, RadarMath.normalizeAngle(180f), tolerance)
        assertEquals(45f, RadarMath.normalizeAngle(405f), tolerance)
        assertEquals(270f, RadarMath.normalizeAngle(-90f), tolerance)
        assertEquals(359f, RadarMath.normalizeAngle(-1f), tolerance)
    }

    @Test
    fun `calculateSweepAngle interpolates progress correctly`() {
        assertEquals(0f, RadarMath.calculateSweepAngle(0f), tolerance)
        assertEquals(180f, RadarMath.calculateSweepAngle(0.5f), tolerance)
        assertEquals(0f, RadarMath.calculateSweepAngle(1f), tolerance)
        assertEquals(90f, RadarMath.calculateSweepAngle(0.25f), tolerance)
        // Clamped bounds
        assertEquals(0f, RadarMath.calculateSweepAngle(-0.5f), tolerance)
        assertEquals(0f, RadarMath.calculateSweepAngle(1.5f), tolerance)
    }
}
