package dev.ragnar.aegis.ui.home.panels

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.ui.components.HudCubeIcon
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Top-left floating panel: AEGIS AI CORE
 *
 * Visual layout matching reference:
 * [CubeIcon] AEGIS AI CORE ////
 * v0.9.4-α
 */
@Composable
fun FloatingCorePanel(
    modifier: Modifier = Modifier,
    version: String = "v0.9.4-α",
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    FloatingHudPanel(
        modifier = modifier,
        contentPadding = 6.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HudCubeIcon()
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AEGIS AI CORE",
                style = typography.sectionHeader,
                color = colors.cyan,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "////",
                style = typography.sectionHeader.copy(letterSpacing = 1.sp),
                color = colors.cyanDim.copy(alpha = 0.6f),
            )
        }
        Text(
            text = version,
            style = typography.telemetryLabel.copy(fontSize = 10.sp),
            color = colors.textSecondary,
        )
    }
}
