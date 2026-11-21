package dev.ragnar.aegis.ui.quickactions

import dev.ragnar.aegis.data.quickactions.TorchController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [QuickActionsViewModel] — F-009 (Quick Actions).
 *
 * Uses a pure fake [TorchController] so no Android framework APIs are invoked.
 */
class QuickActionsViewModelTest {

    // --- Fake TorchController ---

    private class FakeTorchController(
        override val isAvailable: Boolean = true,
        private val shouldSucceed: Boolean = true,
    ) : TorchController {
        var lastSetValue: Boolean? = null

        override fun setTorch(enable: Boolean): Boolean {
            lastSetValue = enable
            return shouldSucceed
        }
    }

    // --- UT-009-01: Initial state ---

    @Test
    fun `UT-009-01 initial state has torch off and no error`() {
        val fake = FakeTorchController(isAvailable = true)
        val vm = QuickActionsViewModel(fake)

        val state = vm.uiState.value
        assertFalse(state.isTorchOn)
        assertTrue(state.isTorchAvailable)
        assertNull(state.torchError)
    }

    @Test
    fun `initial state reflects torch unavailability`() {
        val fake = FakeTorchController(isAvailable = false)
        val vm = QuickActionsViewModel(fake)

        assertFalse(vm.uiState.value.isTorchAvailable)
    }

    // --- UT-009-02: Toggle torch ---

    @Test
    fun `UT-009-02 toggleTorch turns torch on when it was off`() {
        val fake = FakeTorchController(isAvailable = true, shouldSucceed = true)
        val vm = QuickActionsViewModel(fake)

        vm.toggleTorch()

        assertTrue(vm.uiState.value.isTorchOn)
        assertEquals(true, fake.lastSetValue)
        assertNull(vm.uiState.value.torchError)
    }

    @Test
    fun `toggleTorch turns torch off when it was on`() {
        val fake = FakeTorchController(isAvailable = true, shouldSucceed = true)
        val vm = QuickActionsViewModel(fake)

        vm.toggleTorch() // ON
        vm.toggleTorch() // OFF

        assertFalse(vm.uiState.value.isTorchOn)
        assertEquals(false, fake.lastSetValue)
    }

    // --- UT-009-03: Torch error ---

    @Test
    fun `UT-009-03 toggleTorch sets torchError when hardware fails`() {
        val fake = FakeTorchController(isAvailable = true, shouldSucceed = false)
        val vm = QuickActionsViewModel(fake)

        vm.toggleTorch()

        // Torch should remain OFF since operation failed
        assertFalse(vm.uiState.value.isTorchOn)
        assertNotNull(vm.uiState.value.torchError)
    }

    @Test
    fun `clearTorchError removes the error message`() {
        val fake = FakeTorchController(isAvailable = true, shouldSucceed = false)
        val vm = QuickActionsViewModel(fake)

        vm.toggleTorch()
        assertNotNull(vm.uiState.value.torchError)

        vm.clearTorchError()
        assertNull(vm.uiState.value.torchError)
    }
}
