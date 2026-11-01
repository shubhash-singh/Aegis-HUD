package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Quick-launch row — horizontal strip of app shortcut slots.
 *
 * Phase 3 renders static placeholder slots (empty bordered squares).
 * Phase 4 (F-004) will populate with real app icons from PackageManager.
 *
 * Design from UI_UX_SPECIFICATION.md Section 5 "Quick launch zone"
 * and Section 6 "App Icon Grid": 48dp icons, app name below, 4-5 columns.
 *
 * @param slotCount Number of placeholder slots to display.
 */
@Composable
fun QuickLaunchRow(
    modifier: Modifier = Modifier,
    slotCount: Int = 5,
) {
    val spacing = AegisTheme.spacing

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Quick launch apps" },
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(horizontal = spacing.sm),
    ) {
        items(slotCount) { index ->
            QuickLaunchSlot(
                label = "APP ${index + 1}",
            )
        }
    }
}

/**
 * A single quick-launch slot — placeholder for Phase 3.
 * Shows a bordered square with a label below.
 */
@Composable
private fun QuickLaunchSlot(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Column(
        modifier = modifier.semantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Icon placeholder — bordered square
        Box(
            modifier = Modifier
                .size(spacing.appIconSize)
                .background(
                    color = colors.surface,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                )
                .border(
                    width = spacing.panelBorderWidth,
                    color = colors.border,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "—",
                style = typography.telemetry,
                color = colors.textMuted,
            )
        }
        // App name label
        Text(
            text = label,
            style = typography.label,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.size(width = spacing.appIconSize + 8.dp, height = 18.dp),
        )
    }
}
