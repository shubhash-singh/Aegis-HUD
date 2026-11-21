package dev.ragnar.aegis.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.settings.DataStorePreferencesRepository
import dev.ragnar.aegis.data.settings.PreferencesRepository
import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing user customization and preferences state (F-010, F-011).
 */
class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<UserPreferences> = preferencesRepository.userPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserPreferences(),
    )

    fun setOperatorName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setOperatorName(name)
        }
    }

    fun setAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            preferencesRepository.setAccentColor(accentColor)
        }
    }

    fun setAnimationSpeed(speed: AnimationSpeed) {
        viewModelScope.launch {
            preferencesRepository.setAnimationSpeed(speed)
        }
    }

    fun setShowRadar(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowRadar(show)
        }
    }

    fun setShowSystemStatus(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowSystemStatus(show)
        }
    }

    fun setShowTelemetry(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowTelemetry(show)
        }
    }

    fun setShowQuickActions(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowQuickActions(show)
        }
    }

    fun set24HourClock(is24Hour: Boolean) {
        viewModelScope.launch {
            preferencesRepository.set24HourClock(is24Hour)
        }
    }

    fun setShowClockSeconds(showSeconds: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowClockSeconds(showSeconds)
        }
    }

    fun setAppSortOrder(order: AppSortOrder) {
        viewModelScope.launch {
            preferencesRepository.setAppSortOrder(order)
        }
    }

    fun setDrawerGridColumns(columns: Int) {
        viewModelScope.launch {
            preferencesRepository.setDrawerGridColumns(columns)
        }
    }

    fun setContextAwareHudEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setContextAwareHudEnabled(enabled)
        }
    }

    fun toggleContextRule(ruleId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleContextRule(ruleId)
        }
    }

    fun setSwipeUpEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSwipeUpEnabled(enabled)
        }
    }

    fun setSwipeDownEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSwipeDownEnabled(enabled)
        }
    }

    fun setLongPressEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setLongPressEnabled(enabled)
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            preferencesRepository.clearFavorites()
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            preferencesRepository.resetToDefaults()
        }
    }
}

/** Manual ViewModelProvider.Factory for [SettingsViewModel]. */
class SettingsViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val repository = DataStorePreferencesRepository(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return SettingsViewModel(repository) as T
    }
}
