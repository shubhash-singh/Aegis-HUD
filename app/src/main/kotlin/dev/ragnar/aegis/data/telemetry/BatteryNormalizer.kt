package dev.ragnar.aegis.data.telemetry

import android.os.BatteryManager
import dev.ragnar.aegis.domain.telemetry.BatteryHealth
import dev.ragnar.aegis.domain.telemetry.BatteryState
import dev.ragnar.aegis.domain.telemetry.ChargingStatus
import dev.ragnar.aegis.domain.telemetry.PlugType
import kotlin.math.roundToInt

/**
 * Pure mapping and normalization logic for battery data.
 *
 * Decoupled from Android runtime context so all parsing, rounding, and safe default
 * handling can be validated by JVM unit tests.
 */
object BatteryNormalizer {

    /**
     * Normalizes raw extras from `Intent.ACTION_BATTERY_CHANGED`.
     *
     * @param level Raw `EXTRA_LEVEL`
     * @param scale Raw `EXTRA_SCALE`
     * @param status Raw `EXTRA_STATUS`
     * @param plugged Raw `EXTRA_PLUGGED`
     * @param temperatureTenths Raw `EXTRA_TEMPERATURE` in tenths of a degree Celsius
     * @param voltageMv Raw `EXTRA_VOLTAGE` in millivolts
     * @param health Raw `EXTRA_HEALTH`
     */
    fun normalize(
        level: Int?,
        scale: Int?,
        status: Int?,
        plugged: Int?,
        temperatureTenths: Int?,
        voltageMv: Int?,
        health: Int?,
    ): BatteryState {
        val percentage = calculatePercentage(level, scale)
        val chargingStatus = mapChargingStatus(status)
        val plugType = mapPlugType(plugged)
        val tempCelsius = temperatureTenths?.let { it / 10.0f }
        val batteryHealth = mapHealth(health)

        return BatteryState(
            percentage = percentage,
            chargingStatus = chargingStatus,
            plugType = plugType,
            temperatureCelsius = tempCelsius,
            voltageMv = voltageMv?.takeIf { it > 0 },
            health = batteryHealth,
        )
    }

    private fun calculatePercentage(level: Int?, scale: Int?): Int? {
        if (level == null || scale == null || scale <= 0) return null
        val percent = (level.toFloat() / scale.toFloat()) * 100f
        return percent.roundToInt().coerceIn(0, 100)
    }

    private fun mapChargingStatus(status: Int?): ChargingStatus = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> ChargingStatus.Charging
        BatteryManager.BATTERY_STATUS_FULL -> ChargingStatus.Full
        BatteryManager.BATTERY_STATUS_DISCHARGING -> ChargingStatus.Discharging
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> ChargingStatus.NotCharging
        else -> ChargingStatus.Unknown
    }

    private fun mapPlugType(plugged: Int?): PlugType = when (plugged) {
        0 -> PlugType.None
        BatteryManager.BATTERY_PLUGGED_AC -> PlugType.Ac
        BatteryManager.BATTERY_PLUGGED_USB -> PlugType.Usb
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> PlugType.Wireless
        null -> PlugType.Unknown
        else -> PlugType.Unknown
    }

    private fun mapHealth(health: Int?): BatteryHealth = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.Good
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.Overheat
        BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.Dead
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OverVoltage
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UnspecifiedFailure
        BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.Cold
        else -> BatteryHealth.Unknown
    }
}
