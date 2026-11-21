package dev.ragnar.aegis.ui.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import androidx.compose.runtime.staticCompositionLocalOf
import dev.ragnar.aegis.domain.widget.HostedWidget

/**
 * Controller interface allowing Compose components to coordinate widget binding,
 * configuration, view creation, and deletion via ActivityResultLaunchers in MainActivity.
 */
interface WidgetHostController {
    val appWidgetHost: AppWidgetHost
    val appWidgetManager: AppWidgetManager

    fun requestAddWidget(
        provider: AppWidgetProviderInfo,
        onSuccess: (HostedWidget) -> Unit,
        onError: (String) -> Unit = {},
    )

    fun deleteWidget(appWidgetId: Int)
}

val LocalWidgetHostController = staticCompositionLocalOf<WidgetHostController?> { null }
