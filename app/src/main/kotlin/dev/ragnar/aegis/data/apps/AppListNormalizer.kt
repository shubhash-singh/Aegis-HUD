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
}
