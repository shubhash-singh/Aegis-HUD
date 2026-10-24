package dev.ragnar.aegis.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * AEGIS HUD theme wrapper.
 *
 * Phase 1 stub: sets the HUD background color and provides a container for content.
 * Phase 2 will add CompositionLocals for AegisColors, AegisTypography, and AegisSpacing
 * as described in ARCHITECTURE.md Section 7.
 */
@Composable
fun AegisTheme(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisColors.Background)
    ) {
        content()
    }
}
