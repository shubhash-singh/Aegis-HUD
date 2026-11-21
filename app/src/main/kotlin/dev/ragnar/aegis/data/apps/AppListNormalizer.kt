package dev.ragnar.aegis.data.apps

import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.domain.apps.AppSortOrder
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
     * Filters an app list by [query]. Returns all apps when [query] is blank;
     * otherwise performs a case-insensitive substring match against [AppInfo.label].
     */
    fun filter(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        val lowerQuery = query.trim().lowercase(Locale.ROOT)
        return apps.filter { it.label.lowercase(Locale.ROOT).contains(lowerQuery) }
    }

    /**
     * Sorts an app list based on [sortOrder] and [favoriteComponents].
     *
     * - [AppSortOrder.AZ]: Alphabetical ascending by label.
     * - [AppSortOrder.ZA]: Alphabetical descending by label.
     * - [AppSortOrder.FavoritesFirst]: Pinned/favorite apps first (sorted A-Z), followed by others (sorted A-Z).
     * - [AppSortOrder.InstallDate]: Descending by install time (most recently installed first).
     */
    fun sort(
        apps: List<AppInfo>,
        sortOrder: AppSortOrder = AppSortOrder.AZ,
        favoriteComponents: Set<String> = emptySet(),
    ): List<AppInfo> = when (sortOrder) {
        AppSortOrder.AZ -> apps.sortedWith(
            compareBy<AppInfo> { it.label.lowercase(Locale.ROOT) }
                .thenBy { it.packageName }
                .thenBy { it.componentName },
        )
        AppSortOrder.ZA -> apps.sortedWith(
            compareByDescending<AppInfo> { it.label.lowercase(Locale.ROOT) }
                .thenByDescending { it.packageName }
                .thenByDescending { it.componentName },
        )
        AppSortOrder.FavoritesFirst -> {
            val (favorites, nonFavorites) = apps.partition { it.componentName in favoriteComponents }
            val sortedFavs = favorites.sortedWith(
                compareBy<AppInfo> { it.label.lowercase(Locale.ROOT) }
                    .thenBy { it.packageName }
                    .thenBy { it.componentName },
            )
            val sortedNonFavs = nonFavorites.sortedWith(
                compareBy<AppInfo> { it.label.lowercase(Locale.ROOT) }
                    .thenBy { it.packageName }
                    .thenBy { it.componentName },
            )
            sortedFavs + sortedNonFavs
        }
        AppSortOrder.InstallDate -> apps.sortedWith(
            compareByDescending<AppInfo> { it.installTimeMillis }
                .thenBy { it.label.lowercase(Locale.ROOT) }
                .thenBy { it.packageName }
                .thenBy { it.componentName },
        )
    }
}
