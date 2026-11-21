package dev.ragnar.aegis.data.telemetry

import android.os.BatteryManager
import dev.ragnar.aegis.domain.telemetry.BatteryHealth
import dev.ragnar.aegis.domain.telemetry.ChargingStatus
import dev.ragnar.aegis.domain.telemetry.PlugType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BatteryNormalizerTest {

    // --- F-006 Battery Telemetry Unit Tests (TEST_PLAN.md UT-006-02 to UT-006-04) ---

    @Test
    fun `UT-006-02 Platform-neutral battery values map to charging status correctly`() {
        val charging = BatteryNormalizer.normalize(
            level = 50,
            scale = 100,
            status = BatteryManager.BATTERY_STATUS_CHARGING,
            plugged = BatteryManager.BATTERY_PLUGGED_AC,
            temperatureTenths = 300,
            voltageMv = 4000,
            health = BatteryManager.BATTERY_HEALTH_GOOD,
        )
        assertEquals(ChargingStatus.Charging, charging.chargingStatus)
        assertEquals(PlugType.Ac, charging.plugType)

        val full = BatteryNormalizer.normalize(
            level = 100,
            scale = 100,
            status = BatteryManager.BATTERY_STATUS_FULL,
            plugged = BatteryManager.BATTERY_PLUGGED_USB,
            temperatureTenths = 280,
            voltageMv = 4200,
            health = BatteryManager.BATTERY_HEALTH_GOOD,
        )
        assertEquals(ChargingStatus.Full, full.chargingStatus)
        assertEquals(PlugType.Usb, full.plugType)

        val discharging = BatteryNormalizer.normalize(
            level = 80,
            scale = 100,
            status = BatteryManager.BATTERY_STATUS_DISCHARGING,
            plugged = 0,
            temperatureTenths = 290,
            voltageMv = 3900,
            health = BatteryManager.BATTERY_HEALTH_GOOD,
        )
        assertEquals(ChargingStatus.Discharging, discharging.chargingStatus)
        assertEquals(PlugType.None, discharging.plugType)
    }

    @Test
    fun `UT-006-03 Temperature converts from tenths of degrees to degrees`() {
        val state = BatteryNormalizer.normalize(
            level = 80,
            scale = 100,
            status = BatteryManager.BATTERY_STATUS_DISCHARGING,
            plugged = 0,
            temperatureTenths = 315, // 31.5°C
            voltageMv = 4000,
            health = BatteryManager.BATTERY_HEALTH_GOOD,
        )

        assertEquals(31.5f, state.temperatureCelsius ?: 0f, 0.01f)
    }

    @Test
    fun `UT-006-04 Missing platform-neutral values produce safe defaults`() {
        val state = BatteryNormalizer.normalize(
            level = null,
            scale = null,
            status = null,
            plugged = null,
            temperatureTenths = null,
            voltageMv = null,
            health = null,
        )

        assertNull(state.percentage)
        assertEquals(ChargingStatus.Unknown, state.chargingStatus)
        assertEquals(PlugType.Unknown, state.plugType)
        assertNull(state.temperatureCelsius)
        assertNull(state.voltageMv)
        assertEquals(BatteryHealth.Unknown, state.health)
    }

    @Test
    fun `percentage is correctly calculated and clamped between 0 and 100`() {
        val normal = BatteryNormalizer.normalize(
            level = 42,
            scale = 100,
            status = null,
            plugged = null,
            temperatureTenths = null,
            voltageMv = null,
            health = null,
        )
        assertEquals(42, normal.percentage)

        val non100Scale = BatteryNormalizer.normalize(
            level = 15,
            scale = 30,
            status = null,
            plugged = null,
            temperatureTenths = null,
            voltageMv = null,
            health = null,
        )
        assertEquals(50, non100Scale.percentage)
    }
}
