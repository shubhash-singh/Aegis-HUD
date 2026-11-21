package dev.ragnar.aegis.ui.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.apps.AndroidAppRepository
import dev.ragnar.aegis.data.apps.AppListNormalizer
import dev.ragnar.aegis.data.apps.AppRepository
import dev.ragnar.aegis.data.settings.DataStorePreferencesRepository
import dev.ragnar.aegis.data.settings.PreferencesRepository
import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.domain.apps.AppSortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

/** State rendered by the app drawer. */
data class AppDrawerUiState(
    /** All launchable apps (unfiltered, normalized). */
    val apps: List<AppInfo> = emptyList(),
    /** Apps visible in the drawer after applying sort order and search query. */
    val filteredApps: List<AppInfo> = emptyList(),
    /** Pinned / favorite apps for quick access. */
    val pinnedApps: List<AppInfo> = emptyList(),
    /** Set of pinned activity component names. */
    val favoriteComponents: Set<String> = emptySet(),
    /** Current sort order. */
    val sortOrder: AppSortOrder = AppSortOrder.AZ,
    /** Configurable column count (3, 4, or 5). */
    val gridColumns: Int = 4,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    /** Current search query text. */
    val searchQuery: String = "",
    /** True when a non-blank query produced zero results (distinct from "no apps"). */
    val isNoResults: Boolean = false,
)

/** Owns the app-discovery subscription, sorting, pinning, and layout settings for the launcher UI. */
class AppDrawerViewModel(
    private val appRepository: AppRepository,
    private val preferencesRepository: PreferencesRepository,
    started: SharingStarted = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState = combine(
        appRepository.observeLaunchableApps(),
        preferencesRepository.userPreferences,
        _searchQuery,
    ) { apps, prefs, query ->
        val favorites = prefs.favoriteApps
        val sortedApps = AppListNormalizer.sort(apps, prefs.appSortOrder, favorites)
        val filtered = AppListNormalizer.filter(sortedApps, query)
        val pinned = apps.filter { it.componentName in favorites }
            .sortedBy { it.label.lowercase(Locale.ROOT) }

        AppDrawerUiState(
            apps = apps,
            filteredApps = filtered,
            pinnedApps = pinned,
            favoriteComponents = favorites,
            sortOrder = prefs.appSortOrder,
            gridColumns = prefs.drawerGridColumns,
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
            started = started,
            initialValue = AppDrawerUiState(),
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleFavorite(app: AppInfo) {
        toggleFavorite(app.componentName)
    }

    fun toggleFavorite(componentName: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(componentName)
        }
    }

    fun cycleSortOrder() {
        val current = uiState.value.sortOrder
        val all = AppSortOrder.entries
        val nextIndex = (all.indexOf(current) + 1) % all.size
        setSortOrder(all[nextIndex])
    }

    fun setSortOrder(order: AppSortOrder) {
        viewModelScope.launch {
            preferencesRepository.setAppSortOrder(order)
        }
    }

    fun cycleGridColumns() {
        val current = uiState.value.gridColumns
        val next = when (current) {
            3 -> 4
            4 -> 5
            else -> 3
        }
        setGridColumns(next)
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch {
            preferencesRepository.setDrawerGridColumns(columns)
        }
    }

    fun launch(app: AppInfo): String? = appRepository.launch(app)
        .exceptionOrNull()
        ?.let { "UNABLE TO OPEN ${app.label.uppercase()}" }

    fun launchTopResult(): String? {
        val topApp = uiState.value.filteredApps.firstOrNull() ?: return null
        return launch(topApp)
    }
}

/** Manual DI factory; provides AppRepository and PreferencesRepository. */
class AppDrawerViewModelFactory(
    context: Context,
    private val appRepository: AppRepository? = null,
    private val preferencesRepository: PreferencesRepository? = null,
) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(AppDrawerViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        val appRepo = appRepository ?: AndroidAppRepository(appContext)
        val prefRepo = preferencesRepository ?: DataStorePreferencesRepository(appContext)
        return AppDrawerViewModel(appRepo, prefRepo) as T
    }
}
