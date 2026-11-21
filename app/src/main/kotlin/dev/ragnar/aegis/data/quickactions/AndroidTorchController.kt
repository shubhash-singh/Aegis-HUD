package dev.ragnar.aegis.data.quickactions

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

/**
 * Android implementation of [TorchController] using [CameraManager.setTorchMode].
 *
 * Checks for the presence of a camera unit with a flash lamp before any operation.
 * Silently returns `false` when the device has no controllable torch hardware.
 *
 * Requires no special permissions beyond the implicit camera2 API access.
 */
class AndroidTorchController(context: Context) : TorchController {

    private val cameraManager: CameraManager? =
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    /**
     * Returns the camera ID of the first back-facing camera that has a flash unit, or null.
     */
    private val torchCameraId: String? by lazy {
        cameraManager?.cameraIdList?.firstOrNull { id ->
            try {
                val chars = cameraManager.getCameraCharacteristics(id)
                val facing = chars.get(CameraCharacteristics.LENS_FACING)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                facing == CameraCharacteristics.LENS_FACING_BACK && hasFlash
            } catch (_: Exception) {
                false
            }
        }
    }

    override val isAvailable: Boolean
        get() = cameraManager != null && torchCameraId != null

    override fun setTorch(enable: Boolean): Boolean {
        val cameraId = torchCameraId ?: return false
        return try {
            cameraManager?.setTorchMode(cameraId, enable)
            true
        } catch (_: Exception) {
            false
        }
    }
}
