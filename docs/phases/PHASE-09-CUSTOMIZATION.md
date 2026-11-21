# PHASE-09 — Customization

**Feature:** F-010 (Customization, P1)
**Dependencies:** PHASE-02 (HUD Design System), PHASE-03 (HUD Home Screen), PHASE-06 (Animated Radar), PHASE-07 (Telemetry), PHASE-08 (Quick Actions)
**Status:** Implementation Complete — Build Verified (Device Validation Pending)
**Started:** 2026-11-21
**Completed:** 2026-11-21 (Build Verified)

---

## Objective

Implement HUD customization and persistence using Jetpack DataStore Preferences:
1. Persistent user preferences via DataStore Preferences (`androidx.datastore.preferences`).
2. Accent color customization (Cyan, Amber, Matrix Green, Tactical Crimson).
3. Panel visibility toggles (Radar, System Status, Telemetry, Quick Actions).
4. Radar animation speed settings (Normal 4s, Fast 2s, Slow 8s, Disabled).
5. Clock style customization (24-hour / 12-hour, toggle seconds).
6. Settings modal/screen with HUD styling matching UI_UX_SPECIFICATION.md.
7. Immediate dynamic update across the launcher without restart.
8. Safe default values without mandatory user configuration.

---

## Sub-Phases

### F-010: Customization (P1)

| ID | Title | Status |
|---|---|---|
| F-010-P1 | DataStore preferences setup & repository | Implemented — Build Verified |
| F-010-P2 | Accent color selection (theme tokens) | Implemented — Build Verified |
| F-010-P3 | Panel visibility toggles | Implemented — Build Verified |
| F-010-P4 | Animation speed setting | Implemented — Build Verified |
| F-010-P5 | Clock style selection | Implemented — Build Verified |

---

## Acceptance Criteria

- [x] Preferences persist across application restarts via DataStore.
- [x] Theme accent color updates live across all HUD panels and borders.
- [x] Panel visibility toggles immediately hide/show designated home screen panels.
- [x] Animation speed setting updates radar rotation speed or disables animation.
- [x] Clock updates format immediately (24H vs 12H, seconds visibility).
- [x] Sensible defaults load when no preferences are saved.
- [x] Unit tests verify pure mapping, default values, and repository updates.
- [x] `./gradlew assembleDebug`, `testDebugUnitTest`, and `lintDebug` all pass with 0 errors.

---

## Unit Tests

- [x] `UT-010-01`: Default user preferences match system defaults.
- [x] `UT-010-02`: Accent color palette mapping produces valid theme color schemes and fallbacks.
- [x] `UT-010-03`: Animation speed mapping handles valid and invalid identifiers safely.
- [x] `UT-010-04`: `withAccent` maps accent tokens to primary color and borderActive.
- [x] `UT-010-05`: Clock formatting respects 12H/24H formats.
- [x] `UT-010-06`: SettingsViewModel reacts to repository state and updates accent color, speed, panel visibility, clock style, and resets to defaults.

---

## Files Created / Modified

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/kotlin/dev/ragnar/aegis/domain/settings/UserPreferences.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/settings/PreferencesRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/settings/DataStorePreferencesRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/settings/SettingsViewModel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/settings/SettingsSheet.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Color.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/AegisTheme.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/TopBar.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/ClockViewModel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarDisplay.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/domain/settings/UserPreferencesTest.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/ui/settings/SettingsViewModelTest.kt`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| 2026-11-21 | `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL (26s) | APK generated successfully |
| 2026-11-21 | `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (18s) | 58 tests passed (11 new customization tests) |
| 2026-11-21 | `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (55s) | 0 errors |
