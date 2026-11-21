package dev.ragnar.aegis.ui.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.apps.AndroidAppRepository
import dev.ragnar.aegis.data.apps.AppListNormalizer
import dev.ragnar.aegis.data.apps.AppRepository
import dev.ragnar.aegis.domain.apps.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** State rendered by the app drawer and quick-launch row. */
data class AppDrawerUiState(
    /** All launchable apps (unfiltered). Used by Quick Launch row. */
    val apps: List<AppInfo> = emptyList(),
    /** Apps visible in the drawer after applying the search query. */
    val filteredApps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    /** Current search query text. */
    val searchQuery: String = "",
    /** True when a non-blank query produced zero results (distinct from "no apps"). */
    val isNoResults: Boolean = false,
)

/** Owns the app-discovery subscription for the visible launcher UI. */
class AppDrawerViewModel(
    private val appRepository: AppRepository,
) : ViewModel() {

    /** Backing search query state. */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState = combine(
        appRepository.observeLaunchableApps(),
        _searchQuery,
    ) { apps, query ->
        val filtered = AppListNormalizer.filter(apps, query)
        AppDrawerUiState(
            apps = apps,
            filteredApps = filtered,
            isLoading = false,
            searchQuery = query,
            isNoResults = query.isNotBlank() && filtered.isEmpty(),
        )
    }
        .catch {
            emit(
                AppDrawerUiState(
                    isLoading = false,
                    errorMessage = "APPLICATION LIST UNAVAILABLE",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = AppDrawerUiState(),
        )

    /** Updates the search query; the filtered app list recomputes automatically. */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /** Resets the search query to empty. */
    fun clearSearch() {
        _searchQuery.value = ""
    }

    /** Starts the selected target and exposes a recoverable failure in the drawer UI. */
    fun launch(app: AppInfo): String? = appRepository.launch(app)
        .exceptionOrNull()
        ?.let { "UNABLE TO OPEN ${app.label.uppercase()}" }

    /** Launches the first app in the filtered list (called on IME search action). */
    fun launchTopResult(): String? {
        val topApp = uiState.value.filteredApps.firstOrNull() ?: return null
        return launch(topApp)
    }
}

/** Manual DI factory; Phase 5 deliberately does not introduce a DI framework. */
class AppDrawerViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(AppDrawerViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return AppDrawerViewModel(AndroidAppRepository(appContext)) as T
    }
}
