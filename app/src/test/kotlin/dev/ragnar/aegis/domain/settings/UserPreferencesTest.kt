package dev.ragnar.aegis.domain.settings

import dev.ragnar.aegis.ui.home.ClockState
import dev.ragnar.aegis.ui.theme.AegisColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * Unit tests for [UserPreferences], [AccentColor], and [AnimationSpeed] — F-010 (Customization).
 */
class UserPreferencesTest {

    @Test
    fun `UT-010-01 default user preferences match system defaults`() {
        val prefs = UserPreferences()
        assertEquals(AccentColor.Cyan, prefs.accentColor)
        assertEquals(AnimationSpeed.Normal, prefs.animationSpeed)
        assertTrue(prefs.showRadar)
        assertTrue(prefs.showSystemStatus)
        assertTrue(prefs.showTelemetry)
        assertTrue(prefs.showQuickActions)
        assertTrue(prefs.is24HourClock)
        assertTrue(prefs.showClockSeconds)
    }

    @Test
    fun `UT-010-02 AccentColor fromId maps correctly and falls back to Cyan`() {
        assertEquals(AccentColor.Cyan, AccentColor.fromId("cyan"))
        assertEquals(AccentColor.Amber, AccentColor.fromId("amber"))
        assertEquals(AccentColor.Green, AccentColor.fromId("green"))
        assertEquals(AccentColor.Crimson, AccentColor.fromId("crimson"))
        assertEquals(AccentColor.Cyan, AccentColor.fromId("invalid"))
        assertEquals(AccentColor.Cyan, AccentColor.fromId(null))
    }

    @Test
    fun `UT-010-03 AnimationSpeed fromId maps correctly and falls back to Normal`() {
        assertEquals(AnimationSpeed.Fast, AnimationSpeed.fromId("fast"))
        assertEquals(AnimationSpeed.Normal, AnimationSpeed.fromId("normal"))
        assertEquals(AnimationSpeed.Slow, AnimationSpeed.fromId("slow"))
        assertEquals(AnimationSpeed.Disabled, AnimationSpeed.fromId("off"))
        assertEquals(AnimationSpeed.Normal, AnimationSpeed.fromId("unknown"))
        assertEquals(AnimationSpeed.Normal, AnimationSpeed.fromId(null))
    }

    @Test
    fun `UT-010-04 withAccent maps accent tokens to primary color`() {
        val defaultScheme = AegisColorScheme.default()
        val amberScheme = defaultScheme.withAccent(AccentColor.Amber)

        assertEquals(androidx.compose.ui.graphics.Color(AccentColor.Amber.primaryColorArgb), amberScheme.cyan)
        assertEquals(androidx.compose.ui.graphics.Color(AccentColor.Amber.primaryColorArgb), amberScheme.borderActive)
        assertEquals(androidx.compose.ui.graphics.Color(AccentColor.Amber.primaryColorArgb), amberScheme.textAccent)
    }

    @Test
    fun `UT-010-05 ClockState 12H vs 24H formatting`() {
        val state24 = ClockState.now(is24Hour = true)
        assertTrue(state24.hours.length == 2)

        val state12 = ClockState.now(is24Hour = false)
        assertTrue(state12.hours.length == 2)
        val hourInt = state12.hours.toInt()
        assertTrue("12H format should be between 1 and 12, was $hourInt", hourInt in 1..12)
    }
}
