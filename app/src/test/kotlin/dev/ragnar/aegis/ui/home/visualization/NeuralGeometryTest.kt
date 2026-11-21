package dev.ragnar.aegis.ui.home.visualization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [NeuralGeometry] cached geometry builder.
 */
class NeuralGeometryTest {

    @Test
    fun geometryBuild_createsValidCenterAndRadii() {
        val width = 1080f
        val height = 2400f
        val geo = NeuralGeometry.build(width, height)

        assertEquals(540f, geo.centerX, 0.01f)
        assertTrue("Brain dimensions must be positive", geo.brainWidth > 0f && geo.brainHeight > 0f)
        assertTrue("Platform radius must be positive", geo.platformRadiusX > 0f && geo.platformRadiusY > 0f)
        assertTrue("Platform Y must be below brain center", geo.platformCenter.y > geo.centerY)
        assertTrue("Upper arc radius must be positive", geo.upperArcRadius > 0f)
        assertTrue("Outer HUD radius must exceed upper arc radius", geo.outerHudRadius > geo.upperArcRadius)
    }

    @Test
    fun geometryBuild_allNodesWithinCanvasBounds() {
        val width = 1080f
        val height = 2400f
        val geo = NeuralGeometry.build(width, height)

        assertTrue("Must contain anatomical brain nodes (>= 60)", geo.nodes.size >= 60)

        for ((idx, node) in geo.nodes.withIndex()) {
            assertTrue("Node $idx X (${node.x}) must be >= 0", node.x >= 0f)
            assertTrue("Node $idx X (${node.x}) must be <= $width", node.x <= width)
            assertTrue("Node $idx Y (${node.y}) must be >= 0", node.y >= 0f)
            assertTrue("Node $idx Y (${node.y}) must be <= $height", node.y <= height)
            assertTrue("Node $idx shell must be between 0 and 3", node.shell in 0..3)
        }
    }

    @Test
    fun geometryBuild_edgesReferenceValidNodeIndices() {
        val width = 800f
        val height = 1200f
        val geo = NeuralGeometry.build(width, height)

        assertTrue("Must contain neural connection edges", geo.edges.isNotEmpty())

        val nodeCount = geo.nodes.size
        for ((idx, edge) in geo.edges.withIndex()) {
            val (from, to) = edge
            assertTrue("Edge $idx 'from' index $from must be < $nodeCount", from in 0 until nodeCount)
            assertTrue("Edge $idx 'to' index $to must be < $nodeCount", to in 0 until nodeCount)
            assertTrue("Edge $idx must not connect node to itself", from != to)
        }
    }

    @Test
    fun geometryBuild_stemEmitterPosition() {
        val geo = NeuralGeometry.build(1080f, 1920f)
        assertTrue("Stem emitter must be below center Y", geo.stemEmitterOffset.y > geo.centerY)
        assertTrue("Stem emitter must be above platform Y", geo.stemEmitterOffset.y < geo.platformCenter.y)
    }

    @Test
    fun organicMeshPose_isContinuousAtLoopBoundary() {
        val start = organicMeshPose(0f)
        val end = organicMeshPose(1f)

        assertEquals("Yaw must not jump when the infinite animation restarts", start.yaw, end.yaw, 0.0001f)
        assertEquals("Pitch must not jump when the infinite animation restarts", start.pitch, end.pitch, 0.0001f)
    }

    @Test
    fun organicMeshPose_keepsTheCameraMotionSubtle() {
        val poses = (0..100).map { organicMeshPose(it / 100f) }
        val yawRange = poses.maxOf { it.yaw } - poses.minOf { it.yaw }
        val pitchRange = poses.maxOf { it.pitch } - poses.minOf { it.pitch }

        assertTrue("Yaw drift should remain under 16 degrees", yawRange < 0.28f)
        assertTrue("Pitch drift should remain under 3 degrees", pitchRange < 0.053f)
    }
}
