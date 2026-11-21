package dev.ragnar.aegis.ui.quickactions

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ragnar.aegis.ui.components.HudPanel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Quick Actions panel (F-009) — a row of shortcut buttons in the HUD.
 *
 * Displays four action buttons:
 *  - ⚙ SETTINGS  — opens Android Settings
 *  - ⚡ TORCH     — toggles device flashlight (disabled if hardware unavailable)
 *  - ⌨ WI-FI     — opens Wi-Fi Settings
 *  - ✦ BT        — opens Bluetooth Settings
 *
 * Design: matches HUD panel aesthetic using AegisTheme tokens.
 * No permissions beyond those already declared; torch uses CameraManager (no CAMERA permission needed for torch on API 23+).
 */
@Composable
fun QuickActionsPanel(
    modifier: Modifier = Modifier,
    viewModel: QuickActionsViewModel = viewModel(
        factory = run {
            val context = LocalContext.current
            remember(context.applicationContext) {
                QuickActionsViewModelFactory(context.applicationContext)
            }
        }
    ),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val colors = AegisTheme.colors
    val spacing = AegisTheme.spacing
    val typography = AegisTheme.typography

    HudPanel(
        modifier = modifier,
        title = "Quick Actions",
    ) {
        Spacer(modifier = Modifier.height(spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // ⚙ Settings
            QuickActionButton(
                label = "SETTINGS",
                icon = "⚙",
                isActive = false,
                isAvailable = true,
                contentDescription = "Open Settings",
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
            )

            // ⚡ Torch
            QuickActionButton(
                label = if (uiState.isTorchOn) "TORCH ON" else "TORCH",
                icon = if (uiState.isTorchOn) "◉" else "○",
                isActive = uiState.isTorchOn,
                isAvailable = uiState.isTorchAvailable,
                contentDescription = if (uiState.isTorchOn) "Torch on — tap to disable" else "Torch off — tap to enable",
                onClick = {
                    if (uiState.isTorchAvailable) viewModel.toggleTorch()
                },
            )

            // ⌨ Wi-Fi
            QuickActionButton(
                label = "WI-FI",
                icon = "⌘",
                isActive = false,
                isAvailable = true,
                contentDescription = "Open Wi-Fi Settings",
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
            )

            // ✦ Bluetooth
            QuickActionButton(
                label = "BT",
                icon = "✦",
                isActive = false,
                isAvailable = true,
                contentDescription = "Open Bluetooth Settings",
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
            )
        }

        // Torch error feedback
        if (uiState.torchError != null) {
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = "⚠ ${uiState.torchError}",
                style = typography.telemetryLabel,
                color = colors.statusWarning,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.xs),
            )
        }
    }
}

/**
 * A single quick-action button.
 *
 * @param label         Short text label shown below the icon.
 * @param icon          Unicode icon character displayed in the button.
 * @param isActive      When `true`, highlights with [AegisTheme.colors.borderActive].
 * @param isAvailable   When `false`, renders in muted/disabled style.
 * @param contentDescription Accessibility label.
 * @param onClick       Action to perform on tap (no-op when not available).
 */
@Composable
private fun QuickActionButton(
    label: String,
    icon: String,
    isActive: Boolean,
    isAvailable: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    val borderColor: Color = when {
        !isAvailable -> colors.border.copy(alpha = 0.3f)
        isActive -> colors.borderActive
        else -> colors.border
    }
    val iconColor: Color = when {
        !isAvailable -> colors.textMuted
        isActive -> colors.cyan
        else -> colors.textPrimary
    }
    val labelColor: Color = when {
        !isAvailable -> colors.textMuted
        isActive -> colors.textAccent
        else -> colors.textSecondary
    }
    val bgColor: Color = if (isActive) colors.surfaceElevated else colors.surface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .semantics { this.contentDescription = contentDescription }
            .then(if (isAvailable) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(spacing.minTouchTarget)
                .background(color = bgColor, shape = RoundedCornerShape(spacing.panelCornerRadius))
                .border(
                    width = spacing.panelBorderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(spacing.panelCornerRadius),
                ),
        ) {
            Text(
                text = icon,
                style = typography.telemetry,
                color = iconColor,
            )
        }
        Text(
            text = label,
            style = typography.telemetryLabel,
            color = labelColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = spacing.xs),
        )
    }
}
