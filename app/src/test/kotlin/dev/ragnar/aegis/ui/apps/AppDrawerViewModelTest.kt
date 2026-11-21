package dev.ragnar.aegis.ui.apps

import dev.ragnar.aegis.data.apps.AppRepository
import dev.ragnar.aegis.data.settings.PreferencesRepository
import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.domain.apps.AppSortOrder
import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import dev.ragnar.aegis.domain.settings.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
 * Unit tests for [AppDrawerViewModel] — F-004, F-005, F-011.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppDrawerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeAppRepository(
        initialApps: List<AppInfo> = emptyList(),
    ) : AppRepository {
        val appsFlow = MutableStateFlow(initialApps)
        var lastLaunched: AppInfo? = null

        override fun observeLaunchableApps(): Flow<List<AppInfo>> = appsFlow.asStateFlow()

        override fun launch(app: AppInfo): Result<Unit> {
            lastLaunched = app
            return Result.success(Unit)
        }
    }

    private class FakePreferencesRepository(
        initial: UserPreferences = UserPreferences(),
    ) : PreferencesRepository {
        val flow = MutableStateFlow(initial)
        override val userPreferences: Flow<UserPreferences> = flow.asStateFlow()

        override suspend fun setAccentColor(accentColor: AccentColor) {}
        override suspend fun setAnimationSpeed(speed: AnimationSpeed) {}
        override suspend fun setShowRadar(show: Boolean) {}
        override suspend fun setShowSystemStatus(show: Boolean) {}
        override suspend fun setShowTelemetry(show: Boolean) {}
        override suspend fun setShowQuickActions(show: Boolean) {}
        override suspend fun set24HourClock(is24Hour: Boolean) {}
        override suspend fun setShowClockSeconds(showSeconds: Boolean) {}

        override suspend fun toggleFavorite(componentName: String) {
            val cur = flow.value.favoriteApps
            flow.value = flow.value.copy(
                favoriteApps = if (cur.contains(componentName)) cur - componentName else cur + componentName,
            )
        }

        override suspend fun setFavorites(favorites: Set<String>) {
            flow.value = flow.value.copy(favoriteApps = favorites)
        }

        override suspend fun clearFavorites() {
            flow.value = flow.value.copy(favoriteApps = emptySet())
        }

        override suspend fun setAppSortOrder(order: AppSortOrder) {
            flow.value = flow.value.copy(appSortOrder = order)
        }

        override suspend fun setDrawerGridColumns(columns: Int) {
            flow.value = flow.value.copy(drawerGridColumns = columns.coerceIn(3, 5))
        }

        override suspend fun resetToDefaults() {
            flow.value = UserPreferences()
        }
    }

    private fun createApp(
        label: String,
        packageName: String = "dev.ragnar.$label",
        componentName: String = "dev.ragnar.$label/.Main",
    ) = AppInfo(label, packageName, componentName)

    @Test
    fun `initial state reflects apps and default preferences`() = runTest(testDispatcher) {
        val app1 = createApp("Browser")
        val app2 = createApp("Camera")
        val appRepo = FakeAppRepository(listOf(app2, app1))
        val prefRepo = FakePreferencesRepository()

        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(2, vm.uiState.value.apps.size)
        // Default AZ sorting
        assertEquals(listOf("Browser", "Camera"), vm.uiState.value.filteredApps.map { it.label })
        assertEquals(AppSortOrder.AZ, vm.uiState.value.sortOrder)
        assertEquals(4, vm.uiState.value.gridColumns)
        assertTrue(vm.uiState.value.pinnedApps.isEmpty())
    }

    @Test
    fun `search query filters the filteredApps list`() = runTest(testDispatcher) {
        val app1 = createApp("Browser")
        val app2 = createApp("Camera")
        val app3 = createApp("Calculator")
        val appRepo = FakeAppRepository(listOf(app1, app2, app3))
        val prefRepo = FakePreferencesRepository()

        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        vm.onSearchQueryChanged("cam")
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.filteredApps.size)
        assertEquals("Camera", vm.uiState.value.filteredApps.first().label)
        assertFalse(vm.uiState.value.isNoResults)

        vm.onSearchQueryChanged("xyz")
        advanceUntilIdle()
        assertTrue(vm.uiState.value.filteredApps.isEmpty())
        assertTrue(vm.uiState.value.isNoResults)

        vm.clearSearch()
        advanceUntilIdle()
        assertEquals(3, vm.uiState.value.filteredApps.size)
        assertFalse(vm.uiState.value.isNoResults)
    }

    @Test
    fun `toggleFavorite toggles component and updates pinnedApps`() = runTest(testDispatcher) {
        val app1 = createApp("Browser")
        val app2 = createApp("Camera")
        val appRepo = FakeAppRepository(listOf(app1, app2))
        val prefRepo = FakePreferencesRepository()

        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        vm.toggleFavorite(app2)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.favoriteComponents.contains(app2.componentName))
        assertEquals(1, vm.uiState.value.pinnedApps.size)
        assertEquals("Camera", vm.uiState.value.pinnedApps.first().label)

        vm.toggleFavorite(app2)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.favoriteComponents.contains(app2.componentName))
        assertTrue(vm.uiState.value.pinnedApps.isEmpty())
    }

    @Test
    fun `cycleSortOrder cycles through all sort orders in sequence`() = runTest(testDispatcher) {
        val appRepo = FakeAppRepository()
        val prefRepo = FakePreferencesRepository()
        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        assertEquals(AppSortOrder.AZ, vm.uiState.value.sortOrder)

        vm.cycleSortOrder()
        advanceUntilIdle()
        assertEquals(AppSortOrder.ZA, vm.uiState.value.sortOrder)

        vm.cycleSortOrder()
        advanceUntilIdle()
        assertEquals(AppSortOrder.FavoritesFirst, vm.uiState.value.sortOrder)

        vm.cycleSortOrder()
        advanceUntilIdle()
        assertEquals(AppSortOrder.InstallDate, vm.uiState.value.sortOrder)

        vm.cycleSortOrder()
        advanceUntilIdle()
        assertEquals(AppSortOrder.AZ, vm.uiState.value.sortOrder)
    }

    @Test
    fun `cycleGridColumns cycles through 3, 4, 5`() = runTest(testDispatcher) {
        val appRepo = FakeAppRepository()
        val prefRepo = FakePreferencesRepository()
        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        assertEquals(4, vm.uiState.value.gridColumns)

        vm.cycleGridColumns()
        advanceUntilIdle()
        assertEquals(5, vm.uiState.value.gridColumns)

        vm.cycleGridColumns()
        advanceUntilIdle()
        assertEquals(3, vm.uiState.value.gridColumns)

        vm.cycleGridColumns()
        advanceUntilIdle()
        assertEquals(4, vm.uiState.value.gridColumns)
    }

    @Test
    fun `launchTopResult launches first filtered app`() = runTest(testDispatcher) {
        val app1 = createApp("Browser")
        val app2 = createApp("Camera")
        val appRepo = FakeAppRepository(listOf(app1, app2))
        val prefRepo = FakePreferencesRepository()
        val vm = AppDrawerViewModel(appRepo, prefRepo, SharingStarted.Eagerly)
        advanceUntilIdle()

        vm.onSearchQueryChanged("Cam")
        advanceUntilIdle()
        val err = vm.launchTopResult()

        assertEquals(null, err)
        assertEquals("Camera", appRepo.lastLaunched?.label)
    }
}
