package dev.ragnar.aegis.data.diagnostics

import dev.ragnar.aegis.domain.diagnostics.DiagnosticsSnapshot

/**
 * Repository interface for retrieving system and launcher diagnostic metrics.
 */
interface DiagnosticsRepository {
    fun getSnapshot(): DiagnosticsSnapshot
    fun copyToClipboard(text: String): Boolean
}
