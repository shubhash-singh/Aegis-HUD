package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisColorScheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-screen holographic neural AI core visualization.
 *
 * Renders the centerpiece holographic intelligence core matching the reference design:
 * - Anatomical 3D neural brain lateral mesh with glowing cyan nodes and geodesic pathways
 * - Downward holographic projection cone with vertical light rays
 * - Perspective concentric elliptical emitter platform at base
 * - Tech HUD concentric rings and upper measurement arc
 * - Flowing horizontal wave ribbons extending to edges
 * - Floating ambient particles and edge-traveling data pulses
 */
@Composable
fun AICoreVisualization(
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    speedMultiplier: Float = 1f,
    colors: AegisColorScheme,
) {
    val anim = rememberNeuralAnimationState(isPaused = isPaused, speedMultiplier = speedMultiplier)

    val innerRot by anim.innerRotation
    val outerRot by anim.outerRotation
    val brainPulse by anim.brainPulse
    val beamAlpha by anim.beamAlpha
    val pulsePhase by anim.pulsePhase
    val particlePhase by anim.particlePhase
    val ringPhase by anim.ringPhase
    val wavePhase by anim.wavePhase

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "AEGIS Neural AI Core Visualization" },
    ) {
        val geo = NeuralGeometry.build(size.width, size.height)

        // 1. Surrounding tech HUD rings & outer rotating indicators
        drawSurroundingTechRings(geo, innerRot, outerRot, colors)

        // 2. Horizontal flowing wave ribbons
        drawHorizontalWaveRibbons(geo, wavePhase, colors)

        // 3. Downward holographic projection cone
        drawProjectionCone(geo, beamAlpha, colors)

        // 4. Perspective concentric elliptical energy platform
        drawEnergyPlatform(geo, innerRot, colors)

        // 5. Brain neural network (connection lines + data pulses + nodes)
        drawBrainNeuralMesh(geo, brainPulse, pulsePhase, colors)

        // 6. Upper glowing measurement arc indicator
        drawUpperMeasurementArc(geo, colors)

        // 7. Ambient floating particles
        drawAmbientParticles(geo, particlePhase, colors)
    }
}

/**
 * Surrounding concentric tech HUD circles and counter-rotating segmented arcs.
 */
