package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisColorScheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ---- Tuning knobs ----
private const val BRAIN_SCALE = 1.10f   // 1f = original brain width; cards cover the tips as it grows
private const val BRAIN_Y_OFFSET = -0.04f // in brain half-widths; negative moves the brain up
private const val FOCAL = 4.5f          // lower = stronger perspective
private const val BASE_YAW = 0.0f       // radians; + turns the front toward the viewer
private const val SWAY = 0.22f          // radians of left/right drift (0f = static brain)
private const val PITCH = 0.10f         // look-down angle
private const val BUCKETS = 4           // edges are batched into this many brightness levels

/** Per-frame buffers, allocated once and reused so drawing never allocates. */
class NeuralProjection(n: Int) {
    val sx = FloatArray(n)
    val sy = FloatArray(n)
    val persp = FloatArray(n)
    val w = FloatArray(n)               // brightness 0..1
    val z = FloatArray(n)               // rotated depth, larger = nearer
    val order = IntArray(n) { it }      // back-to-front; nearly sorted each frame
    val paths = Array(BUCKETS) { Path() }
    val rimPath = Path()
}

internal fun DrawScope.drawBrainMesh3D(
    mesh: NeuralMesh3D,
    f: NeuralProjection,
    geo: NeuralGeometry,
    brainPulse: Float,
    pulsePhase: Float,
    rotationDeg: Float,
    colors: AegisColorScheme,
) {
    val twoPi = 2f * PI.toFloat()
    val s = geo.brainWidth * 0.5f * BRAIN_SCALE * brainPulse
    val yaw = BASE_YAW + SWAY * sin(rotationDeg * (PI.toFloat() / 180f))

    // 1. rotate + perspective project + per-node brightness
    projectNeuralMesh(
        mesh, yaw, PITCH, FOCAL, s,
        geo.centerX, geo.centerY + BRAIN_Y_OFFSET * s,
        f.sx, f.sy, f.persp, f.w, f.z,
    )

    // 2. back-to-front order (insertion sort; cheap because the order barely changes per frame)
    val o = f.order
    for (i in 1 until o.size) {
        val v = o[i]
        val d = f.z[v]
        var j = i - 1
        while (j >= 0 && f.z[o[j]] > d) { o[j + 1] = o[j]; j-- }
        o[j + 1] = v
    }

    // 3. edges, batched by brightness; the outline goes into its own path
    for (p in f.paths) p.rewind()
    f.rimPath.rewind()
    for (e in 0 until mesh.edgeCount) {
        val a = mesh.edgeA[e]
        val b = mesh.edgeB[e]
        val path = if (mesh.edgeRim[e]) {
            f.rimPath
        } else {
            val w = (f.w[a] + f.w[b]) * 0.5f
            f.paths[(w * BUCKETS).toInt().coerceIn(0, BUCKETS - 1)]
        }
        path.moveTo(f.sx[a], f.sy[a])
        path.lineTo(f.sx[b], f.sy[b])
    }

    val dp1 = 1.dp.toPx()
    for (bi in 0 until BUCKETS) {
        val wb = (bi + 0.5f) / BUCKETS
        val alpha = 0.08f + 0.70f * wb
        val stroke = dp1 * (0.55f + 0.9f * wb)
        if (bi >= BUCKETS - 2) { // soft glow on the brightest lines
            drawPath(
                f.paths[bi], colors.cyan.copy(alpha = alpha * 0.28f),
                style = Stroke(width = stroke * 3.2f, cap = StrokeCap.Round),
            )
        }
        drawPath(
            f.paths[bi], colors.cyan.copy(alpha = alpha),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
    drawPath(
        f.rimPath, colors.cyan.copy(alpha = 0.22f),
        style = Stroke(width = dp1 * 3.6f, cap = StrokeCap.Round),
    )
    drawPath(
        f.rimPath, colors.cyan.copy(alpha = 0.85f),
        style = Stroke(width = dp1 * 1.3f, cap = StrokeCap.Round),
    )

    // 4. data pulses racing along every 6th edge
    var e = 0
    while (e < mesh.edgeCount) {
        val a = mesh.edgeA[e]
        val b = mesh.edgeB[e]
        val t = (pulsePhase + (e * 0.19f) % 1f) % 1f
        val w = f.w[a] + (f.w[b] - f.w[a]) * t
        val alpha = (sin(t * PI.toFloat()) * (0.25f + 0.75f * w)).coerceIn(0f, 1f)
        if (alpha > 0.1f) {
            val c = Offset(f.sx[a] + (f.sx[b] - f.sx[a]) * t, f.sy[a] + (f.sy[b] - f.sy[a]) * t)
            val r = 1.8.dp.toPx() * (0.6f + 0.8f * w)
            drawCircle(Color.White.copy(alpha = alpha), r, c)
            drawCircle(colors.cyan.copy(alpha = alpha * 0.6f), r * 2f, c)
        }
        e += 6
    }

    // 5. nodes back to front: small dots everywhere, twinkling "stars" on every 5th
    for (k in o.indices) {
        val i = o[k]
        val w = f.w[i]
        val sc = f.persp[i]
        val c = Offset(f.sx[i], f.sy[i])
        if (i % 5 == 0) {
            val tw = 0.85f + 0.15f * sin(twoPi * (pulsePhase + i * 0.137f))
            val a = (w * tw).coerceIn(0f, 1f)
            val r = 3.0.dp.toPx() * sc
            drawCircle(colors.cyan.copy(alpha = a * 0.30f), r * 4.2f, c)
            drawCircle(colors.cyan.copy(alpha = a * 0.65f), r * 1.8f, c)
            drawCircle(Color.White.copy(alpha = a * 0.95f), r * 0.75f, c)
        } else {
            drawCircle(
                colors.cyan.copy(alpha = 0.25f + 0.6f * w),
                1.3.dp.toPx() * sc * (0.7f + 0.6f * w), c,
            )
        }
    }
}
