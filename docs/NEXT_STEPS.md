# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21 (Phase 6 build verified)
**Current Phase:** PHASE-06 — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-07 — System Telemetry Dashboard / F-006 Battery Telemetry

---

## Completed This Session

- Implemented **PHASE-06 (Animated Radar and Technical HUD Graphics)**:
  - Created `RadarMath` pure Kotlin geometric utilities.
  - Created `RadarDisplay` Compose Canvas component with range rings, measurement ticks, center reticle, rotating sweep sector, and azimuth readout.
  - Created `ScanLineOverlay` translating vertically with soft bloom.
  - Integrated `RadarDisplay` and `ScanLineOverlay` into `HomeScreen`.
  - Added dynamic `PowerManager.ACTION_POWER_SAVE_MODE_CHANGED` receiver to pause animations automatically when battery-saver mode is active.
  - Added 7 unit tests in `RadarMathTest` (26 total project unit tests passing).
- Validated Phase 6 implementation:
  - `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (22s).
  - `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (16s, 26 tests passed with 0 failures).
  - `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (34s, 0 errors).
- Synchronized all project documentation (Progress Tracker, Feature Plan, Phase Log, Changelog, Phase 6 doc, Project Plan).

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4, 5, and 6
   (app discovery, live search, radar animation 60fps, and battery-saver pause). (Low severity; development can proceed).

## Exact Next Task

Begin **PHASE-07 (System Telemetry Dashboard)** or **F-006 (Battery Telemetry)**:
1. Review `docs/FEATURE_PLAN.md` for F-006 (Battery Telemetry) and F-008 (System Telemetry Dashboard).
2. Create the corresponding phase plan document in `docs/phases/`.
3. Implement `BatteryRepository` using `Intent.ACTION_BATTERY_CHANGED` sticky broadcast.
4. Implement `NetworkRepository` using `ConnectivityManager.NetworkCallback`.
5. Wire real battery and network telemetry into the HUD status and telemetry panels (replacing "---" placeholders per DEC-009).
6. Add unit tests and verify via Gradle.

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-06-HUD-GRAPHICS.md`
- `docs/FEATURE_PLAN.md` (F-006: Battery Telemetry, F-008: System Telemetry Dashboard)
- `docs/UI_UX_SPECIFICATION.md` (Section 2: Status Colors, Section 5: Telemetry Panels)

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
```