private fun DrawScope.drawSurroundingTechRings(
    geo: NeuralGeometry,
    innerRot: Float,
    outerRot: Float,
    colors: AegisColorScheme,
) {
    val center = geo.center
    val rOuter = geo.outerHudRadius
    val strokeWidth = 1.dp.toPx()

    // 1. Thin concentric measurement ring
    drawCircle(
        color = colors.cyan.copy(alpha = 0.22f),
        radius = rOuter,
        center = center,
        style = Stroke(width = strokeWidth),
    )

    // Inner subtle ring
    drawCircle(
        color = colors.cyanDim.copy(alpha = 0.15f),
        radius = rOuter * 0.88f,
        center = center,
        style = Stroke(width = strokeWidth),
    )

    // 2. Rotating perimeter arc segments
    rotate(degrees = innerRot, pivot = center) {
        for (i in 0 until 4) {
            val startAngle = i * 90f + 15f
            drawArc(
                color = colors.cyan.copy(alpha = 0.45f),
                startAngle = startAngle,
                sweepAngle = 35f,
                useCenter = false,
                topLeft = Offset(center.x - rOuter, center.y - rOuter),
                size = Size(rOuter * 2, rOuter * 2),
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }
    }

    // 3. Outer counter-rotating dashed ticks
    rotate(degrees = outerRot, pivot = center) {
        for (i in 0 until 12) {
            val angle = i * 30f
            val rad = Math.toRadians(angle.toDouble())
            val p1 = Offset(
                center.x + (rOuter * 0.94f) * cos(rad).toFloat(),
                center.y + (rOuter * 0.94f) * sin(rad).toFloat(),
            )
            val p2 = Offset(
                center.x + (rOuter * 1.02f) * cos(rad).toFloat(),
                center.y + (rOuter * 1.02f) * sin(rad).toFloat(),
            )
            drawLine(colors.cyan.copy(alpha = 0.35f), p1, p2, strokeWidth = 1.2.dp.toPx())
        }
    }
}

/**
 * Upper curved glowing cyan arc with technical measurement tick marks (as seen directly above the brain).
 */
private fun DrawScope.drawUpperMeasurementArc(
    geo: NeuralGeometry,
    colors: AegisColorScheme,
) {
    val center = geo.center
    val arcR = geo.upperArcRadius
    val strokeWidth = 3.dp.toPx()

    // Vibrant thick upper cyan arc segment (around 220° to 320°)
    drawArc(
        color = colors.cyan,
        startAngle = 220f,
        sweepAngle = 100f,
        useCenter = false,
        topLeft = Offset(center.x - arcR, center.y - arcR),
        size = Size(arcR * 2, arcR * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )

    // Outer subtle glow for the arc
    drawArc(
        color = colors.cyan.copy(alpha = 0.35f),
        startAngle = 215f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(center.x - arcR - 2.dp.toPx(), center.y - arcR - 2.dp.toPx()),
        size = Size((arcR + 2.dp.toPx()) * 2, (arcR + 2.dp.toPx()) * 2),
        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
    )

    // Small radial measurement ticks along this upper arc
    for (deg in 225..315 step 15) {
        val rad = Math.toRadians(deg.toDouble())
        val r1 = arcR + 2.dp.toPx()
        val r2 = arcR + 8.dp.toPx()
        val p1 = Offset(center.x + r1 * cos(rad).toFloat(), center.y + r1 * sin(rad).toFloat())
        val p2 = Offset(center.x + r2 * cos(rad).toFloat(), center.y + r2 * sin(rad).toFloat())
        drawLine(colors.cyan.copy(alpha = 0.7f), p1, p2, strokeWidth = 1.2.dp.toPx())
    }
}

/**
 * Flowing horizontal sine-wave particle ribbons extending across both sides of the brain.
 */
private fun DrawScope.drawHorizontalWaveRibbons(
    geo: NeuralGeometry,
    wavePhase: Float,
    colors: AegisColorScheme,
) {
    val phase = wavePhase * 2f * PI.toFloat()
    val steps = 80
    val w = size.width
    val baseCy = geo.centerY + geo.brainHeight * 0.15f

    // Draw left and right wave ribbons with glowing envelope
    fun drawRibbon(yOffset: Float, amplitude: Float, freq: Float, alphaBase: Float) {
        val path = Path()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = t * w
            // Distance from center: maximum amplitude on sides, tapers near center and screen edges
            val distNorm = abs(x - geo.centerX) / (w / 2f)
            val envelope = (sin(distNorm * PI.toFloat()) * distNorm).coerceIn(0f, 1f)
            val y = baseCy + yOffset + amplitude * sin(t * freq * 2f * PI.toFloat() + phase) * envelope

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = colors.cyan.copy(alpha = alphaBase),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
        )
    }

    drawRibbon(yOffset = -8.dp.toPx(), amplitude = 18.dp.toPx(), freq = 3.2f, alphaBase = 0.28f)
    drawRibbon(yOffset = 4.dp.toPx(), amplitude = 24.dp.toPx(), freq = 2.8f, alphaBase = 0.22f)
    drawRibbon(yOffset = 16.dp.toPx(), amplitude = 14.dp.toPx(), freq = 3.6f, alphaBase = 0.18f)
}

/**
 * Downward holographic projection cone and fine ray lines originating from the brain stem.
 */
private fun DrawScope.drawProjectionCone(
    geo: NeuralGeometry,
    beamAlpha: Float,
    colors: AegisColorScheme,
) {
    val origin = geo.stemEmitterOffset
    val platform = geo.platformCenter
    val halfW = geo.platformRadiusX * 0.95f

    // Luminous gradient cone
    val conePath = Path().apply {
        moveTo(origin.x, origin.y)
        lineTo(platform.x + halfW, platform.y)
        lineTo(platform.x - halfW, platform.y)
        close()
    }

    drawPath(
        path = conePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                colors.cyan.copy(alpha = beamAlpha * 0.35f),
                colors.cyan.copy(alpha = beamAlpha * 0.15f),
                colors.cyan.copy(alpha = 0.02f),
            ),
            startY = origin.y,
            endY = platform.y,
        ),
    )

    // Fine projection ray lines fanning out
    val rayCount = 14
    for (i in 0 until rayCount) {
        val t = (i.toFloat() / (rayCount - 1)) * 2f - 1f // -1 to 1
        val targetX = platform.x + halfW * t
        val alpha = (1f - abs(t) * 0.4f) * beamAlpha * 0.4f
        drawLine(
            color = colors.cyan.copy(alpha = alpha),
            start = origin,
            end = Offset(targetX, platform.y),
            strokeWidth = 0.8.dp.toPx(),
        )
    }
}

