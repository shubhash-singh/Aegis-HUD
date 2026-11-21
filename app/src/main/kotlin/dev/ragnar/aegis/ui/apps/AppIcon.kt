package dev.ragnar.aegis.ui.apps

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import dev.ragnar.aegis.domain.apps.AppInfo

/**
 * High-performance native Compose application icon display.
 *
 * Uses cached [ImageBitmap] to deliver zero-allocation, 60+ FPS smooth drawer scrolling
 * without the heavy layout overhead of [androidx.compose.ui.viewinterop.AndroidView].
 */
@Composable
fun AppIcon(
    app: AppInfo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = remember(app.componentName) {
        AppIconCache.getOrLoad(context, app)
    }

    Image(
        bitmap = bitmap,
        contentDescription = app.label,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
