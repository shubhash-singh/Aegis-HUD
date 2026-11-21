package dev.ragnar.aegis.ui.settings

import dev.ragnar.aegis.data.settings.PreferencesRepository
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SettingsViewModel] — F-010 (Customization).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

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

        override suspend fun resetToDefaults() {
            _flow.value = UserPreferences()
        }
    }

    @Test
    fun `initial state reflects repository preferences`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        assertEquals(AccentColor.Cyan, vm.uiState.value.accentColor)
        assertEquals(AnimationSpeed.Normal, vm.uiState.value.animationSpeed)
        assertTrue(vm.uiState.value.showRadar)
    }

    @Test
    fun `setAccentColor updates state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setAccentColor(AccentColor.Amber)
        assertEquals(AccentColor.Amber, vm.uiState.value.accentColor)
    }

    @Test
    fun `setAnimationSpeed updates state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setAnimationSpeed(AnimationSpeed.Fast)
        assertEquals(AnimationSpeed.Fast, vm.uiState.value.animationSpeed)
    }

    @Test
    fun `panel visibility toggles update state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.setShowRadar(false)
        assertFalse(vm.uiState.value.showRadar)

        vm.setShowSystemStatus(false)
        assertFalse(vm.uiState.value.showSystemStatus)

        vm.setShowTelemetry(false)
        assertFalse(vm.uiState.value.showTelemetry)

        vm.setShowQuickActions(false)
        assertFalse(vm.uiState.value.showQuickActions)
    }

    @Test
    fun `clock settings toggles update state`() = runTest {
        val repo = FakePreferencesRepository()
        val vm = SettingsViewModel(repo)

        vm.set24HourClock(false)
        assertFalse(vm.uiState.value.is24HourClock)

        vm.setShowClockSeconds(false)
        assertFalse(vm.uiState.value.showClockSeconds)
    }

    @Test
    fun `resetToDefaults restores initial default preferences`() = runTest {
        val initial = UserPreferences(
            accentColor = AccentColor.Crimson,
            showRadar = false,
            is24HourClock = false,
        )
        val repo = FakePreferencesRepository(initial)
        val vm = SettingsViewModel(repo)

        assertEquals(AccentColor.Crimson, vm.uiState.value.accentColor)
        assertFalse(vm.uiState.value.showRadar)

        vm.resetToDefaults()

        assertEquals(AccentColor.Cyan, vm.uiState.value.accentColor)
        assertTrue(vm.uiState.value.showRadar)
        assertTrue(vm.uiState.value.is24HourClock)
    }
}
