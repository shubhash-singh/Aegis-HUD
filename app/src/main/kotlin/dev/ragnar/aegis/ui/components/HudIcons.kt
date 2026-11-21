package dev.ragnar.aegis.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * 4 L-shaped tactical HUD corner brackets framing the home screen display.
 */
@Composable
fun HudCornerBrackets(
    modifier: Modifier = Modifier,
    bracketLength: Dp = 18.dp,
    bracketThickness: Dp = 2.dp,
    color: Color = AegisTheme.colors.borderActive.copy(alpha = 0.85f),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val len = bracketLength.toPx()
        val th = bracketThickness.toPx()
        val w = size.width
        val h = size.height

        // Top-Left bracket
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), th, cap = StrokeCap.Square)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), th, cap = StrokeCap.Square)

        // Top-Right bracket
        drawLine(color, Offset(w, 0f), Offset(w - len, 0f), th, cap = StrokeCap.Square)
        drawLine(color, Offset(w, 0f), Offset(w, len), th, cap = StrokeCap.Square)

        // Bottom-Left bracket
        drawLine(color, Offset(0f, h), Offset(len, h), th, cap = StrokeCap.Square)
        drawLine(color, Offset(0f, h), Offset(0f, h - len), th, cap = StrokeCap.Square)

        // Bottom-Right bracket
        drawLine(color, Offset(w, h), Offset(w - len, h), th, cap = StrokeCap.Square)
        drawLine(color, Offset(w, h), Offset(w, h - len), th, cap = StrokeCap.Square)
    }
}

/**
 * Isometric 3D cube icon (used in AEGIS AI CORE).
 */
@Composable
fun HudCubeIcon(
    modifier: Modifier = Modifier.size(16.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.2.dp.toPx()

        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.42f

        // Isometric cube vertices
        val pTop = Offset(cx, cy - r)
        val pTopRight = Offset(cx + r * 0.866f, cy - r * 0.5f)
        val pBottomRight = Offset(cx + r * 0.866f, cy + r * 0.5f)
        val pBottom = Offset(cx, cy + r)
        val pBottomLeft = Offset(cx - r * 0.866f, cy + r * 0.5f)
        val pTopLeft = Offset(cx - r * 0.866f, cy - r * 0.5f)
        val pCenter = Offset(cx, cy)

        val outerPath = Path().apply {
            moveTo(pTop.x, pTop.y)
            lineTo(pTopRight.x, pTopRight.y)
            lineTo(pBottomRight.x, pBottomRight.y)
            lineTo(pBottom.x, pBottom.y)
            lineTo(pBottomLeft.x, pBottomLeft.y)
            lineTo(pTopLeft.x, pTopLeft.y)
            close()
        }
        drawPath(outerPath, color, style = Stroke(stroke, join = StrokeJoin.Round))

        // Inner Y lines
        drawLine(color, pCenter, pTop, stroke)
        drawLine(color, pCenter, pBottomLeft, stroke)
        drawLine(color, pCenter, pBottomRight, stroke)
    }
}

/**
 * CPU Processor chip icon.
 */
@Composable
fun HudCpuIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        val pad = w * 0.22f
        drawRect(
            color = color,
            topLeft = Offset(pad, pad),
            size = Size(w - pad * 2, h - pad * 2),
            style = Stroke(stroke),
        )
        // Inner core
        drawRect(
            color = color.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.38f, h * 0.38f),
            size = Size(w * 0.24f, h * 0.24f),
        )

        // Pins (2 on each side)
        val pinLen = pad * 0.8f
        for (i in 0..1) {
            val offset = w * (0.35f + i * 0.3f)
            // Top
            drawLine(color, Offset(offset, 0f), Offset(offset, pinLen), stroke)
            // Bottom
            drawLine(color, Offset(offset, h), Offset(offset, h - pinLen), stroke)
            // Left
            drawLine(color, Offset(0f, offset), Offset(pinLen, offset), stroke)
            // Right
            drawLine(color, Offset(w, offset), Offset(w - pinLen, offset), stroke)
        }
    }
}

/**
 * Layered RAM stack icon.
 */
@Composable
fun HudRamIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        for (i in 0..2) {
            val cy = h * (0.28f + i * 0.22f)
            val path = Path().apply {
                moveTo(w * 0.5f, cy - h * 0.12f)
                lineTo(w * 0.88f, cy)
                lineTo(w * 0.5f, cy + h * 0.12f)
                lineTo(w * 0.12f, cy)
                close()
            }
            drawPath(path, color, style = Stroke(stroke, join = StrokeJoin.Round))
        }
    }
}

/**
 * Thermometer temperature icon.
 */
