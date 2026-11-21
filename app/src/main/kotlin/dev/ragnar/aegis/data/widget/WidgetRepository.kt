package dev.ragnar.aegis.data.widget

import dev.ragnar.aegis.domain.widget.HostedWidget
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for persisting and managing hosted widgets.
 */
interface WidgetRepository {
    val hostedWidgets: Flow<List<HostedWidget>>
    suspend fun addWidget(widget: HostedWidget)
    suspend fun removeWidget(appWidgetId: Int)
    suspend fun updateWidgetHeight(appWidgetId: Int, heightDp: Int)
    suspend fun clearAllWidgets()
}
