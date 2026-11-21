# PHASE-04-APP-DISCOVERY.md — AEGIS HUD Launcher

**Phase ID:** PHASE-04  
**Title:** Application Discovery & Launching  
**Status:** Implementation Complete — Build Verified (Device Validation Pending) (2026-11-21)  
**Dependency:** PHASE-03 (implemented; build verified)

---

## Objective

Discover Android activities that advertise `ACTION_MAIN` and `CATEGORY_LAUNCHER`, expose a
testable, alphabetically sorted app list, show the list in the HUD app drawer and quick-launch
row, launch a selected activity safely, and refresh while the package set changes.

## Scope and Constraints

- Query only launcher activities; do not request `QUERY_ALL_PACKAGES`.
- Keep Android framework objects behind a repository boundary. The `AppInfo` model and sorting
  logic must remain JVM-testable.
- Cache icons locally through `PackageManager`; no image-loading dependency is added.
- Dynamically register for package added, removed, and changed broadcasts while the UI collects
  the repository flow. The receiver is unregistered with the flow collection.
- Use `FLAG_ACTIVITY_NEW_TASK` for external launches and show a visible, non-crashing error if
  the target is no longer launchable.
- Phase 5 owns text search. The existing search field remains non-interactive in this phase.

## Subtasks

| # | Task | Status |
|---|---|---|
| 1 | Define platform-neutral `AppInfo` model | Done |
| 2 | Implement sorted, de-duplicated JVM-safe app-list normalization | Done |
| 3 | Implement `PackageManager` repository for launcher query and icon cache | Done |
| 4 | Add package-change observation and lifecycle-bound collection | Done |
| 5 | Add app-drawer ViewModel and manual factory | Done |
| 6 | Render app drawer grid, icon/name cells, loading, empty, and error states | Done |
| 7 | Populate quick-launch row and wire drawer / app launch actions | Done |
| 8 | Add unit tests for sorting, de-duplication, and empty input | Done (3 tests added) |
| 9 | Run assemble, unit tests, and lint | Done (All passed) |
| 10 | Update phase and project documentation | Done |

## Acceptance Criteria

- [x] The manifest retains a targeted launcher `<queries>` declaration.
- [x] Launchable activities are displayed alphabetically, without duplicate components.
- [x] The app drawer shows application icons and labels, with loading and empty states.
- [x] A tap launches the selected application in its own task.
- [x] Missing, disabled, or unlaunchable activities fail safely with a user-visible error.
- [x] The list refreshes after package-added, package-removed, and package-changed broadcasts.
- [x] JVM tests cover alphabetical sorting, duplicate removal, and empty input.
- [x] `./gradlew assembleDebug`, `./gradlew testDebugUnitTest`, and `./gradlew lintDebug` succeed.

## Required Device Follow-up

The repository query, system icon rendering, real application launch, and package-change refresh
need a physical device or emulator for final validation. This does not replace the existing
Phase 1 device-validation blocker B-002.

## Validation Record

On 2026-11-21, NixOS environment blocker B-003 was resolved by patching the AAPT2 ELF interpreter using `patchelf --set-interpreter` pointing to the NixOS glibc loader (DEC-013). Minor Compose compilation issues in `AppDrawer.kt` and `QuickLaunchRow.kt` were resolved.

Validation runs executed:
- `./gradlew --no-daemon assembleDebug` — **BUILD SUCCESSFUL** (41s). APK generated at `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew --no-daemon testDebugUnitTest` — **BUILD SUCCESSFUL** (31s). 15 tests passed across 4 test classes (`AppListNormalizerTest`, `ClockViewModelTest`, `ThemeTokensTest`, `ExampleUnitTest`) with 0 failures, 0 errors, 0 skipped.
- `./gradlew --no-daemon lintDebug` — **BUILD SUCCESSFUL** (53s). 0 errors.

Device testing (launching on real device/emulator) remains pending connection of a target device (B-002).
