package dev.ragnar.aegis.ui.home.visualization

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisColorScheme

/**
 * Source fragments intentionally taken from AEGIS production code. This makes the background
 * readable as real application telemetry rather than a stream of arbitrary Matrix-style glyphs.
 */
private val AEGIS_CODE_FRAGMENTS = arrayOf(
    "fun AICoreVisualization(",
    "val mesh = remember {",
    "NeuralMesh3D.build()",
    "drawBrainMesh3D(",
    "isPaused = isPaused",
    "colors = colors",
    "data class BatteryState(",
    "data class NetworkState(",
    "AnimationSpeed.Normal",
    "fun organicMeshPose(",
)

/** Reusable Android paint; no paint or typeface allocation occurs in the Canvas draw loop. */
internal class AegisCodeRainRenderer {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
    }
}

/** Draws a restrained, code-based rain layer behind the neural hologram. */
internal fun DrawScope.drawAegisCodeRain(
    renderer: AegisCodeRainRenderer,
    phase: Float,
    colors: AegisColorScheme,
) {
    val lineHeight = 11.dp.toPx()
    val textSize = 10.dp.toPx()
    val columnCount = (size.width / 92.dp.toPx()).toInt().coerceIn(4, 7)
    val streamLength = 4
    val streamTravel = size.height + lineHeight * (streamLength + 2)

    renderer.paint.textSize = textSize

    for (column in 0 until columnCount) {
        // Offsetting each stream keeps the layer continuously populated while remaining loop-safe.
        val progress = (phase + column * 0.173f) % 1f
        val headY = -lineHeight * streamLength + progress * streamTravel
        val x = size.width * (column + 0.08f) / columnCount

        for (line in 0 until streamLength) {
            val y = headY - line * lineHeight
            if (y < -lineHeight || y > size.height + lineHeight) continue

            val trailAlpha = 1f - line / streamLength.toFloat()
            // Present enough to read as source code, but still subordinate to HUD data and the mesh.
            renderer.paint.color = colors.cyan.copy(alpha = 0.040f + trailAlpha * 0.100f).toArgb()
            renderer.paint.textSize = textSize * (if (line == 0) 1.08f else 1f)
            drawContext.canvas.nativeCanvas.drawText(
                AEGIS_CODE_FRAGMENTS[(column * 3 + line) % AEGIS_CODE_FRAGMENTS.size],
                x,
                y,
                renderer.paint,
            )
        }
    }
}
