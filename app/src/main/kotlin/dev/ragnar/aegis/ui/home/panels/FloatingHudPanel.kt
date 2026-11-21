package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Holographic floating HUD panel container matching the reference design.
 *
 * Visual style:
 * - Rounded rectangle with 8dp radius
 * - Translucent deep cyan-navy background (#031422 @ 82% alpha)
 * - 1dp glowing cyan border (#00E5FF @ 75% alpha)
 * - Optional title row with micro-icon and section title
 */
@Composable
fun FloatingHudPanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    borderColor: Color = AegisTheme.colors.borderActive.copy(alpha = 0.72f),
    backgroundColor: Color = Color(0xFF031422).copy(alpha = 0.82f),
    cornerRadius: Dp = 8.dp,
    contentPadding: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
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
