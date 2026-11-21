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
}
