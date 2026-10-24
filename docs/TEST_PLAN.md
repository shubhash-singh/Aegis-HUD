# TEST_PLAN.md — AEGIS HUD Launcher

**Version:** 0.1.1-planning
**Last Updated:** 2026-10-25 (Correction Task)

---

## 1. Testing Strategy

### Test Levels

| Level | Description | Location | Tool | Phase Required |
|---|---|---|---|---|
| Unit Tests | Test pure Kotlin logic: filtering, sorting, data mapping | `app/src/test/` | JUnit 4, kotlinx-coroutines-test | Phase 1+ |
| ViewModel Tests | Test ViewModel state changes with `runTest` and a `StandardTestDispatcher` or `UnconfinedTestDispatcher` | `app/src/test/` | JUnit 4, kotlinx-coroutines-test | Phase 2+ |
| Compose UI Tests | Test composables in isolation | `app/src/androidTest/` | ComposeTestRule | Phase 3+ |
| Instrumentation Tests | Test Activity, manifest, and Android framework adapters | `app/src/androidTest/` | AndroidX Test | Phase 1+ |
| Integration Tests | Test repository + ViewModel together with fake platform | `app/src/test/` | JUnit 4 | Phase 4+ |
| Device Tests | Full end-to-end on physical device | Manual + adb | Manual | Phase 14 |
| Regression Tests | Re-run all tests after major changes | Both | All tools | Phase 13+ |

---

## 2. Test Requirements by Feature

### F-001: Launcher Foundation
- [ ] `IT-001-01`: Activity starts without crash (instrumentation test).
- [ ] `IT-001-02`: Back press on home screen does not finish Activity (instrumentation test).
- [ ] `IT-001-03`: Activity recreation does not lose critical launcher state (instrumentation test).

### F-002: HUD Design System
- [ ] `CT-002-01`: `AegisTheme` composable renders without crash (Compose instrumentation test).
- [ ] `UT-002-02`: Color tokens are not null.
- [ ] `CT-002-03`: `HudPanel` composable renders with and without content (Compose instrumentation test).

### F-003: HUD Home Screen
- [ ] `UT-003-01`: Clock displays correct time format (unit test on formatting function).
- [ ] `VT-003-02`: Clock updates after one second (ViewModel test with `runTest` and a test dispatcher).
- [ ] `UT-003-03`: Date label displays correct date string (unit test).
- [ ] `CT-003-04`: Home screen renders without crash (Compose instrumentation test).

### F-004: Application Discovery
- [ ] `IT-004-01`: PackageManager adapter maps `ResolveInfo` into a platform-neutral `AppInfo` (instrumentation test).
- [ ] `UT-004-02`: App list sorts alphabetically (unit test).
- [ ] `UT-004-03`: Duplicate entries are removed (unit test).
- [ ] `UT-004-04`: Empty package list produces empty `AppInfo` list (unit test).
- [ ] `UT-004-05`: `AppRepository` emits updated list on package change (ViewModel test).

### F-005: Application Search
- [ ] `UT-005-01`: Filter by exact name returns correct app (unit test).
- [ ] `UT-005-02`: Filter by partial name (case-insensitive) returns matches (unit test).
- [ ] `UT-005-03`: Empty query returns all apps (unit test).
- [ ] `UT-005-04`: Query with no matches returns empty list (unit test).

### F-006: Battery Telemetry
- [ ] `IT-006-01`: Battery adapter extracts values from `Intent.ACTION_BATTERY_CHANGED` extras (instrumentation test).
- [ ] `UT-006-02`: Platform-neutral battery values map to charging state correctly (unit test).
- [ ] `UT-006-03`: Temperature converts from tenths of degrees to degrees (unit test).
- [ ] `UT-006-04`: Missing platform-neutral values produce safe defaults (unit test).

### F-007: Animated Radar
- [ ] `CT-007-01`: Radar composable renders without crash (Compose instrumentation test).
- [ ] `CT-007-02`: Animation does not throw exceptions (Compose instrumentation test).

### F-008: System Telemetry
- [ ] `IT-008-01`: Network adapter maps `NetworkCapabilities` correctly (instrumentation test).
- [ ] `UT-008-02`: Fake network source produces the correct offline `NetworkState` (unit test).

### F-009: Quick Actions
- [ ] `UT-009-01`: Flashlight action calls a fake `TorchController` (unit test).
- [ ] `DT-009-02`: Settings intent resolves on device (device test).

### F-010: Customization / DataStore
- [ ] `UT-010-01`: Default preferences produce expected values (unit test with in-memory DataStore).
- [ ] `UT-010-02`: Saved accent color is read back correctly (unit test).
- [ ] `UT-010-03`: Animation speed setting persists (unit test).

### F-011: Application Organization
- [ ] `UT-011-01`: Favorite app list persists via DataStore (unit test with in-memory DataStore).
- [ ] `UT-011-02`: Sort order logic sorts alphabetically by default (unit test).
- [ ] `UT-011-03`: Quick-launch slot configuration reads back correctly (unit test).
- [ ] `UT-011-04`: Removing an app from favorites updates the list (unit test).

### F-012: Widget Hosting (feasibility investigation phase only; implementation tests TBD after F-012-P1)
- [ ] `UT-012-01`: Feasibility investigation report exists and documents API constraints (manual check).

