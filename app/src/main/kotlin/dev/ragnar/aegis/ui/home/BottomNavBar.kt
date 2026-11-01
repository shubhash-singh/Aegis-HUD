package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Bottom navigation bar — provides access to the app drawer.
 *
 * Phase 3 renders a placeholder button.
 * Phase 4 (F-004) will wire this to the actual drawer navigation.
 *
 * Design from UI_UX_SPECIFICATION.md Section 5 "Bottom nav zone":
 * ```
 * [SEARCH BAR]  [DRAWER BUTTON]
 * ```
 *
 * @param onDrawerClick Callback when the drawer button is tapped.
 */
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onDrawerClick: () -> Unit = {},
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Bottom navigation" },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Search bar placeholder
        Box(
            modifier = Modifier
                .weight(1f)
                .height(spacing.minTouchTarget)
                .padding(end = spacing.sm)
                .background(
                    color = colors.surface,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .border(
                    width = spacing.panelBorderWidth,
                    color = colors.border,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .padding(horizontal = spacing.md),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "SEARCH",
                style = typography.label,
                color = colors.textMuted,
            )
        }

        // Drawer button
        Box(
            modifier = Modifier
                .height(spacing.minTouchTarget)
                .background(
                    color = colors.surfaceElevated,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .border(
                    width = spacing.panelBorderWidth,
                    color = colors.borderActive,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .clickable(onClick = onDrawerClick)
                .padding(horizontal = spacing.lg)
                .semantics { contentDescription = "Open app drawer" },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "▦ APPS",
                style = typography.label,
                color = colors.cyan,
            )
        }
    }
}
