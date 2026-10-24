# REQUIREMENTS.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

---

## 1. Functional Requirements

### FR-001: Launcher Registration
- The application must declare itself as a valid Android home-screen launcher.
- The application must appear in the "Select Home App" dialog.
- The application must start when the home button is pressed after selection as the default.

### FR-002: Home Screen Display
- The home screen must display a live digital clock updating every second.
- The home screen must display the current date.
- The home screen must display a system label (AEGIS / RAGNAR).
- The home screen must display quick-launch application slots.
- The home screen must provide access to the application drawer.

### FR-003: Battery Telemetry
- The launcher must display the current battery percentage.
- The launcher must indicate the charging state (charging, full, discharging).
- The launcher must display battery temperature when available from the system.
- Battery data must update within 5 seconds of a state change.
- Battery data must be read from the Android BatteryManager, not estimated.

### FR-004: Application Discovery
- The launcher must query and display all user-launchable applications.
- The launcher must display each application's icon and name.
- The launcher must launch applications in their own task.
- The launcher must update the application list when packages are installed or removed.
- The launcher must handle missing or disabled launch intents gracefully.

### FR-005: Application Search
- The launcher must filter the application list by name as the user types.
- The search field must clear on dismiss.
- An empty query must show all applications.
- A no-results state must be visually communicated.

### FR-006: Navigation and Lifecycle
- Pressing the back key from the home screen must not close the launcher.
- The launcher must resume correctly after an application returns to it.
- The launcher must handle configuration changes (rotation) without crashing.
- The launcher must rebuild its state correctly after process recreation.

---

## 2. Non-Functional Requirements

### NFR-001: Performance
- The home screen must render within 1 second of a cold start on the target device.
- Scroll in the app drawer must maintain 60fps on the target device.
- The radar animation must not cause perceptible UI jank.
- Recompositions must be minimized; use stable keys and `remember` appropriately.

### NFR-002: Battery Consumption
- Background battery reading must use only the sticky broadcast (no polling service).
- Network monitoring must use the registered `NetworkCallback` (no polling).
- Radar animation can be paused in battery-saver mode.
- No wakelocks are held by the launcher.

### NFR-003: Privacy
- No user data is transmitted to any server.
- No location data is collected.
- No usage statistics are collected.
- Telemetry is read and displayed on-device only.

### NFR-004: Security
- No hardcoded credentials, API keys, or personal information in the codebase.
- No root access assumed or required.
- No accessibility service used without documented and justified user consent.
- Permissions are requested only when the feature requiring them is invoked.

### NFR-005: Reliability
- The launcher must not crash due to unavailable telemetry values.
- The launcher must handle `null` or empty app lists without crashing.
- The launcher must function without an internet connection.
- All known exceptions from Android platform APIs must be caught and handled.

### NFR-006: Compatibility
- Minimum Android version: 8.0 (API 26).
- Target Android version: Android 15 (API 35).
- The application must handle both gesture navigation and button navigation.
- The application must handle both portrait and landscape orientations, or document its orientation lock policy.

### NFR-007: Accessibility
- All interactive elements must have a `contentDescription`.
- Minimum touch target: 48dp × 48dp.
- Text must be readable at system font scale up to 130%.
- Animations must be disableable via a user setting.
- No information conveyed by color alone.

### NFR-008: Maintainability
- No hardcoded colors, strings, or dimensions in composable functions.
- All colors, fonts, and spacings accessed through design tokens.
- Code comments explain non-obvious decisions.
- Each file should have a single clear responsibility.

### NFR-009: Testability
- ViewModel logic must be testable without an Android device.
- Repository layer must expose `Flow` interfaces testable with fake implementations.
- Pure computation (sorting, filtering) must be testable with JVM unit tests.

---

## 3. Permissions

| Permission | Required For | When Requested |
|---|---|---|
| (none) | App discovery via queries manifest | Declared in manifest; no runtime request |
| `ACCESS_NETWORK_STATE` | Network connectivity telemetry | Declared in manifest; no runtime request |
| `RECEIVE_BOOT_COMPLETED` | Optional: re-register as launcher on boot | Declared in manifest if needed |
| `CAMERA` | Flashlight control (Phase 8+) | At runtime when flashlight feature is first used |
| `VIBRATE` | Optional haptic feedback | Declared in manifest; no runtime request |

**Not requested:**
- `ACCESS_FINE_LOCATION` — not needed
- `READ_CONTACTS` — not needed
- `QUERY_ALL_PACKAGES` — use targeted `<queries>` instead
- `WRITE_SETTINGS` — not used; intent-based for system settings

---

## 4. Android API Constraints

| Feature | Android Version Constraint | Notes |
|---|---|---|
| Package visibility | API 30+ (Android 11) requires `<queries>` | Handled in manifest |
| `RoleManager.ROLE_HOME` | API 29+ | Used for default launcher selection; fallback for API 26-28 |
| `BatteryManager.BATTERY_PROPERTY_*` | API 21+ | Safe for minSdk 26 |
| `ConnectivityManager.registerNetworkCallback` | API 21+ | Safe for minSdk 26 |
| `CameraManager.setTorchMode` | API 23+ | Safe for minSdk 26 |
| Edge-to-edge display | API 30+ best support | `enableEdgeToEdge()` degrades gracefully |
| Wi-Fi toggle via API | Deprecated API 29+ | Intent-only; documented limitation |
| Bluetooth toggle via API | Deprecated API 33+ | Intent-only; documented limitation |

---

## 5. Assumptions

- The project is developed for personal use on one physical Android device.
- The target device runs Android 10 or higher.
- The development environment is NixOS with Android Studio (Toolbox), JDK 21, and Android SDK API 35.
- No app signing configuration is required in Phase 1; debug builds are sufficient.
- No Play Store submission is planned in Phase 1-14.
- No CI/CD pipeline is required for Phase 1-13.
