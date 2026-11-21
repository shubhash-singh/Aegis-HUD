package dev.ragnar.aegis.ui.console

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.diagnostics.AegisLogBuffer
import dev.ragnar.aegis.data.diagnostics.AndroidDiagnosticsRepository
import dev.ragnar.aegis.data.diagnostics.DiagnosticsRepository
import dev.ragnar.aegis.domain.diagnostics.DiagnosticsSnapshot
import dev.ragnar.aegis.domain.diagnostics.LauncherDiagnostics
import dev.ragnar.aegis.domain.diagnostics.LogEntry
import dev.ragnar.aegis.domain.diagnostics.LogLevel
import dev.ragnar.aegis.domain.diagnostics.SystemHardwareInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI state for the Developer Console. */
data class DeveloperConsoleUiState(
    val snapshot: DiagnosticsSnapshot = DiagnosticsSnapshot(
        launcherDiagnostics = LauncherDiagnostics(0L, "00H 00M 00S", 0f, 0f, 0, 0, 0),
        systemInfo = SystemHardwareInfo("---", "---", "---", 0, "---", "---", "---"),
        permissions = emptyList(),
        recentLogs = emptyList(),
    ),
    val selectedLogLevel: LogLevel? = null,
    val displayedLogs: List<LogEntry> = emptyList(),
    val isCopied: Boolean = false,
)

/**
 * ViewModel managing Developer Console diagnostics state (F-014).
 */
class DeveloperConsoleViewModel(
    private val diagnosticsRepository: DiagnosticsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeveloperConsoleUiState())
    val uiState: StateFlow<DeveloperConsoleUiState> = _uiState.asStateFlow()

    init {
        refreshSnapshot()
    }

    fun refreshSnapshot() {
        val snapshot = diagnosticsRepository.getSnapshot()
        val filter = _uiState.value.selectedLogLevel
        val filteredLogs = if (filter == null) {
            snapshot.recentLogs
        } else {
            snapshot.recentLogs.filter { it.level == filter }
        }

        _uiState.update { current ->
            current.copy(
                snapshot = snapshot,
                displayedLogs = filteredLogs,
            )
        }
    }

    fun setLogLevelFilter(level: LogLevel?) {
        _uiState.update { current ->
            val logs = if (level == null) {
                current.snapshot.recentLogs
            } else {
                current.snapshot.recentLogs.filter { it.level == level }
            }
            current.copy(
                selectedLogLevel = level,
                displayedLogs = logs,
            )
        }
    }

    fun clearLogs() {
        AegisLogBuffer.clear()
        refreshSnapshot()
    }

    fun copyDiagnostics() {
        val markdown = _uiState.value.snapshot.toFormattedMarkdown()
        val success = diagnosticsRepository.copyToClipboard(markdown)
        if (success) {
            _uiState.update { it.copy(isCopied = true) }
            viewModelScope.launch {
                delay(2500)
                _uiState.update { it.copy(isCopied = false) }
            }
        }
    }

    fun dismissCopyConfirmation() {
        _uiState.update { it.copy(isCopied = false) }
    }
}

/** Manual factory for [DeveloperConsoleViewModel]. */
class DeveloperConsoleViewModelFactory(
    context: Context,
    private val diagnosticsRepository: DiagnosticsRepository? = null,
) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(DeveloperConsoleViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        val repo = diagnosticsRepository ?: AndroidDiagnosticsRepository(appContext)
        return DeveloperConsoleViewModel(repo) as T
    }
}