/**
 * 3D perspective concentric elliptical energy platform at the projection base.
 */
private fun DrawScope.drawEnergyPlatform(
    geo: NeuralGeometry,
    innerRot: Float,
    colors: AegisColorScheme,
) {
    val center = geo.platformCenter
    val rx = geo.platformRadiusX
    val ry = geo.platformRadiusY
    val strokeWidth = 1.2.dp.toPx()

    // Outer ellipse ring
    drawOval(
        color = colors.cyan.copy(alpha = 0.6f),
        topLeft = Offset(center.x - rx, center.y - ry),
        size = Size(rx * 2, ry * 2),
        style = Stroke(width = strokeWidth),
    )

    // Outer subtle glow
    drawOval(
        color = colors.cyan.copy(alpha = 0.25f),
        topLeft = Offset(center.x - rx - 2.dp.toPx(), center.y - ry - 1.dp.toPx()),
        size = Size((rx + 2.dp.toPx()) * 2, (ry + 1.dp.toPx()) * 2),
        style = Stroke(width = 3.dp.toPx()),
    )

    // Mid concentric ellipse
    val midRx = rx * 0.72f
    val midRy = ry * 0.72f
    drawOval(
        color = colors.cyan.copy(alpha = 0.45f),
        topLeft = Offset(center.x - midRx, center.y - midRy),
        size = Size(midRx * 2, midRy * 2),
        style = Stroke(width = strokeWidth),
    )

    // Inner concentric ellipse
    val inRx = rx * 0.45f
    val inRy = ry * 0.45f
    drawOval(
        color = colors.cyan.copy(alpha = 0.75f),
        topLeft = Offset(center.x - inRx, center.y - inRy),
        size = Size(inRx * 2, inRy * 2),
        style = Stroke(width = 1.5.dp.toPx()),
    )

    // Radial measurement tick marks around outer ellipse
    rotate(degrees = innerRot * 0.3f, pivot = center) {
        val tickCount = 18
        for (i in 0 until tickCount) {
            val angle = i * (360f / tickCount)
            val rad = Math.toRadians(angle.toDouble())
            val p1 = Offset(center.x + (rx * 0.95f) * cos(rad).toFloat(), center.y + (ry * 0.95f) * sin(rad).toFloat())
            val p2 = Offset(center.x + (rx * 1.05f) * cos(rad).toFloat(), center.y + (ry * 1.05f) * sin(rad).toFloat())
            drawLine(colors.cyan.copy(alpha = 0.5f), p1, p2, strokeWidth = 1.dp.toPx())
        }
    }

    // Brilliant central energy hotspot (white-cyan core + bloom)
    drawCircle(
        color = colors.cyan.copy(alpha = 0.4f),
        radius = 12.dp.toPx(),
        center = center,
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = 4.dp.toPx(),
        center = center,
    )
}

/**
 * Detailed 3D anatomical brain neural mesh.
 * Renders glowing interconnected nodes, geodesic pathways, and animated data pulses.
 */