@Composable
fun HudTempIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        val cx = w / 2f

        // Bulb at bottom
        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(cx, h * 0.75f),
            style = Stroke(stroke),
        )
        drawCircle(
            color = color,
            radius = w * 0.12f,
            center = Offset(cx, h * 0.75f),
        )

        // Tube going up
        val tubeW = w * 0.14f
        drawLine(color, Offset(cx - tubeW, h * 0.58f), Offset(cx - tubeW, h * 0.18f), stroke)
        drawLine(color, Offset(cx + tubeW, h * 0.58f), Offset(cx + tubeW, h * 0.18f), stroke)
        // Tube cap
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - tubeW, h * 0.1f),
            size = Size(tubeW * 2, tubeW * 2),
            style = Stroke(stroke),
        )
        // Mercury column
        drawLine(color, Offset(cx, h * 0.7f), Offset(cx, h * 0.35f), stroke * 1.5f)
    }
}

/**
 * Battery level icon.
 */
@Composable
fun HudBatteryIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        // Battery body
        val bw = w * 0.6f
        val bh = h * 0.78f
        val bx = (w - bw) / 2f
        val by = h * 0.18f

        drawRect(
            color = color,
            topLeft = Offset(bx, by),
            size = Size(bw, bh),
            style = Stroke(stroke),
        )

        // Battery positive terminal on top
        val tw = bw * 0.4f
        val th = h * 0.1f
        drawRect(
            color = color,
            topLeft = Offset((w - tw) / 2f, by - th),
            size = Size(tw, th),
        )

        // Battery fill indicator inside
        drawRect(
            color = color.copy(alpha = 0.7f),
            topLeft = Offset(bx + stroke * 2, by + bh * 0.45f),
            size = Size(bw - stroke * 4, bh * 0.5f),
        )
    }
}

/**
 * Neural branching network icon (for INTELLIGENCE).
 */
@Composable
fun HudIntelligenceIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        val nodeR = 1.8.dp.toPx()

        val pTop = Offset(w * 0.5f, h * 0.2f)
        val pLeft = Offset(w * 0.22f, h * 0.78f)
        val pRight = Offset(w * 0.78f, h * 0.78f)
        val pCenter = Offset(w * 0.5f, h * 0.55f)

        // Connection lines
        drawLine(color, pTop, pCenter, stroke)
        drawLine(color, pCenter, pLeft, stroke)
        drawLine(color, pCenter, pRight, stroke)

        // Nodes
        drawCircle(color, nodeR, pTop)
        drawCircle(color, nodeR, pLeft)
        drawCircle(color, nodeR, pRight)
        drawCircle(color, nodeR * 1.2f, pCenter)
    }
}

/**
 * ECG pulse wave icon (for TELEMETRY).
 */
@Composable
fun HudTelemetryIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.2.dp.toPx()
        val cy = h / 2f

        val path = Path().apply {
            moveTo(0f, cy)
            lineTo(w * 0.25f, cy)
            lineTo(w * 0.38f, cy - h * 0.38f)
            lineTo(w * 0.52f, cy + h * 0.38f)
            lineTo(w * 0.65f, cy - h * 0.18f)
            lineTo(w * 0.75f, cy)
            lineTo(w, cy)
        }
        drawPath(path, color, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Lightning bolt voltage icon.
 */
@Composable
fun HudVoltageIcon(
    modifier: Modifier = Modifier.size(12.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.58f, 0f)
            lineTo(w * 0.18f, h * 0.55f)
            lineTo(w * 0.48f, h * 0.55f)
            lineTo(w * 0.42f, h)
            lineTo(w * 0.82f, h * 0.42f)
            lineTo(w * 0.52f, h * 0.42f)
            close()
        }
        drawPath(path, color)
    }
}

/**
 * Clock circle icon.
 */
@Composable
fun HudClockIcon(
    modifier: Modifier = Modifier.size(12.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        val cx = w / 2f
        val cy = h / 2f

        drawCircle(color, w * 0.45f, Offset(cx, cy), style = Stroke(stroke))
        // Hour hand
        drawLine(color, Offset(cx, cy), Offset(cx, cy - h * 0.28f), stroke)
        // Minute hand
        drawLine(color, Offset(cx, cy), Offset(cx + w * 0.22f, cy), stroke)
    }
}

/**
 * Layer stack icon (for SYSTEM STATUS).
 */
@Composable
fun HudStackIcon(
    modifier: Modifier = Modifier.size(14.dp),
    color: Color = AegisTheme.colors.cyan,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        for (i in 0..1) {
            val cy = h * (0.35f + i * 0.32f)
            val path = Path().apply {
                moveTo(w * 0.5f, cy - h * 0.15f)
                lineTo(w * 0.9f, cy)
                lineTo(w * 0.5f, cy + h * 0.15f)
                lineTo(w * 0.1f, cy)
                close()
            }
            drawPath(path, color, style = Stroke(stroke, join = StrokeJoin.Round))
        }
    }
}
