package dev.ragnar.aegis.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.ui.apps.AppIcon
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Quick-launch row — horizontal strip of app shortcut slots.
 *
 * Phase 4 displays the first alphabetically sorted applications supplied by the app repository.
 *
 * Design from UI_UX_SPECIFICATION.md Section 5 "Quick launch zone"
 * and Section 6 "App Icon Grid": 48dp icons, app name below, 4-5 columns.
 *
 * @param apps Launchable apps; the first [maxSlots] populate the row.
 * @param onAppClick Starts a selected application.
 */
@Composable
fun QuickLaunchRow(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    maxSlots: Int = 5,
) {
    val spacing = AegisTheme.spacing

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Quick launch apps" },
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(horizontal = spacing.sm),
    ) {
        if (apps.isEmpty()) {
            item { QuickLaunchPlaceholder() }
        } else {
            items(
                items = apps.take(maxSlots),
                key = AppInfo::componentName,
            ) { app ->
                QuickLaunchSlot(app = app, onClick = { onAppClick(app) })
            }
        }
    }
}

/**
 * A single quick-launch slot showing the activity icon and application label.
 */
@Composable
private fun QuickLaunchSlot(
    app: AppInfo,
    onClick: () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Column(
        modifier = Modifier
            .semantics { contentDescription = "Open ${app.label}" }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
            AppIcon(app = app, modifier = Modifier.size(spacing.appIconSize))
        }
        Text(
            text = app.label,
            style = typography.label,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(spacing.appIconSize + spacing.md),
        )
    }
}

@Composable
private fun QuickLaunchPlaceholder() {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Text(
        text = "NO QUICK-LAUNCH APPS",
        style = typography.label,
        color = colors.textMuted,
        modifier = Modifier.padding(vertical = spacing.sm),
    )
}
