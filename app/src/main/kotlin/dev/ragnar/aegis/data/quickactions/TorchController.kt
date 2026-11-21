package dev.ragnar.aegis.data.quickactions

/**
 * Platform-neutral interface for controlling the device flashlight.
 *
 * Abstracts [android.hardware.camera2.CameraManager.setTorchMode] behind a testable facade.
 * All implementations must guard against hardware that lacks a camera flash (F-009-P2).
 *
 * No Android APIs surface in this interface — it is fully testable on JVM.
 */
interface TorchController {

    /** Whether the device has a torch unit that can be controlled. */
    val isAvailable: Boolean

    /**
     * Enable or disable the torch.
     *
     * @param enable `true` to switch on, `false` to switch off.
     * @return `true` if the operation succeeded; `false` on hardware error or unavailability.
     */
    fun setTorch(enable: Boolean): Boolean
}
