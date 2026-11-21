package dev.ragnar.aegis.ui.widget

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.domain.widget.HostedWidget
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Tactical HUD overlay deck displaying all active hosted widgets in a cybernetic container stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TacticalWidgetDeck(
    widgets: List<HostedWidget>,
    onAddWidgetClick: () -> Unit,
    onCycleHeight: (Int) -> Unit,
    onRemoveWidget: (HostedWidget) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xC0010812),
        dragHandle = null,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEA071E30),
                            Color(0xF9010710),
                        ),
                    ),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.cyan.copy(alpha = 0.85f),
                            colors.cyan.copy(alpha = 0.20f),
                        ),
                    ),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                )
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .semantics { contentDescription = "Tactical Widget Deck" },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
            ) {
                // HUD drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 8.dp)
                        .width(48.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.cyan.copy(alpha = 0.5f))
                        .align(Alignment.CenterHorizontally),
                )

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "TACTICAL WIDGET DECK",
                            style = typography.clockSmall,
                            color = colors.cyan,
                        )
                        Text(
                            text = "${widgets.size} MODULES DOCKED // HOST_ID: #2048",
                            style = typography.telemetryLabel,
                            color = colors.textSecondary,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.cyan.copy(alpha = 0.2f))
                                .border(1.dp, colors.cyan, RoundedCornerShape(4.dp))
                                .clickable(onClick = onAddWidgetClick)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = "+ DOCK MODULE",
                                style = typography.telemetryLabel,
                                color = colors.cyan,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.cyan.copy(alpha = 0.15f))
                                .border(1.dp, colors.cyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = "CLOSE",
                                style = typography.telemetryLabel,
                                color = colors.cyan,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deck Content
                if (widgets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NO ACTIVE WIDGET MODULES DOCKED",
                                style = typography.label,
                                color = colors.cyan,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Dock Android application widgets directly into your cybernetic HUD display.",
                                style = typography.telemetryLabel,
                                color = colors.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.cyan.copy(alpha = 0.2f))
                                    .border(1.dp, colors.cyan, RoundedCornerShape(6.dp))
                                    .clickable(onClick = onAddWidgetClick)
                                    .padding(horizontal = 18.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    text = "+ BROWSE WIDGET REGISTRY",
                                    style = typography.telemetryLabel,
                                    color = colors.cyan,
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(widgets, key = { it.appWidgetId }) { widget ->
                            HudWidgetContainer(
                                widget = widget,
                                onCycleHeight = { onCycleHeight(widget.appWidgetId) },
                                onRemove = { onRemoveWidget(widget) },
                            )
                        }
                    }
                }
            }
        }
    }
}
