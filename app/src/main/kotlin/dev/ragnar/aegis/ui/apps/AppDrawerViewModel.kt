package dev.ragnar.aegis.ui.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.apps.AndroidAppRepository
import dev.ragnar.aegis.data.apps.AppRepository
import dev.ragnar.aegis.domain.apps.AppInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** State rendered by the app drawer and quick-launch row. */
data class AppDrawerUiState(
    val apps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

/** Owns the app-discovery subscription for the visible launcher UI. */
class AppDrawerViewModel(
    private val appRepository: AppRepository,
) : ViewModel() {

    val uiState = appRepository.observeLaunchableApps()
        .map { apps -> AppDrawerUiState(apps = apps, isLoading = false) }
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

    /** Starts the selected target and exposes a recoverable failure in the drawer UI. */
    fun launch(app: AppInfo): String? = appRepository.launch(app)
        .exceptionOrNull()
        ?.let { "UNABLE TO OPEN ${app.label.uppercase()}" }
}

/** Manual DI factory; Phase 4 deliberately does not introduce a DI framework. */
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
