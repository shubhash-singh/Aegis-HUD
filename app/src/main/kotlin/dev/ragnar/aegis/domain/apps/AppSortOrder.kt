package dev.ragnar.aegis.domain.apps

/**
 * Ordering options for the applications drawer.
 */
enum class AppSortOrder(
    val id: String,
    val displayName: String,
    val shortLabel: String,
) {
    AZ(
        id = "az",
        displayName = "NAME (A-Z)",
        shortLabel = "A-Z",
    ),
    ZA(
        id = "za",
        displayName = "NAME (Z-A)",
        shortLabel = "Z-A",
    ),
    FavoritesFirst(
        id = "favorites_first",
        displayName = "FAVORITES FIRST",
        shortLabel = "FAV",
    ),
    InstallDate(
        id = "install_date",
        displayName = "RECENTLY INSTALLED",
        shortLabel = "DATE",
    );

    companion object {
        fun fromId(id: String?): AppSortOrder {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: AZ
        }
    }
}
