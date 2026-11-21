# PHASE-11 — Application Organization

**Feature:** F-011 (Application Organization)  
**Dependencies:** PHASE-04 (App Discovery & Launching), PHASE-05 (Search), PHASE-09 (Customization & DataStore), PHASE-10 (AI Command Interface)  
**Status:** Implementation Complete — Build Verified (Device Validation Pending)  
**Started:** 2026-11-21  
**Completed:** 2026-11-22 (Build Verified)  

---

## Objective

Empower users to personalize and organize their applications within the AEGIS launcher:
1. Pin and favorite frequently used applications with persistent storage.
2. Render a dedicated, quick-access "PINNED / FAVORITES" row or section at the top of the App Drawer.
3. Support flexible application sorting: Alphabetical (A-Z), Alphabetical (Z-A), Favorites First, and Recently Installed.
4. Provide configurable grid layouts (3, 4, or 5 columns) adapted to screen size and user preference.
5. Provide intuitive quick-toggle controls directly within the App Drawer and comprehensive controls in the HUD Settings sheet.

---

## Sub-Phases

| ID | Title | Scope | Status |
|---|---|---|---|
| **F-011-P1** | Pin / Favorite Applications | Persistent DataStore storage for pinned apps (`Set<String>` of `componentName`); long-press to pin/unpin; HUD pin indicator on cells; top Pinned section in drawer. | Implemented — Build Verified |
| **F-011-P2** | Configurable Quick-Launch Slots | Instant 1-tap launching for pinned favorites at top of drawer without scrolling through full grid. | Implemented — Build Verified |
| **F-011-P3** | Sort by Name or User Preference | `AppSortOrder` enum (`AZ`, `ZA`, `FavoritesFirst`, `InstallDate`); pure sorting in `AppListNormalizer`; sort selector in App Drawer header and Settings sheet. | Implemented — Build Verified |
| **F-011-P4** | Grid Layout Configuration | Configurable column counts (3, 4, 5 columns); persistent DataStore setting; dynamic `GridCells.Fixed(columns)` in `AppDrawer`. | Implemented — Build Verified |

---

## Architecture & Data Flow

### 1. Domain Models (`dev.ragnar.aegis.domain`)

#### `AppSortOrder`
```kotlin
enum class AppSortOrder(
    val id: String,
    val displayName: String,
    val shortLabel: String,
) {
    AZ("az", "NAME (A-Z)", "A-Z"),
    ZA("za", "NAME (Z-A)", "Z-A"),
    FavoritesFirst("favorites_first", "FAVORITES FIRST", "FAV"),
    InstallDate("install_date", "RECENTLY INSTALLED", "DATE");

    companion object {
        fun fromId(id: String?): AppSortOrder =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: AZ
    }
}
```

#### `AppInfo`
Extended with `installTimeMillis: Long = 0L` to support `InstallDate` sorting deterministically while retaining backwards compatibility.

#### `UserPreferences`
```kotlin
data class UserPreferences(
    val accentColor: AccentColor = AccentColor.Cyan,
    val animationSpeed: AnimationSpeed = AnimationSpeed.Normal,
    val showRadar: Boolean = true,
    val showSystemStatus: Boolean = true,
    val showTelemetry: Boolean = true,
    val showQuickActions: Boolean = true,
    val is24HourClock: Boolean = true,
    val showClockSeconds: Boolean = true,
    val favoriteApps: Set<String> = emptySet(),
    val appSortOrder: AppSortOrder = AppSortOrder.AZ,
    val drawerGridColumns: Int = 4,
)
```

### 2. Persistence Layer (`dev.ragnar.aegis.data.settings`)

`PreferencesRepository` and `DataStorePreferencesRepository`:
- Keys:
  - `FAVORITE_APPS = stringSetPreferencesKey("favorite_apps")`
  - `APP_SORT_ORDER = stringPreferencesKey("app_sort_order")`
  - `DRAWER_GRID_COLUMNS = intPreferencesKey("drawer_grid_columns")`
- Methods:
  - `toggleFavorite(componentName: String)`
  - `setFavorites(favorites: Set<String>)`
  - `setAppSortOrder(order: AppSortOrder)`
  - `setDrawerGridColumns(columns: Int)`

### 3. Pure Sorting Logic (`AppListNormalizer`)

```kotlin
fun sort(
    apps: List<AppInfo>,
    sortOrder: AppSortOrder,
    favoriteComponents: Set<String> = emptySet(),
): List<AppInfo>
```
- `AZ`: Alphabetical ascending by label, then packageName, then componentName.
- `ZA`: Alphabetical descending by label, then packageName, then componentName.
- `FavoritesFirst`: Pinned apps partitioned first (sorted A-Z among themselves), followed by non-pinned apps (sorted A-Z).
- `InstallDate`: Descending by `installTimeMillis` (newest first), then label.

### 4. Presentation Layer (`dev.ragnar.aegis.ui.apps`)

#### `AppDrawerUiState`
```kotlin
data class AppDrawerUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val pinnedApps: List<AppInfo> = emptyList(),
    val favoriteComponents: Set<String> = emptySet(),
    val sortOrder: AppSortOrder = AppSortOrder.AZ,
    val gridColumns: Int = 4,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isNoResults: Boolean = false,
)
```

#### `AppDrawerViewModel`
Combines `AppRepository.observeLaunchableApps()`, `PreferencesRepository.userPreferences`, and `_searchQuery` to produce reactive, sorted, filtered, and pinned app state. Exposes `toggleFavorite(app)`, `setSortOrder(order)`, and `setGridColumns(columns)`.

#### `AppDrawer` Composable
- Header controls:
  - Current sort indicator button (cycles or toggles sort order).
  - Column density selector button (cycles 3 → 4 → 5 → 3).
- Pinned / Quick Launch Row:
  - When not searching and `pinnedApps` is non-empty, renders a dedicated horizontal or grid section "PINNED [N]" at the top of the drawer with distinct glowing cyan accents.
- App Grid Cells:
  - Long press triggers pin/unpin toggle with haptic/visual confirmation.
  - Pin icon badge (`★` or `✦`) shown on cells that are currently pinned.
  - Grid column count driven directly by `state.gridColumns`.

#### `SettingsSheet`
Added dedicated section "APP DRAWER CONFIGURATION":
- Sort order selection chips (A-Z, Z-A, Favorites First, Recently Installed).
- Grid column selector (3, 4, 5 columns).
- Pinned apps count display and clear all favorites button.

---

## Acceptance Criteria

- [x] Pinned applications persist across app restarts using DataStore.
- [x] Long-pressing an app cell in the drawer toggles its pinned status immediately.
- [x] Pinned apps render with a subtle HUD pin indicator badge on their cell.
- [x] A dedicated "PINNED / QUICK LAUNCH" section renders at the top of the drawer when not searching.
- [x] Sorting by A-Z, Z-A, Favorites First, and Install Date orders apps correctly.
- [x] Changing sort order applies immediately in the drawer and persists across restarts.
- [x] Changing grid columns (3, 4, 5) adjusts the drawer layout immediately and persists.
- [x] App search continues to filter smoothly across all apps regardless of sort order or pin status.
- [x] Unit tests validate `AppListNormalizer.sort()`, `UserPreferences` defaults and parsing, and `AppDrawerViewModel` state updates (13 new unit tests, 79 total project tests passing).
- [x] `./gradlew testDebugUnitTest` passes (79 tests passed, 0 failures).
- [x] `./gradlew assembleDebug` passes (BUILD SUCCESSFUL).
- [x] `./gradlew lintDebug` passes with zero errors (BUILD SUCCESSFUL).
