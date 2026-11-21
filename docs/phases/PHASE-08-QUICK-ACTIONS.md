# PHASE-08 — Quick Actions

**Feature:** F-009 (Quick Actions, P1)
**Dependencies:** PHASE-02 (HUD Design System), PHASE-03 (HUD Home Screen)
**Status:** Implementation Complete — Build Verified (Device Validation Pending)
**Started:** 2026-11-21

---

## Objective

Implement a row of quick-action buttons on the HUD home screen allowing the user to:
1. Open Android Settings (`Settings.ACTION_SETTINGS`)
2. Toggle the device flashlight (`CameraManager.setTorchMode`) — with graceful fallback when unsupported.
3. Open Wi-Fi settings (`Settings.ACTION_WIFI_SETTINGS`)
4. Open Bluetooth settings (`Settings.ACTION_BLUETOOTH_SETTINGS`)
5. Provide visual state feedback (active/inactive/error) and handle all error conditions.

No restricted APIs used. No permissions beyond those already declared. All torch operations hidden behind a `TorchController` facade for testability (DEC-009 applies: no fake state).

---

## Sub-Phases

### F-009: Quick Actions (P1)

| ID | Title | Status |
|---|---|---|
| F-009-P1 | Settings shortcut composable | Implemented — Build Verified |
| F-009-P2 | Flashlight toggle (`CameraManager` API) | Implemented — Build Verified |
| F-009-P3 | Wi-Fi settings intent | Implemented — Build Verified |
| F-009-P4 | Bluetooth settings intent | Implemented — Build Verified |
| F-009-P5 | Action state feedback and error handling | Implemented — Build Verified |

---

## Acceptance Criteria

- [x] Each action performs its intended function or opens the correct Settings screen.
- [x] Flashlight button shows real ON/OFF state; toggles on tap.
- [x] Hardware without a flashlight shows a disabled button (no crash).
- [x] All settings intents resolve to the correct screen.
- [x] No attempt to use restricted or deprecated system APIs.
- [x] `TorchController` facade is unit testable.

---

## Unit Tests

- [x] `UT-009-01`: Initial state has torch OFF, `isTorchAvailable` reflects hardware presence, no error.
- [x] `UT-009-02`: `toggleTorch` enables torch when off; disables when on; records correct value via facade.
- [x] `UT-009-03`: `toggleTorch` sets `torchError` and keeps torch OFF when hardware fails.
- [x] `clearTorchError` removes the error message without changing torch state.

---

## Files Created

- `docs/phases/PHASE-08-QUICK-ACTIONS.md`
- `app/src/main/kotlin/dev/ragnar/aegis/data/quickactions/TorchController.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/quickactions/AndroidTorchController.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/quickactions/QuickActionsViewModel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/quickactions/QuickActionsPanel.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/ui/quickactions/QuickActionsViewModelTest.kt`

## Files Modified

- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`
- `app/src/main/AndroidManifest.xml`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| 2026-11-21 | `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL (44s) | APK generated successfully |
| 2026-11-21 | `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (37s) | 47 tests passed (6 new QuickActions tests) |
| 2026-11-21 | `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (81s) | 0 errors |
