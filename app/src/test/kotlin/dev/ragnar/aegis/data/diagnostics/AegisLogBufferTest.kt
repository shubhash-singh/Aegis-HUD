package dev.ragnar.aegis.data.diagnostics

import dev.ragnar.aegis.domain.diagnostics.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AegisLogBuffer] — F-014 (Developer Console).
 */
class AegisLogBufferTest {

    @Before
    fun setUp() {
        AegisLogBuffer.clear()
    }

    @Test
    fun `UT-014-01 log buffer adds entries and limits capacity to MAX_CAPACITY`() {
        // Clear leaves 1 initial log entry ("Log buffer cleared")
        AegisLogBuffer.clear()

        for (i in 1..150) {
            AegisLogBuffer.info("TEST", "Message $i")
        }

        val entries = AegisLogBuffer.getEntries()
        assertEquals(AegisLogBuffer.MAX_CAPACITY, entries.size)

        // The oldest messages should have rolled off, the latest should be present
        assertEquals("Message 150", entries.last().message)
        assertEquals("Message 51", entries.first().message)
    }

    @Test
    fun `UT-014-02 log buffer clears entries`() {
        AegisLogBuffer.warn("TAG", "Warning message")
        AegisLogBuffer.error("TAG", "Error message")

        AegisLogBuffer.clear()

        val entries = AegisLogBuffer.getEntries()
        // clear() resets buffer and adds 1 log entry recording the clear event
        assertEquals(1, entries.size)
        assertEquals("Log buffer cleared", entries.first().message)
    }

    @Test
    fun `UT-014-03 log entries retain timestamp, level, tag, and message`() {
        AegisLogBuffer.clear()
        AegisLogBuffer.debug("CUSTOM_TAG", "Debug payload")

        val entries = AegisLogBuffer.getEntries()
        val entry = entries.last()

        assertEquals("CUSTOM_TAG", entry.tag)
        assertEquals("Debug payload", entry.message)
        assertEquals(LogLevel.DEBUG, entry.level)
        assertTrue(entry.timestampMillis > 0)
        assertTrue(entry.timestampFormatted.isNotBlank())
    }
}
