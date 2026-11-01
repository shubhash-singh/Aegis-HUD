package dev.ragnar.aegis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD Panel composable — the fundamental container for AEGIS UI sections.
 *
 * Renders a panel with:
 * - Background in `aegis.surface`
 * - 1dp border in `aegis.border` (or custom [borderColor])
 * - 2dp corner radius
 * - Corner tick decorations (6dp lines at each corner)
 * - Optional section header in uppercase
 *
 * Design from UI_UX_SPECIFICATION.md Section 6 "HUD Panel".
 *
 * @param modifier Modifier for the outer container.
 * @param title Optional section header text (rendered uppercase).
 * @param borderColor Override border color (default: `aegis.border`).
 * @param content Panel content.
 */
@Composable
fun HudPanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    borderColor: Color = AegisTheme.colors.border,
    content: @Composable () -> Unit,
) {
    val colors = AegisTheme.colors
    val spacing = AegisTheme.spacing
    val typography = AegisTheme.typography
    val cornerRadius = spacing.panelCornerRadius
    val tickLength = spacing.panelTickLength
    val borderWidth = spacing.panelBorderWidth

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.surface,
                shape = RoundedCornerShape(cornerRadius),
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius),
            )
            .drawBehind {
                // Corner tick decorations — 6dp lines extending from each corner
                val tickLengthPx = tickLength.toPx()
                val strokeWidthPx = borderWidth.toPx()

                drawCornerTicks(
                    tickLength = tickLengthPx,
                    strokeWidth = strokeWidthPx,
                    color = borderColor,
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(spacing.sm)
        ) {
            if (title != null) {
                Text(
                    text = title.uppercase(),
                    style = typography.sectionHeader,
                    color = colors.cyanDim,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.xs),
                )
                HudDivider()
            }
            content()
        }
    }
}

/**
 * Draws corner tick marks at all four corners of the drawing area.
 * Each tick is a pair of lines (horizontal + vertical) extending outward from the corner.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCornerTicks(
    tickLength: Float,
    strokeWidth: Float,
    color: Color,
) {
    val w = size.width
    val h = size.height

    // Top-left corner
    drawLine(color, Offset(0f, 0f), Offset(tickLength, 0f), strokeWidth)
    drawLine(color, Offset(0f, 0f), Offset(0f, tickLength), strokeWidth)

    // Top-right corner
    drawLine(color, Offset(w, 0f), Offset(w - tickLength, 0f), strokeWidth)
    drawLine(color, Offset(w, 0f), Offset(w, tickLength), strokeWidth)

    // Bottom-left corner
    drawLine(color, Offset(0f, h), Offset(tickLength, h), strokeWidth)
    drawLine(color, Offset(0f, h), Offset(0f, h - tickLength), strokeWidth)

    // Bottom-right corner
    drawLine(color, Offset(w, h), Offset(w - tickLength, h), strokeWidth)
    drawLine(color, Offset(w, h), Offset(w, h - tickLength), strokeWidth)
}

/**
 * Thin horizontal divider line for HUD panels.
 *
 * Uses `aegis.divider` color at 1dp height.
 */
@Composable
fun HudDivider(
    modifier: Modifier = Modifier,
    color: Color = AegisTheme.colors.divider,
    thickness: Dp = AegisTheme.spacing.panelBorderWidth,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AegisTheme.spacing.xs)
            .background(color)
            .then(
                Modifier.padding(bottom = thickness)
            )
    )
}
