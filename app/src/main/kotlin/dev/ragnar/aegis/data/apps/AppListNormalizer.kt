package dev.ragnar.aegis.data.apps

import dev.ragnar.aegis.domain.apps.AppInfo
import java.util.Locale

/** Pure app-list rules shared by the Android adapter and JVM tests. */
object AppListNormalizer {
    /**
     * Removes duplicate activity components and orders labels case-insensitively. Package and
     * component provide deterministic tie-breakers for apps with identical visible labels.
     */
    fun normalize(apps: Collection<AppInfo>): List<AppInfo> = apps
        .distinctBy(AppInfo::componentName)
        .sortedWith(
            compareBy<AppInfo> { it.label.lowercase(Locale.ROOT) }
                .thenBy { it.packageName }
                .thenBy { it.componentName },
        )

    /**
     * Filters a normalised app list by [query]. Returns all apps when [query] is blank;
     * otherwise performs a case-insensitive substring match against [AppInfo.label].
     *
     * This function is intentionally pure (no Android dependency) so that filter rules
     * are JVM-testable.
     */
    fun filter(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        val lowerQuery = query.trim().lowercase(Locale.ROOT)
        return apps.filter { it.label.lowercase(Locale.ROOT).contains(lowerQuery) }
    }
}
