package dev.ragnar.aegis.ui.quickactions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.ragnar.aegis.data.quickactions.AndroidTorchController
import dev.ragnar.aegis.data.quickactions.TorchController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state snapshot for the Quick Actions panel.
 *
 * @param isTorchOn        Current torch state (ON / OFF).
 * @param isTorchAvailable Whether the device hardware supports torch control.
 * @param torchError       Non-null when the last torch toggle failed.
 */
data class QuickActionsUiState(
    val isTorchOn: Boolean = false,
    val isTorchAvailable: Boolean = false,
    val torchError: String? = null,
)

/**
 * ViewModel for the Quick Actions panel (F-009).
 *
 * Owns the torch on/off state and dispatches settings intents (settings intents
 * are fired as side-effects by the UI layer via callbacks, keeping the ViewModel
 * free of Compose and Context references).
 *
 * [torchController] is injected for unit testability.
 */
class QuickActionsViewModel(
    private val torchController: TorchController,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        QuickActionsUiState(
            isTorchAvailable = torchController.isAvailable,
        )
    )
    val uiState: StateFlow<QuickActionsUiState> = _uiState.asStateFlow()

    /**
     * Toggles the device flashlight ON or OFF.
     * Updates [QuickActionsUiState.isTorchOn] on success.
     * Sets [QuickActionsUiState.torchError] on failure without changing torch state.
     */
    fun toggleTorch() {
        val current = _uiState.value.isTorchOn
        val target = !current
        val success = torchController.setTorch(target)
        _uiState.update { state ->
            if (success) {
                state.copy(isTorchOn = target, torchError = null)
            } else {
                state.copy(torchError = "Torch unavailable")
            }
        }
    }

    /** Clears any displayed torch error. */
    fun clearTorchError() {
        _uiState.update { it.copy(torchError = null) }
    }
}

/** Manual factory wiring [AndroidTorchController] from application context. */
class QuickActionsViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val torchController: TorchController = AndroidTorchController(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(QuickActionsViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return QuickActionsViewModel(torchController) as T
    }
}
