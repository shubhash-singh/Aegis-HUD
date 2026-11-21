package dev.ragnar.aegis.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import dev.ragnar.aegis.ui.components.HudPanel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Full-screen HUD customization overlay / sheet (F-010, F-011).
 */
@Composable
fun SettingsSheet(
    preferences: UserPreferences,
    onAccentColorChange: (AccentColor) -> Unit,
    onAnimationSpeedChange: (AnimationSpeed) -> Unit,
    onToggleRadar: (Boolean) -> Unit,
    onToggleSystemStatus: (Boolean) -> Unit,
    onToggleTelemetry: (Boolean) -> Unit,
    onToggleQuickActions: (Boolean) -> Unit,
    onToggle24HourClock: (Boolean) -> Unit,
    onToggleClockSeconds: (Boolean) -> Unit,
    onAppSortOrderChange: (AppSortOrder) -> Unit = {},
    onGridColumnsChange: (Int) -> Unit = {},
    onClearFavorites: () -> Unit = {},
    onResetDefaults: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background.copy(alpha = 0.96f))
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics { contentDescription = "AEGIS Customization Settings" },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "SYS // CONFIGURATION",
                    style = typography.clockSmall,
                    color = colors.cyan,
                )
                Box(
                    modifier = Modifier
                        .background(colors.surfaceElevated, RoundedCornerShape(spacing.panelCornerRadius))
                        .border(spacing.panelBorderWidth, colors.borderActive, RoundedCornerShape(spacing.panelCornerRadius))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = spacing.md, vertical = spacing.xs),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "CLOSE [✕]",
                        style = typography.label,
                        color = colors.cyan,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // 1. Accent Color
            HudPanel(title = "HUD Theme Color") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    AccentColor.entries.forEach { accent ->
                        val isSelected = preferences.accentColor == accent
                        val accentPreviewColor = Color(accent.primaryColorArgb)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) colors.surfaceElevated else colors.surface,
                                    RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .border(
                                    width = spacing.panelBorderWidth,
                                    color = if (isSelected) accentPreviewColor else colors.border,
                                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .clickable { onAccentColorChange(accent) }
                                .padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(spacing.md)
                                        .height(spacing.md)
                                        .background(accentPreviewColor, RoundedCornerShape(spacing.panelCornerRadius)),
                                )
                                Spacer(modifier = Modifier.width(spacing.sm))
                                Text(
                                    text = accent.displayName,
                                    style = typography.body,
                                    color = if (isSelected) colors.textPrimary else colors.textSecondary,
                                )
                            }
                            Text(
                                text = if (isSelected) "[ACTIVE]" else "[SELECT]",
                                style = typography.telemetryLabel,
                                color = if (isSelected) accentPreviewColor else colors.textMuted,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 2. Radar Animation Speed
            HudPanel(title = "Radar Animation") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    AnimationSpeed.entries.forEach { speed ->
                        val isSelected = preferences.animationSpeed == speed
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) colors.surfaceElevated else colors.surface,
                                    RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .border(
                                    width = spacing.panelBorderWidth,
                                    color = if (isSelected) colors.borderActive else colors.border,
                                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .clickable { onAnimationSpeedChange(speed) }
                                .padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = speed.displayName,
                                style = typography.body,
                                color = if (isSelected) colors.textPrimary else colors.textSecondary,
                            )
                            Text(
                                text = if (isSelected) "[ACTIVE]" else "[SELECT]",
                                style = typography.telemetryLabel,
                                color = if (isSelected) colors.cyan else colors.textMuted,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 3. Panel Visibility
            HudPanel(title = "Panel Visibility") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    SettingsToggleRow(
                        label = "RADAR DISPLAY",
                        isChecked = preferences.showRadar,
                        onToggle = onToggleRadar,
                    )
                    SettingsToggleRow(
                        label = "SYSTEM STATUS",
                        isChecked = preferences.showSystemStatus,
                        onToggle = onToggleSystemStatus,
                    )
                    SettingsToggleRow(
                        label = "TELEMETRY METRICS",
                        isChecked = preferences.showTelemetry,
                        onToggle = onToggleTelemetry,
                    )
                    SettingsToggleRow(
                        label = "QUICK ACTIONS",
                        isChecked = preferences.showQuickActions,
                        onToggle = onToggleQuickActions,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 4. Time Display
            HudPanel(title = "Time & Clock") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    SettingsToggleRow(
                        label = "24-HOUR CLOCK",
                        isChecked = preferences.is24HourClock,
                        onToggle = onToggle24HourClock,
                    )
                    SettingsToggleRow(
                        label = "SHOW SECONDS",
                        isChecked = preferences.showClockSeconds,
                        onToggle = onToggleClockSeconds,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 5. Application Drawer Configuration (F-011)
            HudPanel(title = "App Drawer Organization") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "DEFAULT SORT ORDER",
                        style = typography.telemetryLabel,
                        color = colors.cyanDim,
                    )
                    AppSortOrder.entries.forEach { sortOrder ->
                        val isSelected = preferences.appSortOrder == sortOrder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) colors.surfaceElevated else colors.surface,
                                    RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .border(
                                    width = spacing.panelBorderWidth,
                                    color = if (isSelected) colors.borderActive else colors.border,
                                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                                )
                                .clickable { onAppSortOrderChange(sortOrder) }
                                .padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = sortOrder.displayName,
                                style = typography.body,
                                color = if (isSelected) colors.textPrimary else colors.textSecondary,
                            )
                            Text(
                                text = if (isSelected) "[ACTIVE]" else "[SELECT]",
                                style = typography.telemetryLabel,
                                color = if (isSelected) colors.cyan else colors.textMuted,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    Text(
                        text = "DRAWER GRID COLUMNS",
                        style = typography.telemetryLabel,
                        color = colors.cyanDim,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        listOf(3, 4, 5).forEach { cols ->
                            val isSelected = preferences.drawerGridColumns == cols
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) colors.surfaceElevated else colors.surface,
                                        RoundedCornerShape(spacing.panelCornerRadius),
                                    )
                                    .border(
                                        width = spacing.panelBorderWidth,
                                        color = if (isSelected) colors.borderActive else colors.border,
                                        shape = RoundedCornerShape(spacing.panelCornerRadius),
                                    )
                                    .clickable { onGridColumnsChange(cols) }
                                    .padding(vertical = spacing.sm),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "$cols COL",
                                    style = typography.telemetryLabel,
                                    color = if (isSelected) colors.cyan else colors.textSecondary,
                                )
                            }
                        }
                    }

                    if (preferences.favoriteApps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                                .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                                .clickable(onClick = onClearFavorites)
                                .padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "PINNED APPS (${preferences.favoriteApps.size})",
                                style = typography.body,
                                color = colors.textPrimary,
                            )
                            Text(
                                text = "CLEAR ALL [✕]",
                                style = typography.telemetryLabel,
                                color = colors.statusWarning,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Reset Defaults Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                    .border(spacing.panelBorderWidth, colors.statusWarning, RoundedCornerShape(spacing.panelCornerRadius))
                    .clickable(onClick = onResetDefaults)
                    .padding(vertical = spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "RESET TO SYSTEM DEFAULTS",
                    style = typography.label,
                    color = colors.statusWarning,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isChecked) colors.surfaceElevated else colors.surface,
                RoundedCornerShape(spacing.panelCornerRadius),
            )
            .border(
                width = spacing.panelBorderWidth,
                color = if (isChecked) colors.borderActive else colors.border,
                shape = RoundedCornerShape(spacing.panelCornerRadius),
            )
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = typography.body,
            color = if (isChecked) colors.textPrimary else colors.textSecondary,
        )
        Text(
            text = if (isChecked) "ON [▣]" else "OFF [▢]",
            style = typography.telemetryLabel,
            color = if (isChecked) colors.cyan else colors.textMuted,
        )
    }
}
