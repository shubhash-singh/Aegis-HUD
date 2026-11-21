package dev.ragnar.aegis.ui.apps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * The launcher application drawer, rendered as a modal tactical HUD panel.
 *
 * Supports:
 * - Real-time application search
 * - Pinned / favorite applications section with quick 1-tap launching
 * - Dynamic sorting (A-Z, Z-A, Favorites First, Recently Installed)
 * - Configurable grid layout (3, 4, or 5 columns)
 * - Long-press to pin / favorite applications
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppDrawer(
    state: AppDrawerUiState,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit = {},
    onCycleSortOrder: () -> Unit = {},
    onCycleGridColumns: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onSearchAction: () -> Unit = {},
    initialSearchFocus: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.semantics { contentDescription = "Application drawer" },
        containerColor = Color.Transparent,
        contentColor = colors.textPrimary,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xAA071E30), // More transparent — lets neural bg show through
                            Color(0xBB030F1A),
                            Color(0xCC010710),
                        ),
                    ),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.cyan.copy(alpha = 0.90f),
                            colors.cyanDim.copy(alpha = 0.30f),
                            Color.Transparent,
                        ),
                    ),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
                .padding(top = spacing.sm, bottom = spacing.lg),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md),
            ) {
                // Sleek HUD drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(3.dp)
                            .background(
                                color = colors.cyan.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(2.dp),
                            ),
                    )
                }

                // Header Bar: Title + Controls (Sort, Grid Density)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "APPLICATIONS [${state.filteredApps.size}]",
                    style = typography.sectionHeader,
                    color = colors.cyanDim,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Sort order button
                    Box(
                        modifier = Modifier
                            .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                            .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                            .clickable(
                                onClick = onCycleSortOrder,
                            )
                            .padding(horizontal = spacing.sm, vertical = spacing.xs)
                            .semantics { contentDescription = "Sort mode: ${state.sortOrder.displayName}" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "SORT: ${state.sortOrder.shortLabel}",
                            style = typography.telemetryLabel.copy(fontSize = 10.sp),
                            color = colors.cyan,
                        )
                    }

                    // Column density button
                    Box(
                        modifier = Modifier
                            .background(colors.surface, RoundedCornerShape(spacing.panelCornerRadius))
                            .border(spacing.panelBorderWidth, colors.border, RoundedCornerShape(spacing.panelCornerRadius))
                            .clickable(
                                onClick = onCycleGridColumns,
                            )
                            .padding(horizontal = spacing.sm, vertical = spacing.xs)
                            .semantics { contentDescription = "Grid columns: ${state.gridColumns}" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${state.gridColumns} COL",
                            style = typography.telemetryLabel.copy(fontSize = 10.sp),
                            color = colors.cyan,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            HudSearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                onClear = onClearSearch,
                onSearch = onSearchAction,
                requestFocus = initialSearchFocus,
            )

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    style = typography.label,
                    color = colors.statusWarning,
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }

            when {
                state.isLoading -> DrawerMessage("LOADING APPLICATIONS")
                state.apps.isEmpty() -> DrawerMessage("NO LAUNCHABLE APPLICATIONS")
                state.isNoResults -> DrawerMessage("NO APPLICATIONS MATCHING \"${state.searchQuery.uppercase()}\"")
                else -> {
                    // When not searching and pinned apps exist, display dedicated Pinned / Quick Launch section
                    if (state.searchQuery.isBlank() && state.pinnedApps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = "PINNED // QUICK LAUNCH [${state.pinnedApps.size}]",
                            style = typography.telemetryLabel.copy(fontSize = 10.sp),
                            color = colors.cyan,
                            modifier = Modifier.padding(vertical = spacing.xs),
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            contentPadding = PaddingValues(vertical = spacing.xs),
                        ) {
                            items(
                                items = state.pinnedApps,
                                key = { "pinned_${it.componentName}" },
                                contentType = { "pinned_cell" },
                            ) { app ->
                                PinnedAppCell(
                                    app = app,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onToggleFavorite(app) },
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = "ALL APPLICATIONS",
                            style = typography.telemetryLabel.copy(fontSize = 10.sp),
                            color = colors.textSecondary,
                            modifier = Modifier.padding(vertical = spacing.xs),
                        )
                    }

                    // Main Application Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(state.gridColumns),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        contentPadding = PaddingValues(vertical = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        items(
                            items = state.filteredApps,
                            key = AppInfo::componentName,
                            contentType = { "app_cell" },
                        ) { app ->
                            val isPinned = app.componentName in state.favoriteComponents
                            AppDrawerCell(
                                app = app,
                                isPinned = isPinned,
                                onClick = { onAppClick(app) },
                                onLongClick = { onToggleFavorite(app) },
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun DrawerMessage(message: String) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Text(
        text = message,
        style = typography.telemetry,
        color = colors.textSecondary,
        modifier = Modifier.padding(vertical = spacing.xl),
    )
}

/** Pinned application horizontal quick-launch item. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PinnedAppCell(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing
    val shape = RoundedCornerShape(spacing.panelCornerRadius)

    Column(
        modifier = Modifier
            .width(68.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.surfaceElevated.copy(alpha = 0.95f),
                        colors.surface.copy(alpha = 0.75f),
                    ),
                    radius = 140f,
                ),
                shape = shape,
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(spacing.xs)
            .semantics { contentDescription = "Pinned app: ${app.label}. Long press to unpin." },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AppIcon(app = app, modifier = Modifier.size(36.dp))
            Text(
                text = "★",
                color = colors.cyan,
                fontSize = 10.sp,
                modifier = Modifier.padding(1.dp),
            )
        }
        Text(
            text = app.label,
            style = typography.appName.copy(fontSize = 10.sp),
            color = colors.cyan,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Main application grid cell. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppDrawerCell(
    app: AppInfo,
    isPinned: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing
    val shape = RoundedCornerShape(spacing.panelCornerRadius)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.radialGradient(
                    colors = if (isPinned) listOf(
                        colors.surfaceElevated.copy(alpha = 0.95f),
                        colors.surface.copy(alpha = 0.80f),
                    ) else listOf(
                        colors.surface.copy(alpha = 0.72f),
                        Color(0x55010710),
                    ),
                    radius = 160f,
                ),
                shape = shape,
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(spacing.xs)
            .semantics {
                contentDescription = if (isPinned) {
                    "${app.label} (Pinned). Long press to unpin."
                } else {
                    "Open ${app.label}. Long press to pin."
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AppIcon(app = app, modifier = Modifier.size(spacing.appIconSize))
            if (isPinned) {
                Text(
                    text = "★",
                    color = colors.cyan,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(1.dp),
                )
            }
        }
        Text(
            text = app.label,
            style = typography.appName,
            color = if (isPinned) colors.cyan else colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(spacing.appIconSize + spacing.md)
                .padding(top = spacing.xs),
        )
    }
}
