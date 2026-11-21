package dev.ragnar.aegis.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Animated CRT/HUD scan-line overlay.
 *
 * Translates a subtle horizontal line vertically from top to bottom over [durationMillis].
 * Respects [isPaused] to avoid background battery drain.
 *
 * Design from UI_UX_SPECIFICATION.md Section 7 "Animation Guidelines":
 * Duration: 3s top to bottom, Linear, repeat.
 */
@Composable
fun ScanLineOverlay(
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    durationMillis: Int = 3000,
) {
    val cyan = AegisTheme.colors.cyan

    if (isPaused) return

    val infiniteTransition = rememberInfiniteTransition(label = "ScanLineTransition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ScanLineProgress",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val y = size.height * progress
        val lineHeight = 3f

        // Draw soft vertical bloom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    cyan.copy(alpha = 0.08f),
                    cyan.copy(alpha = 0.18f),
                    cyan.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
                startY = (y - 8f).coerceAtLeast(0f),
                endY = (y + 8f).coerceAtMost(size.height),
            ),
            topLeft = Offset(0f, (y - 8f).coerceAtLeast(0f)),
            size = Size(size.width, 16f),
        )

        // Draw bright central scan line
        drawLine(
            color = cyan.copy(alpha = 0.35f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = lineHeight,
        )
    }
}
