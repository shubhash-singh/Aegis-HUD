# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21 (Phase 7 build verified)
**Current Phase:** PHASE-07 — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-08 — Quick Actions (F-009)

---

## Completed This Session

- Implemented **PHASE-07 (System & Battery Telemetry Dashboard — F-006 & F-008)**:
  - Declared `ACCESS_NETWORK_STATE` in `AndroidManifest.xml`.
  - Created `BatteryState` and `NetworkState` domain models.
  - Implemented `BatteryNormalizer` and `NetworkNormalizer` pure mapping engines.
  - Implemented `AndroidBatteryRepository` using `Intent.ACTION_BATTERY_CHANGED` sticky broadcast.
  - Implemented `AndroidNetworkRepository` using `ConnectivityManager.NetworkCallback`.
  - Created `TelemetryViewModel` combining battery, network, and uptime flows into UI state.
  - Wired live telemetry into `HomeScreen`'s System Status and Telemetry panels.
  - Added 8 unit tests in `BatteryNormalizerTest` and `NetworkNormalizerTest` (34 total project unit tests passing).
- Validated Phase 7 implementation:
  - `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (34s).
  - `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (21s, 34 tests passed with 0 failures).
  - `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (44s, 0 errors).
- Synchronized all project documentation (Progress Tracker, Feature Plan, Phase Log, Changelog, Phase 7 doc, Test Plan, Project Plan).

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 7
   (app discovery, search, radar 60fps, and live telemetry alignment with Android status bar). (Low severity; development can proceed).

## Exact Next Task

Begin **PHASE-08 (Quick Actions — F-009)**:
1. Review `docs/FEATURE_PLAN.md` for F-009 (Quick Actions) scope:
   - F-009-P1: Settings shortcut composable
   - F-009-P2: Flashlight toggle (`CameraManager` API)
   - F-009-P3: Wi-Fi settings intent
   - F-009-P4: Bluetooth settings intent
   - F-009-P5: Action state feedback and error handling
2. Create `docs/phases/PHASE-08-QUICK-ACTIONS.md`.
3. Implement `TorchController` facade and settings intent dispatchers.
4. Add quick action buttons to HUD home screen.
5. Add unit tests and verify via Gradle.

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-07-TELEMETRY.md`
- `docs/FEATURE_PLAN.md` (F-009: Quick Actions)
- `docs/UI_UX_SPECIFICATION.md`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
```