### F-013: Context-Aware HUD
- [ ] `UT-013-01`: Rule evaluation returns correct action for time-based trigger with mock time input (unit test).
- [ ] `UT-013-02`: Rule evaluation returns correct action for battery-level trigger (unit test).
- [ ] `UT-013-03`: No rule configured produces default HUD state (unit test).
- [ ] `UT-013-04`: Rule persistence reads back correctly (unit test with in-memory DataStore).

### F-014: Developer Console
- [ ] `UT-014-01`: Diagnostic data model maps correctly from mock inputs (unit test).
- [ ] `UT-014-02`: Log buffer ring correctly discards oldest entries when full (unit test).
- [ ] `DT-014-01`: Developer console screen displays real values on device (device test).

### F-015: Gesture Navigation (investigation phase only; implementation tests TBD after F-015-P1)
- [ ] `UT-015-01`: Investigation report documents gesture conflict analysis (manual check).

### F-016: Reliability and Performance
- [ ] `PT-016-01`: Cold start to visible home screen: <= 1 second on target device (device perf test).
- [ ] `PT-016-02`: Drawer scroll: sustained 60fps on target device (device perf test).
- [ ] `PT-016-03`: Idle battery drain: <= 1% per hour measured over 1 hour (device battery test).
- [ ] `PT-016-04`: No memory leak from BroadcastReceiver after 30 minutes idle (device memory test).
- [ ] `PT-016-05`: Kill-and-restart cycle: launcher restarts cleanly after process kill (device test).
- [ ] `RT-016-01`: Full unit test regression: ./gradlew testDebugUnitTest passes with no failures.
- [ ] `RT-016-02`: Lint regression: ./gradlew lintDebug produces no errors.

---

## 2b. Manifest Attribute Tests (F-001 Extension)

The following tests verify the manifest attributes accepted in DEC-010:

| Test ID | Description | Method | Phase |
|---|---|---|---|
| `DT-MANIFEST-01` | Pressing Home repeatedly does not stack multiple launcher instances | Manual on device | Phase 1 |  
| `DT-MANIFEST-02` | AEGIS does not appear in the Recents (Overview) screen | Manual on device | Phase 1 |
| `DT-MANIFEST-03` | After `adb shell am kill dev.ragnar.aegis`, pressing Home restarts launcher cleanly | adb + manual | Phase 1 |
| `DT-MANIFEST-04` | On launcher restart, no crash due to null bundle in onCreate | Manual observation | Phase 1 |

---

## 3. Acceptance Criteria Checklist (for Initial Release)

### Must Pass Before Phase 14

> **Note:** Phase 0 completion does not imply any of these tests have passed. All items below are pending until the relevant implementation phase is complete and validated.

- [ ] `AC-LAUNCH-01`: AEGIS appears in "Select Home App" dialog.
- [ ] `AC-LAUNCH-02`: Home button opens AEGIS after selection as default.
- [ ] `AC-LAUNCH-03`: Back press on home screen does not close launcher.
- [ ] `AC-LAUNCH-04`: AEGIS does not appear in the Android Recents screen.
- [ ] `AC-LAUNCH-05`: Repeated Home presses do not stack multiple launcher instances.
- [ ] `AC-CLOCK-01`: Clock shows correct time and updates every second.
- [ ] `AC-BATTERY-01`: Battery percentage matches Android status bar within +/-1%.
- [ ] `AC-APPS-01`: All user-launchable apps appear in drawer.
- [ ] `AC-APPS-02`: Tapping an app in the drawer launches it correctly.
- [ ] `AC-SEARCH-01`: Typing filters the app list in real time.
- [ ] `AC-LIFECYCLE-01`: Rotating the device does not crash the launcher.
- [ ] `AC-LIFECYCLE-02`: Pressing home from another app returns to AEGIS home screen.
- [ ] `AC-LIFECYCLE-03`: Kill and restart via adb produces a clean launcher start.
- [ ] `AC-BUILD-01`: `./gradlew assembleDebug` succeeds without errors.
- [ ] `AC-UNIT-01`: `./gradlew testDebugUnitTest` passes with no failures.
- [ ] `AC-LINT-01`: `./gradlew lintDebug` produces no errors (warnings acceptable).

---

## 4. Test Execution Record

| Date | Command | Result | Notes | Phase |
|---|---|---|---|---|
| 2026-10-25 | (not yet run) | Pending | No code yet; Phase 0 only | 0 |

---

## 5. Device Test Procedure (Phase 14)

1. Build debug APK: `./gradlew assembleDebug`.
2. Install: `./gradlew installDebug` or `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
3. Open Settings > Apps > Default Apps > Home App.
4. Select AEGIS.
5. Press home button.
6. Verify: HUD renders, clock ticks, battery shows real value.
7. Launch 5 different applications from the drawer.
8. Return to home screen each time via home button.
9. Verify: app list updates when a new app is installed during testing.
10. Verify: search filters correctly on device.
11. Verify: rotate device, launcher does not crash.
12. Verify: kill launcher process via adb, press home, launcher restarts cleanly.

---

## 6. Known Testing Limitations

- Unit tests for `PackageManager` and `BroadcastReceiver` require instrumented tests or fakes; JVM unit tests cannot test real Android APIs.
- Android framework types such as `Intent`, `ResolveInfo`, `NetworkCapabilities`, and `CameraManager` must be tested through instrumentation or isolated behind platform-neutral adapters.
- Compose UI tests require an Android emulator or device.
- Network callback behavior cannot be fully tested in unit tests.
- Radar animation performance must be assessed on a real device.
- Battery telemetry accuracy must be verified against the Android status bar on a real device.
