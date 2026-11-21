package dev.ragnar.aegis.ui.settings

import dev.ragnar.aegis.data.settings.PreferencesRepository
import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GestureSettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakePreferencesRepository(
        initial: UserPreferences = UserPreferences()
    ) : PreferencesRepository {
        private val _flow = MutableStateFlow(initial)
        override val userPreferences: Flow<UserPreferences> = _flow.asStateFlow()

        override suspend fun setAccentColor(accentColor: AccentColor) {
            _flow.value = _flow.value.copy(accentColor = accentColor)
        }

        override suspend fun setAnimationSpeed(speed: AnimationSpeed) {
            _flow.value = _flow.value.copy(animationSpeed = speed)
        }

        override suspend fun setShowRadar(show: Boolean) {
            _flow.value = _flow.value.copy(showRadar = show)
        }

        override suspend fun setShowSystemStatus(show: Boolean) {
            _flow.value = _flow.value.copy(showSystemStatus = show)
        }

        override suspend fun setShowTelemetry(show: Boolean) {
            _flow.value = _flow.value.copy(showTelemetry = show)
        }

        override suspend fun setShowQuickActions(show: Boolean) {
            _flow.value = _flow.value.copy(showQuickActions = show)
        }

        override suspend fun set24HourClock(is24Hour: Boolean) {
            _flow.value = _flow.value.copy(is24HourClock = is24Hour)
        }

        override suspend fun setShowClockSeconds(showSeconds: Boolean) {
            _flow.value = _flow.value.copy(showClockSeconds = showSeconds)
        }

        override suspend fun toggleFavorite(componentName: String) {}

        override suspend fun setFavorites(favorites: Set<String>) {
            _flow.value = _flow.value.copy(favoriteApps = favorites)
        }

        override suspend fun clearFavorites() {
            _flow.value = _flow.value.copy(favoriteApps = emptySet())
        }

        override suspend fun setAppSortOrder(order: AppSortOrder) {
            _flow.value = _flow.value.copy(appSortOrder = order)
        }

        override suspend fun setDrawerGridColumns(columns: Int) {
            _flow.value = _flow.value.copy(drawerGridColumns = columns.coerceIn(3, 5))
        }

        override suspend fun setContextAwareHudEnabled(enabled: Boolean) {
            _flow.value = _flow.value.copy(contextAwareHudEnabled = enabled)
        }

        override suspend fun toggleContextRule(ruleId: String) {}

        override suspend fun setEnabledContextRules(ruleIds: Set<String>) {
            _flow.value = _flow.value.copy(enabledContextRules = ruleIds)
        }

        override suspend fun setSwipeUpEnabled(enabled: Boolean) {
            _flow.value = _flow.value.copy(swipeUpEnabled = enabled)
        }

        override suspend fun setSwipeDownEnabled(enabled: Boolean) {
            _flow.value = _flow.value.copy(swipeDownEnabled = enabled)
        }

        override suspend fun setLongPressEnabled(enabled: Boolean) {
            _flow.value = _flow.value.copy(longPressEnabled = enabled)
        }

        override suspend fun resetToDefaults() {
            _flow.value = UserPreferences()
        }
    }

    @Test
    fun `default preferences enable all gestures`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        assertTrue(vm.uiState.value.swipeUpEnabled)
        assertTrue(vm.uiState.value.swipeDownEnabled)
        assertTrue(vm.uiState.value.longPressEnabled)
    }

    @Test
    fun `setSwipeUpEnabled updates state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setSwipeUpEnabled(false)
        assertFalse(vm.uiState.value.swipeUpEnabled)

        vm.setSwipeUpEnabled(true)
        assertTrue(vm.uiState.value.swipeUpEnabled)
    }

    @Test
    fun `setSwipeDownEnabled updates state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setSwipeDownEnabled(false)
        assertFalse(vm.uiState.value.swipeDownEnabled)

        vm.setSwipeDownEnabled(true)
        assertTrue(vm.uiState.value.swipeDownEnabled)
    }

    @Test
    fun `setLongPressEnabled updates state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setLongPressEnabled(false)
        assertFalse(vm.uiState.value.longPressEnabled)

        vm.setLongPressEnabled(true)
        assertTrue(vm.uiState.value.longPressEnabled)
    }

    @Test
    fun `resetToDefaults re-enables all gestures`() = runTest {
        val custom = UserPreferences(
            swipeUpEnabled = false,
            swipeDownEnabled = false,
            longPressEnabled = false,
        )
        val repo = FakePreferencesRepository(custom)
        val vm = SettingsViewModel(repo)

        assertFalse(vm.uiState.value.swipeUpEnabled)
        assertFalse(vm.uiState.value.swipeDownEnabled)
        assertFalse(vm.uiState.value.longPressEnabled)

        vm.resetToDefaults()

        assertTrue(vm.uiState.value.swipeUpEnabled)
        assertTrue(vm.uiState.value.swipeDownEnabled)
        assertTrue(vm.uiState.value.longPressEnabled)
    }
}
