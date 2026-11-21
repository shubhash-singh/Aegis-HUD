# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21 (Phase 8 build verified)
**Current Phase:** PHASE-08 — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-09 — Customization (F-010)

---

## Completed This Session

- Implemented **PHASE-07 (System & Battery Telemetry Dashboard — F-006 & F-008)**:
  - Declared `ACCESS_NETWORK_STATE` in `AndroidManifest.xml`.
  - Created `BatteryState` and `NetworkState` domain models with real throughput metrics.
  - Implemented `BatteryNormalizer` and `NetworkNormalizer` pure mapping and bandwidth calculation engines.
  - Implemented `AndroidBatteryRepository` using `Intent.ACTION_BATTERY_CHANGED` sticky broadcast.
  - Implemented `AndroidNetworkRepository` using `ConnectivityManager.NetworkCallback` and lifecycle-scoped `TrafficStats` delta sampling.
  - Created `TelemetryViewModel` combining battery, network, bandwidth, and uptime flows into UI state.
  - Added real-time network speed formatted with user-specified arrows (`WIFI - 190kb/s↓ 20kb/s↑`) next to network status.
  - Wired live telemetry into `HomeScreen`'s System Status and Telemetry panels.
  - Added 15 unit tests in `BatteryNormalizerTest` and `NetworkNormalizerTest`.
  - Validated: `assembleDebug`, `testDebugUnitTest` (41 passed), `lintDebug` (0 errors).

- Implemented **PHASE-08 (Quick Actions — F-009)**:
  - Created `TorchController` interface and `AndroidTorchController` using `CameraManager.setTorchMode`.
  - Created `QuickActionsViewModel` with real torch ON/OFF state tracking and error handling.
  - Created `QuickActionsPanel` composable with four HUD-styled buttons: ⚙ Settings, Torch, ⌘ Wi-Fi, ✦ Bluetooth.
  - Integrated `QuickActionsPanel` into `HomeScreen`.
  - Added 6 unit tests in `QuickActionsViewModelTest` (47 total project unit tests passing).
  - Validated: `assembleDebug` (44s), `testDebugUnitTest` (37s, 47 passed), `lintDebug` (81s, 0 errors).

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 8
   (app discovery, search, radar 60fps, live telemetry, quick actions torch toggle and settings intents). (Low severity; development can proceed).

## Exact Next Task

Begin **PHASE-09 (Customization — F-010)**:
1. Review `docs/FEATURE_PLAN.md` for F-010 (Customization) scope:
   - F-010-P1: DataStore preferences setup
   - F-010-P2: Accent color selection
   - F-010-P3: Panel visibility toggles
   - F-010-P4: Animation speed setting
   - F-010-P5: Clock style selection
2. Create `docs/phases/PHASE-09-CUSTOMIZATION.md`.
3. Set up DataStore Preferences dependency (verify in `libs.versions.toml`).
4. Implement `PreferencesRepository` and `SettingsViewModel`.
5. Create `SettingsScreen` composable with HUD styling.
6. Wire settings into the HUD (animation speed → radar, accent color → theme).
7. Add unit tests and verify via Gradle.

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-08-QUICK-ACTIONS.md`
- `docs/FEATURE_PLAN.md` (F-010: Customization)
- `docs/UI_UX_SPECIFICATION.md`
- `gradle/libs.versions.toml` (DataStore dependency)

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
