package dev.ragnar.aegis.data.diagnostics

import dev.ragnar.aegis.domain.diagnostics.LogEntry
import dev.ragnar.aegis.domain.diagnostics.LogLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Thread-safe in-memory circular buffer storing recent launcher event logs.
 *
 * Enforces a strict capacity cap (100 entries) to guarantee zero memory leaks
 * and zero disk I/O (DEC-009).
 */
object AegisLogBuffer {
    const val MAX_CAPACITY = 100

    private val lock = Any()
    private val buffer = ArrayDeque<LogEntry>(MAX_CAPACITY)
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.ROOT)

    init {
        // Initial bootstrap log
        log(LogLevel.INFO, "AEGIS", "Diagnostics buffer initialized (capacity: $MAX_CAPACITY)")
    }

    fun log(level: LogLevel, tag: String, message: String) {
        val now = System.currentTimeMillis()
        val formatted = synchronized(dateFormat) {
            dateFormat.format(Date(now))
        }
        val entry = LogEntry(
            timestampMillis = now,
            timestampFormatted = formatted,
            level = level,
            tag = tag,
            message = message,
        )
        synchronized(lock) {
            if (buffer.size >= MAX_CAPACITY) {
                buffer.removeFirst()
            }
            buffer.addLast(entry)
        }
    }

    fun debug(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun info(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun warn(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun error(tag: String, message: String) = log(LogLevel.ERROR, tag, message)

    fun getEntries(): List<LogEntry> = synchronized(lock) {
        buffer.toList()
    }

    fun clear() = synchronized(lock) {
        buffer.clear()
        log(LogLevel.INFO, "AEGIS", "Log buffer cleared")
    }
}
