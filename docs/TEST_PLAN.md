# TEST_PLAN.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

---

## 1. Testing Strategy

### Test Levels

| Level | Description | Location | Tool | Phase Required |
|---|---|---|---|---|
| Unit Tests | Test pure Kotlin logic: filtering, sorting, data mapping | `app/src/test/` | JUnit 4, kotlinx-coroutines-test | Phase 1+ |
| ViewModel Tests | Test ViewModel state changes using TestCoroutineDispatcher | `app/src/test/` | JUnit 4, turbine (optional) | Phase 2+ |
| Compose UI Tests | Test composables in isolation | `app/src/androidTest/` | ComposeTestRule | Phase 3+ |
| Integration Tests | Test repository + ViewModel together with fake platform | `app/src/test/` | JUnit 4 | Phase 4+ |
| Device Tests | Full end-to-end on physical device | Manual + adb | Manual | Phase 14 |
| Regression Tests | Re-run all tests after major changes | Both | All tools | Phase 13+ |

---

## 2. Unit Test Requirements by Feature

### F-001: Launcher Foundation
- [ ] `UT-001-01`: Activity starts without crash (Compose test).
- [ ] `UT-001-02`: Back press on home screen does not finish Activity (Compose test).
- [ ] `UT-001-03`: Process recreation does not lose critical state.

### F-002: HUD Design System
- [ ] `UT-002-01`: `AegisTheme` composable renders without crash (preview test).
- [ ] `UT-002-02`: Color tokens are not null.
- [ ] `UT-002-03`: `HudPanel` composable renders with and without content.

### F-003: HUD Home Screen
- [ ] `UT-003-01`: Clock displays correct time format (unit test on formatting function).
- [ ] `UT-003-02`: Clock updates after one second (ViewModel test with test dispatcher).
- [ ] `UT-003-03`: Date label displays correct date string (unit test).
- [ ] `UT-003-04`: Home screen renders without crash (Compose test).

### F-004: Application Discovery
- [ ] `UT-004-01`: `AppInfo` data class correctly maps from `ResolveInfo` (unit test).
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
- [ ] `UT-006-01`: `BatteryState` maps from `Intent` extras correctly (unit test).
- [ ] `UT-006-02`: Charging state correctly identified (unit test).
- [ ] `UT-006-03`: Temperature converts from tenths of degrees to degrees (unit test).
- [ ] `UT-006-04`: Missing extras produce safe default `BatteryState` (unit test).

### F-007: Animated Radar
- [ ] `UT-007-01`: Radar composable renders without crash (Compose test).
- [ ] `UT-007-02`: Animation does not throw exceptions (Compose test).

### F-008: System Telemetry
- [ ] `UT-008-01`: `NetworkState` maps from `NetworkCapabilities` correctly (unit test, if testable).
- [ ] `UT-008-02`: Offline state produces correct `NetworkState` (unit test with fake).

### F-009: Quick Actions
- [ ] `UT-009-01`: Flashlight toggle calls `setTorchMode` (unit test with mock CameraManager).
- [ ] `UT-009-02`: Settings intent resolves on device (device test).

### F-010: Customization / DataStore
- [ ] `UT-010-01`: Default preferences produce expected values (unit test with in-memory DataStore).
- [ ] `UT-010-02`: Saved accent color is read back correctly (unit test).
- [ ] `UT-010-03`: Animation speed setting persists (unit test).

---

## 3. Acceptance Criteria Checklist (for Initial Release)

### Must Pass Before Phase 14

- [ ] `AC-LAUNCH-01`: AEGIS appears in "Select Home App" dialog.
- [ ] `AC-LAUNCH-02`: Home button opens AEGIS after selection as default.
- [ ] `AC-LAUNCH-03`: Back press on home screen does not close launcher.
- [ ] `AC-CLOCK-01`: Clock shows correct time and updates every second.
- [ ] `AC-BATTERY-01`: Battery percentage matches Android status bar within ±1%.
- [ ] `AC-APPS-01`: All user-launchable apps appear in drawer.
- [ ] `AC-APPS-02`: Tapping an app in the drawer launches it correctly.
- [ ] `AC-SEARCH-01`: Typing filters the app list in real time.
- [ ] `AC-LIFECYCLE-01`: Rotating the device does not crash the launcher.
- [ ] `AC-LIFECYCLE-02`: Pressing home from another app returns to AEGIS home screen.
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
- Compose UI tests require an Android emulator or device.
- Network callback behavior cannot be fully tested in unit tests.
- Radar animation performance must be assessed on a real device.
- Battery telemetry accuracy must be verified against the Android status bar on a real device.
