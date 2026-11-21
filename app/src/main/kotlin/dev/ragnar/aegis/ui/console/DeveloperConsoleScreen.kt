package dev.ragnar.aegis.ui.console

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.domain.diagnostics.LogLevel
import dev.ragnar.aegis.ui.components.HudPanel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Developer Console full-screen HUD diagnostics overlay (F-014).
 */
@Composable
fun DeveloperConsoleScreen(
    state: DeveloperConsoleUiState,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onCopyDiagnostics: () -> Unit,
    onSelectLogLevel: (LogLevel?) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing
    val green = Color(0xFF00FF88)
    val amber = Color(0xFFFFB830)
    val crimson = Color(0xFFFF3344)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background.copy(alpha = 0.96f))
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics { contentDescription = "AEGIS Developer Console" },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Header: Title and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "AEGIS // DEV CONSOLE",
                    style = typography.clockSmall.copy(fontSize = 18.sp),
                    color = colors.cyan,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    // Refresh button
                    Box(
                        modifier = Modifier
                            .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                            .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                            .clickable(onClick = onRefresh)
                            .padding(horizontal = spacing.sm, vertical = spacing.xs)
                            .semantics { contentDescription = "Refresh diagnostics" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "REFRESH [⟳]",
                            style = typography.telemetryLabel,
                            color = colors.cyan,
                        )
                    }

                    // Copy button
                    Box(
                        modifier = Modifier
                            .background(colors.surfaceElevated, RoundedCornerShape(spacing.panelCornerRadius))
                            .border(spacing.panelBorderWidth, colors.cyan, RoundedCornerShape(spacing.panelCornerRadius))
                            .clickable(onClick = onCopyDiagnostics)
                            .padding(horizontal = spacing.sm, vertical = spacing.xs)
                            .semantics { contentDescription = "Copy diagnostics to clipboard" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "COPY [📋]",
                            style = typography.telemetryLabel,
                            color = colors.cyan,
                        )
                    }

                    // Close button
                    Box(
                        modifier = Modifier
                            .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                            .border(spacing.panelBorderWidth, colors.borderActive, RoundedCornerShape(spacing.panelCornerRadius))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = spacing.sm, vertical = spacing.xs)
                            .semantics { contentDescription = "Close developer console" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "CLOSE [✕]",
                            style = typography.telemetryLabel,
                            color = colors.cyan,
                        )
                    }
                }
            }

            // Copy feedback toast
            if (state.isCopied) {
                Spacer(modifier = Modifier.height(spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(green.copy(alpha = 0.15f), RoundedCornerShape(spacing.panelCornerRadius))
                        .border(1.dp, green, RoundedCornerShape(spacing.panelCornerRadius))
                        .padding(horizontal = spacing.md, vertical = spacing.xs),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "● DIAGNOSTICS COPIED TO CLIPBOARD",
                        style = typography.telemetry.copy(fontSize = 11.sp),
                        color = green,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            val launcher = state.snapshot.launcherDiagnostics
            val system = state.snapshot.systemInfo

            // 1. Process & Memory Diagnostics
            HudPanel(title = "1. Process & Memory Runtime") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    DiagRow("PROCESS UPTIME", launcher.processUptimeFormatted, green)
                    DiagRow(
                        "HEAP ALLOCATED",
                        "${String.format(java.util.Locale.ROOT, "%.1f", launcher.heapAllocatedMb)} MB / ${String.format(java.util.Locale.ROOT, "%.1f", launcher.heapMaxMb)} MB (${launcher.heapPercent}%)",
                        if (launcher.heapPercent > 80) amber else colors.cyan,
                    )
                    DiagRow("TRACKED APPS", "${launcher.launchableAppsCount} APPS", colors.textPrimary)
                    DiagRow("PINNED FAVORITES", "${launcher.favoriteAppsCount} APPS", colors.cyan)
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 2. Platform & Hardware Information
            HudPanel(title = "2. System & Platform Architecture") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    DiagRow("DEVICE HARDWARE", "${system.manufacturer} ${system.deviceModel}", colors.cyan)
                    DiagRow("ANDROID OS", "Android ${system.androidVersion} (API ${system.apiLevel})", colors.textPrimary)
                    DiagRow("CPU ARCHITECTURE", system.cpuAbi, colors.textPrimary)
                    DiagRow("BUILD IDENTIFIER", system.buildId, colors.textSecondary)
                    DiagRow("SECURITY PATCH", system.securityPatch, green)
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 3. Permission Audit Panel
            HudPanel(title = "3. Manifest Permissions Audit") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    state.snapshot.permissions.forEach { perm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                                .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                                .padding(horizontal = spacing.sm, vertical = spacing.xs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = perm.shortName,
                                style = typography.body.copy(fontSize = 11.sp),
                                color = colors.textPrimary,
                            )
                            Text(
                                text = if (perm.isGranted) "[GRANTED]" else "[DENIED]",
                                style = typography.telemetryLabel.copy(fontSize = 10.sp),
                                color = if (perm.isGranted) green else amber,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // 4. Live Event Log Stream
            HudPanel(title = "4. Diagnostics Ring Buffer (${state.displayedLogs.size} Events)") {
                Column {
                    // Log level filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val filters: List<Pair<String, LogLevel?>> = listOf(
                            "ALL" to null,
                            "DEBUG" to LogLevel.DEBUG,
                            "INFO" to LogLevel.INFO,
                            "WARN" to LogLevel.WARN,
                            "ERROR" to LogLevel.ERROR,
                        )

                        filters.forEach { (label, level) ->
                            val isSelected = state.selectedLogLevel == level
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) colors.surfaceElevated else colors.surface,
                                        RoundedCornerShape(spacing.panelCornerRadius),
                                    )
                                    .border(
                                        width = spacing.panelBorderWidth,
                                        color = if (isSelected) colors.cyan else colors.border,
                                        shape = RoundedCornerShape(spacing.panelCornerRadius),
                                    )
                                    .clickable { onSelectLogLevel(level) }
                                    .padding(horizontal = spacing.xs, vertical = 2.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = label,
                                    style = typography.telemetryLabel.copy(fontSize = 9.sp),
                                    color = if (isSelected) colors.cyan else colors.textSecondary,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Clear logs button
                        Box(
                            modifier = Modifier
                                .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                                .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                                .clickable(onClick = onClearLogs)
                                .padding(horizontal = spacing.xs, vertical = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "CLEAR [✕]",
                                style = typography.telemetryLabel.copy(fontSize = 9.sp),
                                color = amber,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    // Monospace terminal viewer box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp, max = 260.dp)
                            .background(Color(0xFF02070D), RoundedCornerShape(spacing.panelCornerRadius))
                            .border(1.dp, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                            .padding(spacing.xs),
                    ) {
                        if (state.displayedLogs.isEmpty()) {
                            Text(
                                text = "NO EVENT LOGS RECORDED",
                                style = typography.telemetryLabel.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                color = colors.textMuted,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(2.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                items(state.displayedLogs) { entry ->
                                    val entryColor = when (entry.level) {
                                        LogLevel.DEBUG -> colors.cyanDim
                                        LogLevel.INFO -> colors.cyan
                                        LogLevel.WARN -> amber
                                        LogLevel.ERROR -> crimson
                                    }
                                    Text(
                                        text = "[${entry.timestampFormatted}] [${entry.level}] ${entry.tag}: ${entry.message}",
                                        style = typography.telemetryLabel.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp,
                                        ),
                                        color = entryColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun DiagRow(
    label: String,
    value: String,
    valueColor: Color,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
            .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
            .padding(horizontal = spacing.sm, vertical = spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.body.copy(fontSize = 11.sp),
            color = colors.textSecondary,
        )
        Text(
            text = value,
            style = typography.telemetry.copy(fontSize = 11.sp),
            color = valueColor,
        )
    }
}
