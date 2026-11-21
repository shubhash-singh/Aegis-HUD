package dev.ragnar.aegis.data.telemetry

import dev.ragnar.aegis.domain.telemetry.NetworkType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkNormalizerTest {

    // --- F-008 System Telemetry Unit Tests (TEST_PLAN.md UT-008-02) ---

    @Test
    fun `UT-008-02 Fake network source produces the correct offline NetworkState`() {
        val offline = NetworkNormalizer.normalize(
            hasCapabilityInternet = false,
            hasTransportWifi = false,
            hasTransportCellular = false,
            hasTransportEthernet = false,
            hasTransportVpn = false,
            isMetered = false,
        )

        assertFalse(offline.isConnected)
        assertEquals(NetworkType.None, offline.type)
        assertFalse(offline.isVpn)
    }

    @Test
    fun `wifi transport maps to NetworkType Wifi`() {
        val wifi = NetworkNormalizer.normalize(
            hasCapabilityInternet = true,
            hasTransportWifi = true,
            hasTransportCellular = false,
            hasTransportEthernet = false,
            hasTransportVpn = false,
            isMetered = false,
        )

        assertTrue(wifi.isConnected)
        assertEquals(NetworkType.Wifi, wifi.type)
        assertFalse(wifi.isMetered)
    }

    @Test
    fun `cellular transport maps to NetworkType Cellular and preserves metered flag`() {
        val cellular = NetworkNormalizer.normalize(
            hasCapabilityInternet = true,
            hasTransportWifi = false,
            hasTransportCellular = true,
            hasTransportEthernet = false,
            hasTransportVpn = false,
            isMetered = true,
        )

        assertTrue(cellular.isConnected)
        assertEquals(NetworkType.Cellular, cellular.type)
        assertTrue(cellular.isMetered)
    }

    @Test
    fun `vpn transport flag is preserved`() {
        val vpn = NetworkNormalizer.normalize(
            hasCapabilityInternet = true,
            hasTransportWifi = true,
            hasTransportCellular = false,
            hasTransportEthernet = false,
            hasTransportVpn = true,
            isMetered = false,
        )

        assertTrue(vpn.isConnected)
        assertTrue(vpn.isVpn)
    }

    @Test
    fun `calculateBandwidth with positive deltas returns expected rx and tx rate`() {
        val (rx, tx) = NetworkNormalizer.calculateBandwidth(
            currentRxBytes = 194_560L,
            previousRxBytes = 0L,
            currentTxBytes = 20_480L,
            previousTxBytes = 0L,
            elapsedSeconds = 1.0,
        )
        assertEquals(194_560L, rx)
        assertEquals(20_480L, tx)
    }

    @Test
    fun `calculateBandwidth handles counter wrap or reboot by returning zero`() {
        val (rx, tx) = NetworkNormalizer.calculateBandwidth(
            currentRxBytes = 100L,
            previousRxBytes = 500L,
            currentTxBytes = 100L,
            previousTxBytes = 500L,
            elapsedSeconds = 1.0,
        )
        assertEquals(0L, rx)
        assertEquals(0L, tx)
    }

    @Test
    fun `calculateBandwidth handles zero or negative elapsed time gracefully`() {
        val (rx, tx) = NetworkNormalizer.calculateBandwidth(
            currentRxBytes = 1000L,
            previousRxBytes = 500L,
            currentTxBytes = 1000L,
            previousTxBytes = 500L,
            elapsedSeconds = 0.0,
        )
        assertEquals(0L, rx)
        assertEquals(0L, tx)
    }

    @Test
    fun `formatSpeed formats kilobytes and megabytes correctly`() {
        assertEquals("0kb/s", NetworkNormalizer.formatSpeed(0L))
        assertEquals("190kb/s", NetworkNormalizer.formatSpeed(194_560L))
        assertEquals("20kb/s", NetworkNormalizer.formatSpeed(20_480L))
        assertEquals("2.5mb/s", NetworkNormalizer.formatSpeed((2.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun `normalize preserves throughput metrics`() {
        val state = NetworkNormalizer.normalize(
            hasCapabilityInternet = true,
            hasTransportWifi = true,
            hasTransportCellular = false,
            hasTransportEthernet = false,
            hasTransportVpn = false,
            isMetered = false,
            downBytesPerSecond = 194_560L,
            upBytesPerSecond = 20_480L,
        )
        assertEquals(194_560L, state.downBytesPerSecond)
        assertEquals(20_480L, state.upBytesPerSecond)
    }

    @Test
    fun `formatNetworkLabel produces user-specified down and up arrow format`() {
        val state = dev.ragnar.aegis.domain.telemetry.NetworkState(
            isConnected = true,
            type = NetworkType.Wifi,
            downBytesPerSecond = 194_560L,
            upBytesPerSecond = 20_480L,
        )
        val label = dev.ragnar.aegis.ui.telemetry.TelemetryViewModel.formatNetworkLabel(state)
        assertEquals("WIFI - 190kb/s↓ 20kb/s↑", label)
    }

    @Test
    fun `formatNetworkLabel produces OFFLINE when disconnected`() {
        val state = dev.ragnar.aegis.domain.telemetry.NetworkState(
            isConnected = false,
            type = NetworkType.None,
            downBytesPerSecond = 0L,
            upBytesPerSecond = 0L,
        )
        val label = dev.ragnar.aegis.ui.telemetry.TelemetryViewModel.formatNetworkLabel(state)
        assertEquals("OFFLINE", label)
    }
}
