package dev.ragnar.aegis.ui.widget

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
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.domain.widget.WidgetProviderSummary
import dev.ragnar.aegis.ui.apps.HudSearchBar
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Tactical cybernetic modal sheet allowing the user to search and select
 * an installed third-party widget provider to host on AEGIS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerSheet(
    providers: List<WidgetProviderSummary>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSelectProvider: (WidgetProviderSummary) -> Unit,
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
        scrimColor = Color(0xB0010812),
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
                            colors.cyan.copy(alpha = 0.15f),
                        ),
                    ),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                )
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .semantics { contentDescription = "System Widget Registry Picker" },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp),
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
                            text = "SYSTEM WIDGET REGISTRY",
                            style = typography.clockSmall,
                            color = colors.cyan,
                        )
                        Text(
                            text = "SELECT COMPATIBLE MODULE TO DOCK // HOST #2048",
                            style = typography.telemetryLabel,
                            color = colors.textSecondary,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.cyan.copy(alpha = 0.15f))
                            .border(1.dp, colors.cyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "CLOSE",
                            style = typography.telemetryLabel,
                            color = colors.cyan,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                HudSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClear = onClearSearch,
                    onSearch = {},
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Available Providers List
                if (providers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NO MATCHING WIDGET MODULES",
                                style = typography.label,
                                color = colors.statusWarning,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No installed application provides matching widgets.",
                                style = typography.telemetryLabel,
                                color = colors.textMuted,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(providers, key = { "${it.providerPackage}/${it.providerClass}" }) { provider ->
                            WidgetProviderRow(
                                provider = provider,
                                onSelect = { onSelectProvider(provider) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetProviderRow(
    provider: WidgetProviderSummary,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x35031422))
            .border(0.5.dp, colors.cyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable(onClick = onSelect)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = provider.appName.uppercase(),
                style = typography.telemetryLabel.copy(fontSize = 10.sp),
                color = colors.textMuted,
            )
            Text(
                text = provider.label,
                style = typography.body,
                color = colors.textPrimary,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "[${provider.minWidthDp}×${provider.minHeightDp} DP]",
                    style = typography.telemetryLabel.copy(fontSize = 9.sp),
                    color = colors.cyan,
                )
                if (provider.configureClass != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.cyan.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = "CONFIG REQUIRED",
                            style = typography.telemetryLabel.copy(fontSize = 8.sp),
                            color = colors.cyan,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(colors.cyan.copy(alpha = 0.2f))
                .border(1.dp, colors.cyan, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = "+ DOCK",
                style = typography.telemetryLabel,
                color = colors.cyan,
            )
        }
    }
}
