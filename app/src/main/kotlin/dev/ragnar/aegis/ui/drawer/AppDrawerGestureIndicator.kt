package dev.ragnar.aegis.ui.drawer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Bottom gesture indicator for the app drawer matching the reference design:
 * - Double stacked upward cyan chevrons
 * - SWIPE UP in bold cyan
 * - TO OPEN APP DRAWER in muted secondary font
 * - Horizontal gradient lines flanking the text
 * - Continuous breathing alpha animation
 */
@Composable
fun AppDrawerGestureIndicator(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    val infiniteTransition = rememberInfiniteTransition(label = "DrawerGestureBreathe")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "BreatheAlpha",
    )

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .semantics { contentDescription = "Swipe up to open app drawer" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Double stacked chevron icon
        Canvas(modifier = Modifier.size(24.dp, 16.dp)) {
            val strokeWidth = 2.dp.toPx()
            val w = size.width
            val h = size.height

            // Top chevron
            val path1 = Path().apply {
                moveTo(w * 0.15f, h * 0.45f)
                lineTo(w * 0.5f, h * 0.1f)
                lineTo(w * 0.85f, h * 0.45f)
            }
            drawPath(
                path = path1,
                color = colors.cyan.copy(alpha = alpha),
                style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )

            // Bottom chevron
            val path2 = Path().apply {
                moveTo(w * 0.15f, h * 0.85f)
                lineTo(w * 0.5f, h * 0.5f)
                lineTo(w * 0.85f, h * 0.85f)
            }
            drawPath(
                path = path2,
                color = colors.cyan.copy(alpha = alpha * 0.75f),
                style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // "SWIPE UP" with horizontal flanking lines
        Row(
            modifier = Modifier.fillMaxWidth(0.6f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Left gradient line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, colors.cyan.copy(alpha = alpha * 0.6f)),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "SWIPE UP",
                style = typography.sectionHeader.copy(fontSize = 11.sp, letterSpacing = 1.5.sp),
                color = colors.cyan.copy(alpha = alpha),
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Right gradient line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(colors.cyan.copy(alpha = alpha * 0.6f), Color.Transparent),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "TO OPEN APP DRAWER",
            style = typography.telemetryLabel.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = colors.textSecondary.copy(alpha = alpha * 0.8f),
        )
    }
}
