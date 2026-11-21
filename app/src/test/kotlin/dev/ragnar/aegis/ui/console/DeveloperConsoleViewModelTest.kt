package dev.ragnar.aegis.ui.console

import dev.ragnar.aegis.data.diagnostics.AegisLogBuffer
import dev.ragnar.aegis.data.diagnostics.DiagnosticsRepository
import dev.ragnar.aegis.domain.diagnostics.DiagnosticsSnapshot
import dev.ragnar.aegis.domain.diagnostics.LauncherDiagnostics
import dev.ragnar.aegis.domain.diagnostics.LogEntry
import dev.ragnar.aegis.domain.diagnostics.LogLevel
import dev.ragnar.aegis.domain.diagnostics.PermissionStatus
import dev.ragnar.aegis.domain.diagnostics.SystemHardwareInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
 * Unit tests for [DeveloperConsoleViewModel] — F-014 (Developer Console).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DeveloperConsoleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        AegisLogBuffer.clear()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeDiagnosticsRepository(
        var snapshotData: DiagnosticsSnapshot = DiagnosticsSnapshot(
            launcherDiagnostics = LauncherDiagnostics(1000L, "00H 00M 01S", 10f, 100f, 10, 10, 2),
            systemInfo = SystemHardwareInfo("ModelX", "OEM", "15", 35, "ID1", "arm64", "2026-11-01"),
            permissions = listOf(PermissionStatus("perm", "ShortPerm", true)),
            recentLogs = listOf(
                LogEntry(1L, "00:00:01", LogLevel.INFO, "TAG", "Info msg"),
                LogEntry(2L, "00:00:02", LogLevel.ERROR, "TAG", "Error msg"),
            ),
        )
    ) : DiagnosticsRepository {
        var lastCopiedText: String? = null

        override fun getSnapshot(): DiagnosticsSnapshot = snapshotData

        override fun copyToClipboard(text: String): Boolean {
            lastCopiedText = text
            return true
        }
    }

    @Test
    fun `UT-014-06 initial state loads snapshot from repository`() = runTest(testDispatcher) {
        val repo = FakeDiagnosticsRepository()
        val vm = DeveloperConsoleViewModel(repo)

        assertEquals("ModelX", vm.uiState.value.snapshot.systemInfo.deviceModel)
        assertEquals(2, vm.uiState.value.displayedLogs.size)
        assertEquals(null, vm.uiState.value.selectedLogLevel)
        assertFalse(vm.uiState.value.isCopied)
    }

    @Test
    fun `UT-014-07 setLogLevelFilter filters displayedLogs by level`() = runTest(testDispatcher) {
        val repo = FakeDiagnosticsRepository()
        val vm = DeveloperConsoleViewModel(repo)

        vm.setLogLevelFilter(LogLevel.ERROR)
        assertEquals(1, vm.uiState.value.displayedLogs.size)
        assertEquals(LogLevel.ERROR, vm.uiState.value.displayedLogs.first().level)

        vm.setLogLevelFilter(LogLevel.INFO)
        assertEquals(1, vm.uiState.value.displayedLogs.size)
        assertEquals(LogLevel.INFO, vm.uiState.value.displayedLogs.first().level)

        vm.setLogLevelFilter(null)
        assertEquals(2, vm.uiState.value.displayedLogs.size)
    }

    @Test
    fun `UT-014-08 copyDiagnostics calls repository and sets isCopied flag`() = runTest(testDispatcher) {
        val repo = FakeDiagnosticsRepository()
        val vm = DeveloperConsoleViewModel(repo)

        vm.copyDiagnostics()

        assertTrue(vm.uiState.value.isCopied)
        assertTrue(repo.lastCopiedText?.contains("# AEGIS HUD // DIAGNOSTIC SNAPSHOT") == true)

        // Advance 2600ms to test auto-dismiss
        advanceTimeBy(2600)
        assertFalse(vm.uiState.value.isCopied)
    }

    @Test
    fun `UT-014-09 clearLogs clears buffer and refreshes snapshot`() = runTest(testDispatcher) {
        val repo = FakeDiagnosticsRepository()
        val vm = DeveloperConsoleViewModel(repo)

        AegisLogBuffer.info("TEST", "Some log")
        assertTrue(AegisLogBuffer.getEntries().size >= 2)

        vm.clearLogs()

        // After clearing, AegisLogBuffer resets and records 1 log ("Log buffer cleared")
        assertEquals(1, AegisLogBuffer.getEntries().size)
        assertEquals("Log buffer cleared", AegisLogBuffer.getEntries().first().message)
    }
}
