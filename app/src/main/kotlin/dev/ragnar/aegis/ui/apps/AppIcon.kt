package dev.ragnar.aegis.ui.apps

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import dev.ragnar.aegis.data.apps.AndroidAppRepository
import dev.ragnar.aegis.domain.apps.AppInfo

/** Displays the real application icon using the platform drawable, without an image library. */
@Composable
fun AppIcon(
    app: AppInfo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val iconRepository = remember(context.applicationContext) {
        AndroidAppRepository(context.applicationContext)
    }
    val icon = remember(app.componentName) { iconRepository.iconFor(app) }

    AndroidView(
        modifier = modifier,
        factory = {
            ImageView(it).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = app.label
            }
        },
        update = { imageView ->
            imageView.setImageDrawable(icon)
            imageView.contentDescription = app.label
        },
    )
}
