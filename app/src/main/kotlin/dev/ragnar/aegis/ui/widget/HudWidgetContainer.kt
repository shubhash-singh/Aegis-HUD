package dev.ragnar.aegis.ui.widget

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.ragnar.aegis.domain.widget.HostedWidget
import dev.ragnar.aegis.ui.components.StatusIndicator
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Tactical cyber-styled container framing a hosted third-party Android AppWidget.
 *
 * Implements:
 * - Frosted glassmorphism background with multi-stage cyan glow
 * - HUD header with tactical metadata, height cycler, and purge action
 * - Resilient AndroidView lifecycle rendering with runCatching isolation
 * - Orphan recovery state when a provider application has been uninstalled
 */
@Composable
fun HudWidgetContainer(
    widget: HostedWidget,
    modifier: Modifier = Modifier,
    controller: WidgetHostController? = LocalWidgetHostController.current,
    onCycleHeight: (Int) -> Unit = {},
    onRemove: () -> Unit = {},
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val context = LocalContext.current

    val appWidgetInfo = remember(widget.appWidgetId, controller) {
        controller?.appWidgetManager?.getAppWidgetInfo(widget.appWidgetId)
    }

    // Update AppWidgetOptions bundle when height changes (F-012-P6)
    LaunchedEffect(widget.appWidgetId, widget.heightDp, appWidgetInfo) {
        if (controller != null && appWidgetInfo != null) {
            runCatching {
                val density = context.resources.displayMetrics.density
                val widthPx = context.resources.displayMetrics.widthPixels
                val options = Bundle().apply {
                    putInt(android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, widget.heightDp)
                    putInt(android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, widget.heightDp)
                    putInt(android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, (widthPx / density).toInt())
                    putInt(android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, (widthPx / density).toInt())
                }
                controller.appWidgetManager.updateAppWidgetOptions(widget.appWidgetId, options)
            }
        }
    }

    val activeBorderColor = colors.cyan
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBehind {
                // Multi-stage glowing aura bloom
                drawRect(
                    color = activeBorderColor.copy(alpha = 0.08f),
                    size = size,
                )
                // Specular top highlight
                drawLine(
                    color = activeBorderColor.copy(alpha = 0.35f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE008243A),
                        Color(0xF8010810),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        activeBorderColor.copy(alpha = 0.70f),
                        activeBorderColor.copy(alpha = 0.25f),
                    ),
                ),
                shape = shape,
            )
            .padding(10.dp)
            .semantics { contentDescription = "Hosted Widget ${widget.label}" },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // --- TOP HEADER BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    StatusIndicator(
                        status = if (appWidgetInfo != null) StatusLevel.Ok else StatusLevel.Warning,
                        animate = false,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WIDGET // ${widget.label.uppercase()}",
                        style = typography.label,
                        color = colors.cyan,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${widget.appWidgetId}",
                        style = typography.telemetryLabel,
                        color = colors.textMuted,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Height cycle control
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(activeBorderColor.copy(alpha = 0.15f))
                            .border(0.5.dp, activeBorderColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable { onCycleHeight(widget.appWidgetId) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "${widget.heightDp} DP",
                            style = typography.telemetryLabel.copy(fontSize = 9.sp),
                            color = colors.cyan,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Remove widget control
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.statusCritical.copy(alpha = 0.15f))
                            .border(0.5.dp, colors.statusCritical.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable(onClick = onRemove)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "✕",
                            style = typography.telemetryLabel.copy(fontSize = 10.sp),
                            color = colors.statusCritical,
                        )
                    }
                }
            }

            // --- WIDGET CONTENT AREA ---
            if (appWidgetInfo == null) {
                // Orphaned provider state (F-012-P6)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(widget.heightDp.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x30150505))
                        .border(1.dp, colors.statusWarning.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚠ ORPHANED WIDGET MODULE",
                            style = typography.label,
                            color = colors.statusWarning,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Provider application unavailable or uninstalled.",
                            style = typography.telemetryLabel,
                            color = colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.statusCritical.copy(alpha = 0.2f))
                                .border(1.dp, colors.statusCritical, RoundedCornerShape(4.dp))
                                .clickable(onClick = onRemove)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "PURGE ORPHANED MODULE",
                                style = typography.telemetryLabel,
                                color = colors.statusCritical,
                            )
                        }
                    }
                }
            } else if (controller != null) {
                // Resilient AndroidView embed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(widget.heightDp.dp)
                        .clip(RoundedCornerShape(6.dp)),
                ) {
                    AndroidView(
                        factory = { ctx ->
                            runCatching {
                                controller.appWidgetHost.createView(ctx, widget.appWidgetId, appWidgetInfo).apply {
                                    setAppWidget(widget.appWidgetId, appWidgetInfo)
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                    )
                                }
                            }.getOrElse { error ->
                                TextView(ctx).apply {
                                    text = "REMOTE_RENDER_FAULT: ${error.localizedMessage}"
                                    setTextColor(android.graphics.Color.RED)
                                    textSize = 12f
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(widget.heightDp.dp),
                    )
                }
            } else {
                // Preview / non-controller fallback
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(widget.heightDp.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x20000000))
                        .border(1.dp, activeBorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "[HOST VIEW ACTIVE // ID: #${widget.appWidgetId}]",
                        style = typography.telemetryLabel,
                        color = colors.textMuted,
                    )
                }
            }
        }
    }
}
