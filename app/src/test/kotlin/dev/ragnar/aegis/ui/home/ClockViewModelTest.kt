package dev.ragnar.aegis.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [ClockViewModel] and [ClockState].
 *
 * Tests clock formatting, date formatting, and state snapshot correctness.
 * ViewModel coroutine tests require TestDispatcher — deferred to integration tests.
 */
class ClockViewModelTest {

    @Test
    fun `ClockState now() returns non-empty fields`() {
        val state = ClockState.now()
        assertNotNull(state.hours)
        assertNotNull(state.minutes)
        assertNotNull(state.seconds)
        assertNotNull(state.dateFormatted)
        assertNotNull(state.dayOfWeek)
        assertTrue("Hours should be non-blank", state.hours.isNotBlank())
        assertTrue("Minutes should be non-blank", state.minutes.isNotBlank())
        assertTrue("Seconds should be non-blank", state.seconds.isNotBlank())
    }

    @Test
    fun `ClockState hours and minutes are zero-padded`() {
        val state = ClockState.now()
        assertEquals("Hours must be 2 characters", 2, state.hours.length)
        assertEquals("Minutes must be 2 characters", 2, state.minutes.length)
        assertEquals("Seconds must be 2 characters", 2, state.seconds.length)
    }

    @Test
    fun `ClockState date is uppercase`() {
        val state = ClockState.now()
        assertEquals(
            "Date must be uppercase",
            state.dateFormatted,
            state.dateFormatted.uppercase()
        )
    }

    @Test
    fun `ClockState dayOfWeek is uppercase`() {
        val state = ClockState.now()
        assertEquals(
            "Day of week must be uppercase",
            state.dayOfWeek,
            state.dayOfWeek.uppercase()
        )
    }

    @Test
    fun `ClockState dayOfWeek is a valid day name`() {
        val validDays = listOf(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
            "FRIDAY", "SATURDAY", "SUNDAY"
        )
        val state = ClockState.now()
        assertTrue(
            "Day of week '${state.dayOfWeek}' must be a valid day name",
            state.dayOfWeek in validDays
        )
    }
}
