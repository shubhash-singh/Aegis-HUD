package dev.ragnar.aegis.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.ui.theme.AegisTheme

/** The launcher application drawer, rendered as a modal HUD panel with integrated search. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    state: AppDrawerUiState,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
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
        containerColor = colors.surfaceElevated,
        contentColor = colors.textPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
        ) {
            Text(
                text = "APPLICATIONS",
                style = typography.sectionHeader,
                color = colors.cyanDim,
            )
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
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = spacing.appIconSize + spacing.lg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentPadding = PaddingValues(vertical = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    items(state.filteredApps, key = AppInfo::componentName) { app ->
                        AppDrawerCell(app = app, onClick = { onAppClick(app) })
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

@Composable
private fun AppDrawerCell(
    app: AppInfo,
    onClick: () -> Unit,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing
    val shape = RoundedCornerShape(spacing.panelCornerRadius)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(spacing.panelBorderWidth, colors.border, shape)
            .clickable(onClick = onClick)
            .padding(spacing.sm)
            .semantics { contentDescription = "Open ${app.label}" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppIcon(app = app, modifier = Modifier.size(spacing.appIconSize))
        Text(
            text = app.label,
            style = typography.appName,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(spacing.appIconSize + spacing.md)
                .padding(top = spacing.xs),
        )
    }
}
