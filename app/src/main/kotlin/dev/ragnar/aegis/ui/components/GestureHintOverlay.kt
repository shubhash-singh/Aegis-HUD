package dev.ragnar.aegis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Representation of a configured gesture and its action label.
 */
data class GestureHint(
    val symbol: String,
    val label: String,
)

/**
 * Cyber-tactical semi-transparent HUD overlay displaying available gesture shortcuts.
 * Dismisses on click anywhere.
 */
@Composable
fun GestureHintOverlay(
    hints: List<GestureHint>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    color = colors.surface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .border(
                    width = spacing.panelBorderWidth,
                    color = colors.cyan,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "GESTURE // NAVIGATION",
                style = typography.clockSmall,
                color = colors.cyan,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.md))

            if (hints.isEmpty()) {
                Text(
                    text = "ALL GESTURES DISABLED IN SETTINGS",
                    style = typography.body,
                    color = colors.textMuted,
                    textAlign = TextAlign.Center,
                )
            } else {
                hints.forEach { hint ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = hint.symbol,
                            style = typography.label,
                            color = colors.cyan,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Text(
                            text = hint.label,
                            style = typography.body,
                            color = colors.textPrimary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            Text(
                text = "[ TAP ANYWHERE TO DISMISS ]",
                style = typography.telemetryLabel,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
