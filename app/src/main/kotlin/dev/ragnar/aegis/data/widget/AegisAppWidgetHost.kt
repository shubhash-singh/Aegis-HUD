package dev.ragnar.aegis.data.widget

import android.appwidget.AppWidgetHost
import android.content.Context

/**
 * Dedicated AppWidgetHost for the AEGIS HUD launcher.
 * Uses persistent host ID 2048 (DEC-018).
 */
class AegisAppWidgetHost(
    context: Context,
    hostId: Int = APPWIDGET_HOST_ID,
) : AppWidgetHost(context, hostId) {

    companion object {
        const val APPWIDGET_HOST_ID = 2048
    }
}
