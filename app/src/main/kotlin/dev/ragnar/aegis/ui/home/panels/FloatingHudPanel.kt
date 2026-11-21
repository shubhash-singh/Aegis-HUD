package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Holographic floating HUD panel container with depth, glassmorphism, and edge glowing effects.
 * Matches the reference design aesthetic:
 * - Multi-stage outer cyan glowing aura
 * - Crisp dual-tone glowing neon border with corner accents
 * - Translucent cyber-glass dark gradient with top rim highlight
 */
@Composable
fun FloatingHudPanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    borderColor: Color? = null,
    cornerRadius: Dp = 8.dp,
    contentPadding: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val cyan = colors.cyan
    val shape = RoundedCornerShape(cornerRadius)

    val activeBorderColor = borderColor ?: cyan

    Box(
        modifier = modifier
            // 1. Outer edge glowing aura bloom
            .drawBehind {
                val cr = cornerRadius.toPx()
                // Soft outer glow bloom
                drawRoundRect(
                    color = activeBorderColor.copy(alpha = 0.12f),
                    topLeft = Offset(-4.dp.toPx(), -4.dp.toPx()),
                    size = Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                    cornerRadius = CornerRadius(cr + 4.dp.toPx(), cr + 4.dp.toPx()),
                )
                // Mid-distance radiant aura
                drawRoundRect(
                    color = activeBorderColor.copy(alpha = 0.22f),
                    topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = CornerRadius(cr + 2.dp.toPx(), cr + 2.dp.toPx()),
                )
            }
            // 2. Translucent cyber-glass gradient background with depth
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE008243A), // Translucent cyan-navy top sheen
                        Color(0xF0021320), // Deep tactical dark navy
                        Color(0xF8010810), // Heavy dark base for text contrast
                    ),
                ),
                shape = shape,
            )
            // 3. Crisp glowing neon border with vertical gradient
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        activeBorderColor.copy(alpha = 0.95f),
                        activeBorderColor.copy(alpha = 0.70f),
                        activeBorderColor.copy(alpha = 0.40f),
                    ),
                ),
                shape = shape,
            )
            // 4. Subtle inner top-edge rim specular highlight
            .drawBehind {
                val cr = cornerRadius.toPx()
                // Top rim specular highlight line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            activeBorderColor.copy(alpha = 0.60f),
                            activeBorderColor.copy(alpha = 0.85f),
                            activeBorderColor.copy(alpha = 0.60f),
                            Color.Transparent,
                        ),
                    ),
                    start = Offset(cr, 1.dp.toPx()),
                    end = Offset(size.width - cr, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(contentPadding),
    ) {
        Column {
            if (title != null || icon != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp),
                ) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (title != null) {
                        Text(
                            text = title.uppercase(),
                            style = typography.sectionHeader,
                            color = colors.cyan,
                        )
                    }
                }
            }
            content()
        }
    }
}
