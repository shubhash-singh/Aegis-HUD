package dev.ragnar.aegis.data.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.ragnar.aegis.domain.widget.HostedWidget
import dev.ragnar.aegis.domain.widget.WidgetSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "aegis_hosted_widgets")

/**
 * Implementation of [WidgetRepository] backed by AndroidX DataStore Preferences.
 */
class DataStoreWidgetRepository(
    private val context: Context,
) : WidgetRepository {

    private object Keys {
        val HOSTED_WIDGETS = stringPreferencesKey("hosted_widgets_v1")
    }

    override val hostedWidgets: Flow<List<HostedWidget>> = context.widgetDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val serialized = prefs[Keys.HOSTED_WIDGETS]
            WidgetSerializer.deserialize(serialized)
        }

    override suspend fun addWidget(widget: HostedWidget) {
        context.widgetDataStore.edit { prefs ->
            val current = WidgetSerializer.deserialize(prefs[Keys.HOSTED_WIDGETS])
            val updated = current.filterNot { it.appWidgetId == widget.appWidgetId } + widget
            prefs[Keys.HOSTED_WIDGETS] = WidgetSerializer.serialize(updated)
        }
    }

    override suspend fun removeWidget(appWidgetId: Int) {
        context.widgetDataStore.edit { prefs ->
            val current = WidgetSerializer.deserialize(prefs[Keys.HOSTED_WIDGETS])
            val updated = current.filterNot { it.appWidgetId == appWidgetId }
            prefs[Keys.HOSTED_WIDGETS] = WidgetSerializer.serialize(updated)
        }
    }

    override suspend fun updateWidgetHeight(appWidgetId: Int, heightDp: Int) {
        context.widgetDataStore.edit { prefs ->
            val current = WidgetSerializer.deserialize(prefs[Keys.HOSTED_WIDGETS])
            val updated = current.map {
                if (it.appWidgetId == appWidgetId) it.copy(heightDp = heightDp) else it
            }
            prefs[Keys.HOSTED_WIDGETS] = WidgetSerializer.serialize(updated)
        }
    }

    override suspend fun clearAllWidgets() {
        context.widgetDataStore.edit { prefs ->
            prefs.remove(Keys.HOSTED_WIDGETS)
        }
    }
}
