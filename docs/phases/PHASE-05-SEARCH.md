# PHASE-05 — Application Search

**Feature:** F-005 (Application Search)
**Priority:** P0
**Dependencies:** PHASE-04 (App Discovery & Launching)
**Status:** Implementation Complete — Build Verified (Device Validation Pending)
**Started:** 2026-11-21
**Completed:** 2026-11-21 (Build Verified)

---

## Objective

Implement real-time application search filtering in the app drawer. As the user types, the
visible app list updates instantly. Empty queries show all apps; no-results states are clearly
communicated. Pressing the search/enter IME action launches the top result.

---

## Sub-Phases

| ID | Title | Status |
|---|---|---|
| F-005-P1 | Search composable (text field + clear) | Implemented — Build Verified |
| F-005-P2 | Filter logic wired to app list | Implemented — Build Verified |
| F-005-P3 | Empty state and no-results state | Implemented — Build Verified |
| F-005-P4 | Keyboard handling and IME actions | Implemented — Build Verified |

---

## Acceptance Criteria

- [x] Results update as each character is typed (`AppDrawerViewModel.combine` on `searchQuery` and `AppListNormalizer.filter`).
- [x] Empty query shows all apps (verified by `UT-005-03`).
- [x] No-results state is communicated clearly (distinct message: `NO APPLICATIONS MATCHING "<QUERY>"`).
- [x] Pressing Enter or search action on keyboard launches top result (`launchTopResult()` on IME action).

---

## Unit Tests (TEST_PLAN.md)

- [x] `UT-005-01`: Filter by exact name returns correct app.
- [x] `UT-005-02`: Filter by partial name (case-insensitive) returns matches.
- [x] `UT-005-03`: Empty query returns all apps.
- [x] `UT-005-04`: Query with no matches returns empty list.

---

## Implementation Summary

1. Added `fun filter(apps: List<AppInfo>, query: String): List<AppInfo>` to `AppListNormalizer` (pure Kotlin, JVM-testable).
2. Added `searchQuery` state flow to `AppDrawerViewModel`, combined with `observeLaunchableApps()`, exposing `filteredApps` and `isNoResults`.
3. Created `HudSearchBar` composable with HUD styling, 48dp minimum touch target clear button, and IME search action.
4. Integrated search bar into `AppDrawer` between header and grid, with distinct "NO APPLICATIONS MATCHING" message.
5. Wired bottom nav search placeholder to open drawer with search field focused.
6. Added 4 unit tests (`UT-005-01` through `UT-005-04`) to `AppListNormalizerTest`.
7. Validated with `assembleDebug`, `testDebugUnitTest` (19 tests passed), and `lintDebug` (0 errors).

---

## Files Created

- `app/src/main/kotlin/dev/ragnar/aegis/ui/apps/HudSearchBar.kt`

## Files Modified

- `app/src/main/kotlin/dev/ragnar/aegis/data/apps/AppListNormalizer.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/apps/AppDrawerViewModel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/apps/AppDrawer.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/BottomNavBar.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/data/apps/AppListNormalizerTest.kt`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| 2026-11-21 | `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL (21s) | APK generated successfully |
| 2026-11-21 | `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (16s) | 19 tests passed (4 new search filter tests) |
| 2026-11-21 | `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (33s) | 0 errors |
