# PHASE-06 — Animated Radar and Technical HUD Graphics

**Feature:** F-007 (Animated Radar and HUD Graphics)
**Priority:** P1
**Dependencies:** PHASE-02 (HUD Design System), PHASE-03 (HUD Home Screen)
**Status:** Implementation Complete — Build Verified (Device Validation Pending)
**Started:** 2026-11-21
**Completed:** 2026-11-21 (Build Verified)

---

## Objective

Implement technical HUD graphics on the home screen: an animated rotating radar graphic
drawn via Compose Canvas with concentric rings, measurement tick marks, crosshair reticle,
rotating sweep sector, technical scan overlay, configurable rotation speed, and automatic
pause/throttle in battery-saver mode.

---

## Sub-Phases

| ID | Title | Status |
|---|---|---|
| F-007-P1 | Radar rings and tick marks (Canvas composable) | Implemented — Build Verified |
| F-007-P2 | Rotating sweep animation | Implemented — Build Verified |
| F-007-P3 | Grid and scan-line overlay | Implemented — Build Verified |
| F-007-P4 | Configurable animation speed | Implemented — Build Verified |
| F-007-P5 | Pause/reduce on low-power or battery-saver mode | Implemented — Build Verified |

---

## Acceptance Criteria

- [x] Radar renders at consistent frame rate without jank (`RadarDisplay` Canvas).
- [x] Concentric rings, degree ticks, and center reticle render per `UI_UX_SPECIFICATION.md` Section 6.
- [x] Rotating sweep sector animates continuously with infinite linear rotation (default 4s per revolution).
- [x] Animation can be slowed or paused (via `rotationDurationMillis` and `isPaused` parameters).
- [x] Scan-line overlay composable translates vertically with subtle alpha (`ScanLineOverlay`).
- [x] Automatically pauses or throttles when battery-saver mode is active (`PowerManager.ACTION_POWER_SAVE_MODE_CHANGED` receiver).
- [x] Pure geometry and configuration math verified by JVM unit tests (7 tests in `RadarMathTest`).

---

## Unit Tests

- [x] Polar coordinate to Cartesian conversion produces expected offsets.
- [x] Angle normalization keeps values within `[0°, 360°)`.
- [x] Tick start/end coordinates match outer boundary and tick lengths.
- [x] Sweep angle interpolator maps animation progress `[0, 1]` to `[0°, 360°]`.

---

## Files Created

- `docs/phases/PHASE-06-HUD-GRAPHICS.md`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarMath.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarDisplay.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/ScanLine.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/ui/components/RadarMathTest.kt`

## Files Modified

- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| 2026-11-21 | `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL (22s) | APK generated successfully |
| 2026-11-21 | `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (16s) | 26 tests passed (7 new RadarMath tests) |
| 2026-11-21 | `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (34s) | 0 errors |
