package dev.ragnar.aegis.ui.widget

import dev.ragnar.aegis.data.widget.WidgetRepository
import dev.ragnar.aegis.domain.widget.HostedWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetHostViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeWidgetRepository
    private lateinit var viewModel: WidgetHostViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWidgetRepository()
        viewModel = WidgetHostViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialUiState_hasEmptyHostedWidgetsAndClosedModals() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.hostedWidgets.isEmpty())
        assertFalse(state.isDeckVisible)
        assertFalse(state.isPickerVisible)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun toggleDeck_updatesDeckVisibility() = runTest {
        viewModel.toggleDeck(true)
        assertTrue(viewModel.uiState.value.isDeckVisible)

        viewModel.toggleDeck(false)
        assertFalse(viewModel.uiState.value.isDeckVisible)
    }

    @Test
    fun togglePicker_updatesPickerVisibility() = runTest {
        viewModel.togglePicker(true)
        assertTrue(viewModel.uiState.value.isPickerVisible)

        viewModel.togglePicker(false)
        assertFalse(viewModel.uiState.value.isPickerVisible)
    }

    @Test
    fun onWidgetAdded_persistsToRepositoryAndClosesPicker() = runTest {
        viewModel.togglePicker(true)

        val widget = HostedWidget(101, "com.example.clock", "com.example.clock.Widget", "Digital Clock", 180)
        viewModel.onWidgetAdded(widget)

        val state = viewModel.uiState.value
        assertEquals(1, state.hostedWidgets.size)
        assertEquals(widget, state.hostedWidgets.first())
        assertFalse(state.isPickerVisible)
        assertTrue(state.statusMessage?.contains("DIGITAL CLOCK") == true)
    }

    @Test
    fun onWidgetRemoved_removesFromRepositoryAndSetsStatus() = runTest {
        val widget = HostedWidget(102, "com.example.news", "com.example.news.Widget", "News Feed", 200)
        viewModel.onWidgetAdded(widget)

        viewModel.onWidgetRemoved(widget, null)

        val state = viewModel.uiState.value
        assertTrue(state.hostedWidgets.isEmpty())
        assertTrue(state.statusMessage?.contains("PURGED") == true)
    }

    @Test
    fun cycleWidgetHeight_updatesDimensionsInRepository() = runTest {
        val widget = HostedWidget(103, "com.example.notes", "com.example.notes.Widget", "Notes", 140)
        viewModel.onWidgetAdded(widget)

        viewModel.cycleWidgetHeight(103)

        val state = viewModel.uiState.value
        assertEquals(200, state.hostedWidgets.first().heightDp)
    }

    @Test
    fun searchFiltering_matchesOnLabelOrAppName() = runTest {
        viewModel.onSearchQueryChanged("Spotify")
        assertEquals("Spotify", viewModel.uiState.value.searchQuery)

        viewModel.clearSearch()
        assertEquals("", viewModel.uiState.value.searchQuery)
    }
}

private class FakeWidgetRepository : WidgetRepository {
    private val widgetsFlow = MutableStateFlow<List<HostedWidget>>(emptyList())

    override val hostedWidgets: Flow<List<HostedWidget>> = widgetsFlow

    override suspend fun addWidget(widget: HostedWidget) {
        widgetsFlow.value = widgetsFlow.value.filterNot { it.appWidgetId == widget.appWidgetId } + widget
    }

    override suspend fun removeWidget(appWidgetId: Int) {
        widgetsFlow.value = widgetsFlow.value.filterNot { it.appWidgetId == appWidgetId }
    }

    override suspend fun updateWidgetHeight(appWidgetId: Int, heightDp: Int) {
        widgetsFlow.value = widgetsFlow.value.map {
            if (it.appWidgetId == appWidgetId) it.copy(heightDp = heightDp) else it
        }
    }

    override suspend fun clearAllWidgets() {
        widgetsFlow.value = emptyList()
    }
}
