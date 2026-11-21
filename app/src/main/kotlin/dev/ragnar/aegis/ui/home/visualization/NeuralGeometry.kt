package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import kotlin.math.min

/**
 * Anatomical 3D holographic neural brain geometry.
 *
 * Models a detailed human brain in lateral profile with:
 * - Frontal lobe & prefrontal pole (anterior curvature)
 * - Motor, premotor, sensory cortex & parietal lobules (superior convexity)
 * - Occipital lobe & visual cortex pole (posterior bulge)
 * - Temporal lobe & lateral Sylvian fissure boundary (inferior anterior)
 * - Cerebellum with horizontal folia striations (lower posterior mass)
 * - Brainstem, pons, and downward projection column into emitter platform
 * - Radiating corpus callosum arch and thalamic core
 * - High-density 140+ node geodesic mesh matching the reference visual design
 */
@Immutable
data class NeuralGeometry(
    val centerX: Float,
    val centerY: Float,
    val brainWidth: Float,
    val brainHeight: Float,
    val nodes: List<NodePoint>,
    val edges: List<Pair<Int, Int>>,
    val stemEmitterOffset: Offset,
    val platformCenter: Offset,
    val platformRadiusX: Float,
    val platformRadiusY: Float,
    val upperArcRadius: Float,
    val outerHudRadius: Float,
) {
    val center: Offset get() = Offset(centerX, centerY)

    companion object {
        private val STATIC_NORMALIZED_NODES: List<Triple<Float, Float, Int>> by lazy { getAnatomicalBrainNodes() }
        private val STATIC_EDGES: List<Pair<Int, Int>> by lazy { buildProximityEdges(STATIC_NORMALIZED_NODES, maxDistance = 0.26f) }

        @Volatile
        private var cachedWidth: Float = -1f
        @Volatile
        private var cachedHeight: Float = -1f
        @Volatile
        private var cachedGeometry: NeuralGeometry? = null

        fun build(width: Float, height: Float): NeuralGeometry {
            val cached = cachedGeometry
            if (cached != null && cachedWidth == width && cachedHeight == height) {
                return cached
            }

            val cx = width / 2f
            // Center brain slightly above the vertical middle so the platform and bottom status panel have room
            val cy = height * 0.44f
            val minDim = min(width, height)

            // Brain size: occupies prominent central space
            val brainWidth = minDim * 0.58f
            val brainHeight = minDim * 0.44f

            // Platform in 3D perspective at bottom of projection
            val platformY = cy + brainHeight * 0.88f
            val platformRadiusX = minDim * 0.28f
            val platformRadiusY = platformRadiusX * 0.28f

            // Upper tech arc and outer HUD rings
            val upperArcRadius = brainWidth * 0.68f
            val outerHudRadius = brainWidth * 0.82f

            // Normalized anatomical brain coordinates (-1.0 to 1.0)
            val normalizedNodes = STATIC_NORMALIZED_NODES
            val nodes = normalizedNodes.map { (nx, ny, shell) ->
                val px = cx + nx * (brainWidth / 2f)
                val py = cy + ny * (brainHeight / 2f)
                NodePoint(px, py, shell)
            }

            // Stem emitter is near bottom of stem
            val stemEmitterOffset = Offset(cx + 0.12f * (brainWidth / 2f), cy + 0.95f * (brainHeight / 2f))

            val newGeometry = NeuralGeometry(
                centerX = cx,
                centerY = cy,
                brainWidth = brainWidth,
                brainHeight = brainHeight,
                nodes = nodes,
                edges = STATIC_EDGES,
                stemEmitterOffset = stemEmitterOffset,
                platformCenter = Offset(cx, platformY),
                platformRadiusX = platformRadiusX,
                platformRadiusY = platformRadiusY,
                upperArcRadius = upperArcRadius,
                outerHudRadius = outerHudRadius,
            )

            cachedWidth = width
            cachedHeight = height
            cachedGeometry = newGeometry
            return newGeometry
        }

        /**
         * Comprehensive anatomical coordinates (-1.0 to 1.0) for a human brain lateral profile.
         * nx: -1.0 (anterior/front) to 1.0 (posterior/back)
         * ny: -1.0 (superior/top) to 1.0 (inferior/stem)
         * shell: 0 = deep core/thalamus, 1 = mid subcortical gyri, 2 = cortical surface, 3 = cerebellum/stem
         */
        private fun getAnatomicalBrainNodes(): List<Triple<Float, Float, Int>> {
            return listOf(
                // === 1. FRONTAL LOBE & PREFRONTAL CONVEXITY (Anterior) ===
                Triple(-0.84f, -0.05f, 2),
                Triple(-0.90f, -0.18f, 2),
                Triple(-0.93f, -0.32f, 2),
                Triple(-0.88f, -0.46f, 2),
                Triple(-0.82f, -0.58f, 2),
                Triple(-0.72f, -0.70f, 2),
                Triple(-0.60f, -0.80f, 2),
                Triple(-0.46f, -0.88f, 2),
                Triple(-0.32f, -0.92f, 2),
                Triple(-0.78f, -0.22f, 1),
                Triple(-0.80f, -0.38f, 1),
                Triple(-0.72f, -0.50f, 1),
                Triple(-0.62f, -0.62f, 1),
                Triple(-0.50f, -0.72f, 1),
                Triple(-0.38f, -0.78f, 1),
                Triple(-0.76f, 0.05f, 2),
                Triple(-0.65f, 0.08f, 2),
                Triple(-0.55f, -0.02f, 1),
                Triple(-0.65f, -0.15f, 1),
                Triple(-0.58f, -0.30f, 1),
                Triple(-0.48f, -0.42f, 1),
                Triple(-0.54f, -0.18f, 0),
                Triple(-0.45f, -0.28f, 0),
                Triple(-0.38f, -0.40f, 0),
                Triple(-0.32f, -0.55f, 0),
                Triple(-0.44f, -0.58f, 0),
                Triple(-0.30f, -0.25f, 0),

                // === 2. MOTOR, SENSORY & PARIETAL LOBE (Superior Margin) ===
                Triple(-0.18f, -0.96f, 2),
                Triple(-0.02f, -0.98f, 2),
                Triple(0.14f, -0.97f, 2),
                Triple(0.30f, -0.93f, 2),
                Triple(0.44f, -0.86f, 2),
                Triple(0.56f, -0.78f, 2),
                Triple(-0.22f, -0.82f, 1),
                Triple(-0.20f, -0.65f, 1),
                Triple(-0.18f, -0.48f, 1),
                Triple(-0.15f, -0.32f, 1),
                Triple(-0.06f, -0.84f, 1),
                Triple(-0.05f, -0.68f, 1),
                Triple(-0.04f, -0.50f, 1),
                Triple(-0.02f, -0.35f, 1),
                Triple(0.10f, -0.82f, 1),
                Triple(0.24f, -0.80f, 1),
                Triple(0.36f, -0.72f, 1),
                Triple(0.48f, -0.65f, 1),
                Triple(-0.10f, -0.55f, 0),
                Triple(0.06f, -0.62f, 0),
                Triple(0.20f, -0.60f, 0),
                Triple(0.32f, -0.55f, 0),
                Triple(0.10f, -0.45f, 0),
                Triple(0.22f, -0.42f, 0),

                // === 3. OCCIPITAL LOBE & VISUAL CORTEX (Posterior Pole) ===
                Triple(0.68f, -0.68f, 2),
                Triple(0.80f, -0.54f, 2),
                Triple(0.88f, -0.38f, 2),
                Triple(0.93f, -0.20f, 2),
                Triple(0.94f, -0.02f, 2),
                Triple(0.90f, 0.14f, 2),
                Triple(0.82f, 0.28f, 2),
                Triple(0.60f, -0.55f, 1),
                Triple(0.70f, -0.42f, 1),
                Triple(0.78f, -0.24f, 1),
                Triple(0.80f, -0.05f, 1),
                Triple(0.76f, 0.12f, 1),
                Triple(0.68f, 0.24f, 1),
                Triple(0.50f, -0.52f, 1),
                Triple(0.58f, -0.35f, 1),
                Triple(0.65f, -0.18f, 1),
                Triple(0.68f, 0.02f, 1),
                Triple(0.58f, 0.15f, 1),
                Triple(0.48f, -0.38f, 0),
                Triple(0.52f, -0.18f, 0),
                Triple(0.54f, 0.00f, 0),

                // === 4. TEMPORAL LOBE & LATERAL SULCUS BOUNDARY (Inferior Anterior) ===
                Triple(-0.40f, 0.00f, 1),
                Triple(-0.25f, 0.04f, 1),
                Triple(-0.10f, 0.08f, 1),
                Triple(0.05f, 0.10f, 1),
                Triple(0.20f, 0.08f, 1),
                Triple(0.35f, 0.04f, 1),
                Triple(-0.55f, 0.12f, 2),
                Triple(-0.68f, 0.22f, 2),
                Triple(-0.76f, 0.35f, 2),
                Triple(-0.70f, 0.48f, 2),
                Triple(-0.56f, 0.58f, 2),
                Triple(-0.40f, 0.60f, 2),
                Triple(-0.24f, 0.54f, 2),
                Triple(-0.12f, 0.45f, 2),
                Triple(-0.58f, 0.26f, 1),
                Triple(-0.48f, 0.36f, 1),
                Triple(-0.35f, 0.42f, 1),
                Triple(-0.20f, 0.38f, 1),
                Triple(-0.06f, 0.32f, 1),
                Triple(0.08f, 0.28f, 1),
                Triple(-0.35f, 0.18f, 0),
                Triple(-0.20f, 0.22f, 0),
                Triple(-0.05f, 0.20f, 0),
                Triple(0.12f, 0.18f, 0),

                // === 5. CEREBELLUM (Lower Posterior Mass with Horizontal Folia) ===
                Triple(0.35f, 0.32f, 3),
                Triple(0.48f, 0.32f, 3),
                Triple(0.62f, 0.35f, 3),
                Triple(0.74f, 0.40f, 3),
                Triple(0.82f, 0.50f, 3),
                Triple(0.85f, 0.62f, 3),
                Triple(0.78f, 0.74f, 3),
                Triple(0.66f, 0.82f, 3),
                Triple(0.50f, 0.84f, 3),
                Triple(0.34f, 0.78f, 3),
                Triple(0.42f, 0.45f, 3),
                Triple(0.56f, 0.46f, 3),
                Triple(0.70f, 0.50f, 3),
                Triple(0.72f, 0.62f, 3),
                Triple(0.60f, 0.65f, 3),
                Triple(0.46f, 0.65f, 3),
                Triple(0.52f, 0.74f, 3),
                Triple(0.38f, 0.60f, 3),
                Triple(0.50f, 0.56f, 3),
                Triple(0.62f, 0.58f, 3),

                // === 6. BRAIN STEM, PONS & PROJECTION COLUMN (Inferior Base) ===
                Triple(0.00f, 0.28f, 3),
                Triple(0.12f, 0.30f, 3),
                Triple(0.22f, 0.32f, 3),
                Triple(0.02f, 0.44f, 3),
                Triple(0.12f, 0.46f, 3),
                Triple(0.20f, 0.48f, 3),
                Triple(0.05f, 0.60f, 3),
                Triple(0.14f, 0.62f, 3),
                Triple(0.08f, 0.76f, 3),
                Triple(0.16f, 0.78f, 3),
                Triple(0.10f, 0.88f, 3),
                Triple(0.15f, 0.92f, 3),
                Triple(0.12f, 0.96f, 3),
                Triple(0.14f, 1.00f, 3),

                // === 7. CENTRAL CORE, CORPUS CALLOSUM & THALAMUS ===
                Triple(-0.30f, -0.42f, 0),
                Triple(-0.16f, -0.46f, 0),
                Triple(-0.02f, -0.48f, 0),
                Triple(0.14f, -0.46f, 0),
                Triple(0.28f, -0.40f, 0),
                Triple(-0.12f, -0.32f, 0),
                Triple(0.04f, -0.30f, 0),
                Triple(0.18f, -0.28f, 0),
                Triple(-0.08f, -0.18f, 0),
                Triple(0.08f, -0.16f, 0),
                Triple(0.22f, -0.15f, 0),
                Triple(-0.16f, -0.06f, 0),
                Triple(0.00f, -0.04f, 0),
                Triple(0.14f, -0.02f, 0),
                Triple(0.26f, -0.04f, 0),
                Triple(-0.05f, 0.08f, 0),
                Triple(0.08f, 0.06f, 0),
                Triple(0.20f, 0.04f, 0),
            )
        }

        /**
         * Connects nodes that are within [maxDistance] of each other,
         * with special constraints to preserve cortical fissures and stem direction.
         */
        private fun buildEdges(
            nodes: List<Triple<Float, Float, Int>>,
            maxDistance: Float,
        ): List<Pair<Int, Int>> {
            val edges = mutableListOf<Pair<Int, Int>>()
            val maxDistSq = maxDistance * maxDistance

            for (i in nodes.indices) {
                val (x1, y1, shell1) = nodes[i]
                for (j in i + 1 until nodes.size) {
                    val (x2, y2, shell2) = nodes[j]

                    // Don't bridge across the Sylvian fissure between temporal bottom and frontal top
                    if (y1 > 0.15f && y2 < -0.2f && x1 < -0.3f && x2 < -0.3f) continue
                    if (y2 > 0.15f && y1 < -0.2f && x1 < -0.3f && x2 < -0.3f) continue

                    // Don't bridge between cerebellum and frontal lobe
                    if (x1 > 0.3f && x2 < -0.2f && (shell1 == 3 || shell2 == 3)) continue
                    if (x2 > 0.3f && x1 < -0.2f && (shell1 == 3 || shell2 == 3)) continue

                    val dx = x1 - x2
                    val dy = y1 - y2
                    val distSq = dx * dx + dy * dy

                    if (distSq <= maxDistSq) {
                        edges.add(Pair(i, j))
                    }
                }
            }
            return edges
        }

        private fun buildProximityEdges(
            nodes: List<Triple<Float, Float, Int>>,
            maxDistance: Float,
        ): List<Pair<Int, Int>> = buildEdges(nodes, maxDistance)
    }
}

/**
 * A single neural node position and its structural layer index.
 */
@Immutable
data class NodePoint(
    val x: Float,
    val y: Float,
    val shell: Int,
) {
    val offset: Offset get() = Offset(x, y)
}
