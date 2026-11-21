package dev.ragnar.aegis.domain.telemetry

/**
 * Charging status of the device battery.
 */
enum class ChargingStatus {
    Charging,
    Full,
    Discharging,
    NotCharging,
    Unknown,
}

/**
 * Power source connection type.
 */
enum class PlugType {
    Ac,
    Usb,
    Wireless,
    None,
    Unknown,
}

/**
 * Battery health reported by the hardware.
 */
enum class BatteryHealth {
    Good,
    Overheat,
    Dead,
    OverVoltage,
    UnspecifiedFailure,
    Cold,
    Unknown,
}

/**
 * Platform-neutral snapshot of the device battery telemetry.
 */
data class BatteryState(
    /** Current charge level as percentage (0–100), or null if unavailable. */
    val percentage: Int? = null,
    val chargingStatus: ChargingStatus = ChargingStatus.Unknown,
    val plugType: PlugType = PlugType.Unknown,
    /** Battery temperature in degrees Celsius (e.g. 28.5f), or null if unavailable. */
    val temperatureCelsius: Float? = null,
    val voltageMv: Int? = null,
    val health: BatteryHealth = BatteryHealth.Unknown,
)
