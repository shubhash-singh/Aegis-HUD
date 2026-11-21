package dev.ragnar.aegis.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD-styled search bar for the app drawer.
 *
 * Uses monospaced typography, dark surface background, and cyan border on focus.
 * Provides a clear button (✕) when query is non-empty and an IME search action.
 *
 * @param query Current search text.
 * @param onQueryChange Called as the user types.
 * @param onClear Called when the clear button is tapped.
 * @param onSearch Called when the IME search/enter action is pressed.
 * @param requestFocus When true, the text field requests focus immediately (auto-opens keyboard).
 */
@Composable
fun HudSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    requestFocus: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing
    val shape = RoundedCornerShape(spacing.panelCornerRadius)
    val focusRequester = remember { FocusRequester() }

    if (requestFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = spacing.minTouchTarget)
            .background(colors.surface, shape)
            .border(spacing.panelBorderWidth, colors.borderActive, shape)
            .padding(horizontal = spacing.md)
            .semantics { contentDescription = "Search applications" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Monospaced search prefix indicator
        Text(
            text = "\uD83D\uDD0E",
            style = typography.telemetry,
            color = colors.cyanDim,
        )

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "SEARCH APPLICATIONS",
                    style = typography.telemetry,
                    color = colors.textMuted,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = typography.telemetry.copy(color = colors.textPrimary),
                singleLine = true,
                cursorBrush = SolidColor(colors.cyan),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() },
                ),
            )
        }

        // Clear button with 48dp minimum touch target
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = spacing.minTouchTarget, minHeight = spacing.minTouchTarget)
                    .clickable(onClick = onClear)
                    .semantics { contentDescription = "Clear search" },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✕",
                    style = typography.telemetry,
                    color = colors.cyan,
                )
            }
        }
    }
}
