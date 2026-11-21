package dev.ragnar.aegis.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.widget.WidgetRepository
import dev.ragnar.aegis.domain.widget.HostedWidget
import dev.ragnar.aegis.domain.widget.WidgetHostUiState
import dev.ragnar.aegis.domain.widget.WidgetProviderSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the state of hosted widgets and the widget provider registry.
 */
class WidgetHostViewModel(
    private val widgetRepository: WidgetRepository,
) : ViewModel() {

    private data class InternalState(
        val searchQuery: String = "",
        val isDeckVisible: Boolean = false,
        val isPickerVisible: Boolean = false,
        val statusMessage: String? = null,
    )

    private val _internalState = MutableStateFlow(InternalState())
    private val _availableProviders = MutableStateFlow<List<WidgetProviderSummary>>(emptyList())

    val uiState: StateFlow<WidgetHostUiState> = combine(
        widgetRepository.hostedWidgets,
        _availableProviders,
        _internalState,
    ) { hosted, providers, internal ->
        val query = internal.searchQuery
        val filtered = if (query.isBlank()) {
            providers
        } else {
            providers.filter {
                it.label.contains(query, ignoreCase = true) ||
                    it.appName.contains(query, ignoreCase = true) ||
                    it.providerPackage.contains(query, ignoreCase = true)
            }
        }
        WidgetHostUiState(
            hostedWidgets = hosted,
            availableProviders = providers,
            filteredProviders = filtered,
            searchQuery = query,
            isDeckVisible = internal.isDeckVisible,
            isPickerVisible = internal.isPickerVisible,
            statusMessage = internal.statusMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = WidgetHostUiState(),
    )

    fun loadAvailableProviders(context: Context) {
        viewModelScope.launch {
            runCatching {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val pm = context.packageManager
                val providers = appWidgetManager.installedProviders.map { info ->
                    val appName = runCatching {
                        val appInfo = pm.getApplicationInfo(info.provider.packageName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    }.getOrDefault(info.provider.packageName)

                    WidgetProviderSummary(
                        providerPackage = info.provider.packageName,
                        providerClass = info.provider.className,
                        label = info.loadLabel(pm),
                        appName = appName,
                        minWidthDp = info.minWidth,
                        minHeightDp = info.minHeight,
                        configureClass = info.configure?.className,
                    )
                }.sortedWith(compareBy({ it.appName }, { it.label }))

                _availableProviders.value = providers
            }.onFailure { error ->
                _internalState.update { it.copy(statusMessage = "FAILED TO QUERY WIDGET PROVIDERS: ${error.localizedMessage}") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _internalState.update { it.copy(searchQuery = query) }
    }

    fun clearSearch() {
        _internalState.update { it.copy(searchQuery = "") }
    }

    fun toggleDeck(visible: Boolean) {
        _internalState.update {
            it.copy(
                isDeckVisible = visible,
                isPickerVisible = if (visible) false else it.isPickerVisible,
            )
        }
    }

    fun togglePicker(visible: Boolean) {
        _internalState.update { it.copy(isPickerVisible = visible) }
    }

    fun onWidgetAdded(widget: HostedWidget) {
        viewModelScope.launch {
            widgetRepository.addWidget(widget)
            _internalState.update {
                it.copy(
                    isPickerVisible = false,
                    statusMessage = "MODULE ${widget.label.uppercase()} DOCKED [ID: #${widget.appWidgetId}]",
                )
            }
        }
    }

    fun onWidgetRemoved(widget: HostedWidget, controller: WidgetHostController?) {
        viewModelScope.launch {
            controller?.deleteWidget(widget.appWidgetId)
            widgetRepository.removeWidget(widget.appWidgetId)
            _internalState.update {
                it.copy(statusMessage = "MODULE ${widget.label.uppercase()} PURGED")
            }
        }
    }

    fun cycleWidgetHeight(appWidgetId: Int) {
        viewModelScope.launch {
            val current = uiState.value.hostedWidgets.firstOrNull { it.appWidgetId == appWidgetId } ?: return@launch
            val next = HostedWidget.nextHeight(current.heightDp)
            widgetRepository.updateWidgetHeight(appWidgetId, next)
        }
    }

    fun clearStatusMessage() {
        _internalState.update { it.copy(statusMessage = null) }
    }
}

class WidgetHostViewModelFactory(
    private val widgetRepository: WidgetRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WidgetHostViewModel(widgetRepository) as T
    }
}
