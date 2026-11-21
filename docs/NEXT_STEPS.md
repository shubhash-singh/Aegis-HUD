# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22 (Phase 11 build verified)
**Current Phase:** PHASE-11 — Application Organization (F-011) — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-12 — Developer Console (F-014) / Widget Hosting (F-012)

---

## Completed This Session

- Implemented **PHASE-11 (Application Organization — F-011)**:
  - Created `AppSortOrder` domain enum supporting: `AZ`, `ZA`, `FavoritesFirst`, `InstallDate`.
  - Extended `AppInfo` with `installTimeMillis` populated dynamically via `PackageManager.getPackageInfo().firstInstallTime`.
  - Implemented pure sorting in `AppListNormalizer.sort()` handling all four sort modes with deterministic tie-breaking.
  - Added DataStore persistence for `favoriteApps: Set<String>`, `appSortOrder: AppSortOrder`, and `drawerGridColumns: Int` (clamped 3..5).
  - Enhanced `PreferencesRepository` and `DataStorePreferencesRepository` with `toggleFavorite`, `setFavorites`, `clearFavorites`, `setAppSortOrder`, `setDrawerGridColumns`.
  - Updated `AppDrawerViewModel` combining app discovery, preferences flow, and search query into `AppDrawerUiState`.
  - Updated `AppDrawer` composable:
    - Dedicated "PINNED // QUICK LAUNCH" section for instant 1-tap launching when not searching.
    - Interactive `[SORT: A-Z]` button in header to cycle sort modes.
    - Interactive `[N COL]` button in header to cycle grid density (3, 4, 5 columns).
    - Long-press on app cells (`combinedClickable`) to pin/unpin apps with haptic confirmation.
    - Subtle glowing cyan star badge (`★`) on pinned application cells.
  - Updated `SettingsSheet` with an "App Drawer Organization" configuration section.
  - Added 13 new unit tests across `AppListNormalizerSortTest`, `UserPreferencesTest`, `SettingsViewModelTest`, and `AppDrawerViewModelTest` (79 total project tests passing).
  - Validated: `assembleDebug` (27s), `testDebugUnitTest` (13s, 79 passed), `lintDebug` (1m 6s, 0 errors).

---

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 11
   (app discovery, search, full-screen neural AI core animation, 7 floating panels, swipe-up drawer gesture, quick actions torch toggle, settings intents, DataStore persistence, and app pinning/sorting/grid layout). (Low severity; development can proceed).

---

## Exact Next Task

Begin **PHASE-12 (Developer Console — F-014)**:
1. Review `docs/FEATURE_PLAN.md` for F-014 (Developer Console) scope:
   - F-014-P1: Console screen scaffold and navigation route
   - F-014-P2: Launcher diagnostics (uptime, restart count, memory footprint)
   - F-014-P3: System information panel (Android version, API level, device model)
   - F-014-P4: Permission status display (which permissions are granted)
   - F-014-P5: Log viewer (recent launcher log entries in in-memory ring buffer)
   - F-014-P6: Export/copy diagnostic snapshot
2. Create `docs/phases/PHASE-12-DEVELOPER-CONSOLE.md`.
3. Implement developer diagnostics repository and in-memory ring buffer.
4. Implement `DeveloperConsoleViewModel` and `DeveloperConsoleScreen`.
5. Wire console route accessible via HUD Settings or gesture.
6. Add unit tests and verify via Gradle.

---

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-11-APP-ORGANIZATION.md`
- `docs/FEATURE_PLAN.md` (F-014: Developer Console)
- `docs/ARCHITECTURE.md`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
