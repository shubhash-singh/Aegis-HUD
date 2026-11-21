package dev.ragnar.aegis.domain.widget

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Deterministic, zero-allocation serialization for hosted widgets in DataStore.
 * Safe against arbitrary characters in labels using URL encoding.
 */
object WidgetSerializer {
    private const val RECORD_SEPARATOR = ";"
    private const val FIELD_SEPARATOR = ":"
    private val CHARSET = StandardCharsets.UTF_8.name()

    fun serialize(widgets: List<HostedWidget>): String {
        if (widgets.isEmpty()) return ""
        return widgets.joinToString(separator = RECORD_SEPARATOR) { widget ->
            val escapedLabel = URLEncoder.encode(widget.label, CHARSET)
            "${widget.appWidgetId}$FIELD_SEPARATOR${widget.providerPackage}$FIELD_SEPARATOR${widget.providerClass}$FIELD_SEPARATOR${widget.heightDp}$FIELD_SEPARATOR$escapedLabel"
        }
    }

    fun deserialize(serialized: String?): List<HostedWidget> {
        if (serialized.isNullOrBlank()) return emptyList()
        return serialized.split(RECORD_SEPARATOR).mapNotNull { token ->
            if (token.isBlank()) return@mapNotNull null
            runCatching {
                val parts = token.split(FIELD_SEPARATOR)
                if (parts.size >= 5) {
                    val id = parts[0].toInt()
                    val pkg = parts[1]
                    val cls = parts[2]
                    val height = parts[3].toInt()
                    val label = URLDecoder.decode(parts[4], CHARSET)
                    HostedWidget(
                        appWidgetId = id,
                        providerPackage = pkg,
                        providerClass = cls,
                        label = label,
                        heightDp = height,
                    )
                } else null
            }.getOrNull()
        }
    }
}
