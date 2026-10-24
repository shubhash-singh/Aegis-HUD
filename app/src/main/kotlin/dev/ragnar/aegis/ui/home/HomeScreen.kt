package dev.ragnar.aegis.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.ragnar.aegis.ui.theme.AegisColors
import dev.ragnar.aegis.ui.theme.AegisTypography

/**
 * Minimal HUD home screen placeholder for Phase 1.
 *
 * Displays the AEGIS system label and a status line.
 * BackHandler prevents the launcher from finishing on back press (ARCHITECTURE.md Section 6).
 * System insets are handled per UI_UX_SPECIFICATION.md Section 8.
 *
 * Phase 2 will replace this with the full HUD dashboard layout.
 */
@Composable
fun HomeScreen() {
    // Launcher must not finish on back press — no-op handler.
    BackHandler {
        // Intentionally empty: a home-screen launcher does not exit on back press.
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .semantics { contentDescription = "AEGIS Home Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "AEGIS",
            style = AegisTypography.clock,
            color = AegisColors.Cyan,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "SYSTEM ONLINE",
            style = AegisTypography.telemetryLabel,
            color = AegisColors.CyanDim,
            textAlign = TextAlign.Center,
        )
    }
}
