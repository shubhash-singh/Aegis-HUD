package dev.ragnar.aegis.domain.apps

/**
 * A launchable Android activity represented without framework types.
 *
 * Keeping this model platform-neutral makes list ordering and de-duplication JVM-testable.
 * [componentName] uses Android's flattened `package/class` representation.
 * [installTimeMillis] indicates when the package was installed (used for [AppSortOrder.InstallDate]).
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: String,
    val installTimeMillis: Long = 0L,
)