private fun DrawScope.drawBrainNeuralMesh(
    geo: NeuralGeometry,
    brainPulse: Float,
    pulsePhase: Float,
    colors: AegisColorScheme,
) {
    val center = geo.center

    // 1. Connection lines between nodes
    val lineStroke = 0.9.dp.toPx()
    geo.edges.forEach { (fromIdx, toIdx) ->
        val from = geo.nodes[fromIdx]
        val to = geo.nodes[toIdx]

        // Scale node positions around brain center by pulse
        val p1 = Offset(center.x + (from.x - center.x) * brainPulse, center.y + (from.y - center.y) * brainPulse)
        val p2 = Offset(center.x + (to.x - center.x) * brainPulse, center.y + (to.y - center.y) * brainPulse)

        val alpha = when {
            from.shell == 0 || to.shell == 0 -> 0.65f
            from.shell == 3 || to.shell == 3 -> 0.55f
            else -> 0.45f
        }

        drawLine(
            color = colors.cyan.copy(alpha = alpha),
            start = p1,
            end = p2,
            strokeWidth = lineStroke,
        )
    }

    // 2. Data pulses racing along edges
    geo.edges.forEachIndexed { idx, (fromIdx, toIdx) ->
        val from = geo.nodes[fromIdx]
        val to = geo.nodes[toIdx]
        val stagger = (idx * 0.19f) % 1f
        val t = (pulsePhase + stagger) % 1f

        val p1 = Offset(center.x + (from.x - center.x) * brainPulse, center.y + (from.y - center.y) * brainPulse)
        val p2 = Offset(center.x + (to.x - center.x) * brainPulse, center.y + (to.y - center.y) * brainPulse)

        val px = p1.x + (p2.x - p1.x) * t
        val py = p1.y + (p2.y - p1.y) * t
        val alpha = (sin(t * PI.toFloat()) * 0.95f).coerceIn(0f, 1f)

        if (alpha > 0.1f) {
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 1.8.dp.toPx(),
                center = Offset(px, py),
            )
            drawCircle(
                color = colors.cyan.copy(alpha = alpha * 0.6f),
                radius = 3.5.dp.toPx(),
                center = Offset(px, py),
            )
        }
    }

    // 3. Neural nodes (with outer glow, core circle, and bright center)
    geo.nodes.forEach { node ->
        val p = Offset(center.x + (node.x - center.x) * brainPulse, center.y + (node.y - center.y) * brainPulse)
        val radius = when (node.shell) {
            0 -> 3.2.dp.toPx()
            3 -> 2.6.dp.toPx()
            else -> 2.2.dp.toPx()
        }
        val alpha = when (node.shell) {
            0 -> 0.95f
            3 -> 0.85f
            else -> 0.75f
        }

        // Outer glow
        drawCircle(
            color = colors.cyan.copy(alpha = alpha * 0.4f),
            radius = radius * 2.4f,
            center = p,
        )
        // Core node
        drawCircle(
            color = colors.cyan.copy(alpha = alpha),
            radius = radius,
            center = p,
        )
        // Bright white specular center
        drawCircle(
            color = Color.White.copy(alpha = alpha * 0.8f),
            radius = radius * 0.5f,
            center = p,
        )
    }
}

/**
 * Ambient glowing particles floating upward.
 */
private fun DrawScope.drawAmbientParticles(
    geo: NeuralGeometry,
    particlePhase: Float,
    colors: AegisColorScheme,
) {
    val count = 20
    val w = size.width
    val h = size.height

    for (i in 0 until count) {
        val seed = i.toFloat() / count
        val t = (particlePhase + seed) % 1f
        val px = w * ((seed * 1.7f) % 1f)
        val py = h * (1f - t)
        val alpha = (sin(t * PI.toFloat()) * 0.65f).coerceIn(0f, 0.65f)

        if (alpha > 0.05f) {
            drawCircle(
                color = colors.cyan.copy(alpha = alpha),
                radius = 1.4.dp.toPx(),
                center = Offset(px, py),
            )
        }
    }
}
