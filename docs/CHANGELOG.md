# CHANGELOG.md — AEGIS HUD Launcher

---

## [Unreleased] — Development

### Phase 9 — Customization — 2026-11-21

**Added:**
- Persistent configuration management using AndroidX DataStore Preferences (`androidx.datastore:datastore-preferences:1.1.1`).
- Domain models `UserPreferences`, `AccentColor` (Cyan, Amber, Matrix Green, Tactical Crimson), and `AnimationSpeed` (Fast 2s, Normal 4s, Slow 8s, Disabled).
- `PreferencesRepository` interface and `DataStorePreferencesRepository` backing persistence.
- Dynamic `AegisColorScheme.withAccent(accent)` dynamically updating HUD colors across the entire UI.
- `SettingsViewModel` managing reactive preferences flow.
- Full-screen `SettingsSheet` technical HUD modal with theme selection, radar animation speed chips, panel visibility toggles, clock formatting, and system defaults reset.
- Live `HomeScreen` and `TopBar` integration with dynamic theming, panel conditionals, radar speed wiring, 12H/24H clock and seconds toggles.
- 11 JVM unit tests in `UserPreferencesTest` and `SettingsViewModelTest` (58 total project unit tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (26s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (18s, 58 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (55s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 8 — Quick Actions — 2026-11-21

**Added:**
- `TorchController` interface and `AndroidTorchController` implementation using `CameraManager.setTorchMode`. Checks for real back-facing camera flash; silently disabled when hardware unavailable — no crash.
- `QuickActionsViewModel` tracking real torch ON/OFF state and hardware-failure error.
- `QuickActionsPanel` composable: a row of four 48dp HUD-styled action buttons — ⚙ Settings, ○/◉ Torch, ⌘ Wi-Fi, ✦ Bluetooth. Torch button renders in muted style when unavailable; active state highlighted with `aegis.borderActive`. Inline error message on torch failure.
- All settings intents launched with `FLAG_ACTIVITY_NEW_TASK` (required from launcher task context).
- `QuickActionsPanel` integrated into `HomeScreen` between Telemetry and Quick Launch rows.
- 6 JVM unit tests in `QuickActionsViewModelTest` (47 total project unit tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (44s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (37s, 47 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (81s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 7 — System & Battery Telemetry Dashboard — 2026-11-21

**Added:**
- Live battery telemetry via sticky broadcast (`AndroidBatteryRepository`): charge level %, charging status (`CHG`, `DIS`, `FULL`), temperature in °C, voltage, health.
- Live network connectivity tracking via `ConnectivityManager.NetworkCallback` (`AndroidNetworkRepository`): Wi-Fi, Cellular, Ethernet, Offline.
- Pure parsing and normalization engines (`BatteryNormalizer`, `NetworkNormalizer`) with safe default handling.
- Real system uptime tracking via `SystemClock.elapsedRealtime()`.
- Declared `android.permission.ACCESS_NETWORK_STATE` in manifest.
- Real-time download and upload bandwidth monitoring via Android `TrafficStats` (`downBytesPerSecond`, `upBytesPerSecond`) formatted as `${TYPE} - ${down}↓ ${up}↑` (e.g. `WIFI - 190kb/s↓ 20kb/s↑`). Bandwidth sampling coroutine is active strictly while UI collects the flow.
- Wired live telemetry into `HomeScreen` status and telemetry panels (DEC-009 compliant).
- 15 JVM unit tests across `BatteryNormalizerTest` and `NetworkNormalizerTest` (41 total project unit tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (34s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (21s, 34 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (44s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 6 — Animated Radar and Technical HUD Graphics — 2026-11-21

**Added:**
- Animated technical radar graphic (`RadarDisplay`) rendered via Compose Canvas with concentric range rings, measurement ticks, center crosshair reticle, rotating sweep sector with trailing gradient, and live azimuth readout.
- Full-screen CRT scan-line overlay (`ScanLineOverlay`) with subtle glow.
- Pure geometric utility library (`RadarMath`) with polar conversions, tick vectors, and angle calculations.
- Automatic animation throttling/pausing when device battery-saver mode is active (`PowerManager.ACTION_POWER_SAVE_MODE_CHANGED`).
- 7 JVM unit tests in `RadarMathTest` (26 total project unit tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (22s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (16s, 26 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (34s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 5 — Application Search — 2026-11-21

**Added:**
- Real-time search filtering in `AppDrawer` with `AppListNormalizer.filter`.
- `HudSearchBar` composable with HUD styling, 48dp touch target clear button, and IME search action.
- Interactive search placeholder in bottom navigation bar that opens drawer with search focus.
- IME search action launches top result.
- Distinct no-results state (`NO APPLICATIONS MATCHING "<QUERY>"`).
- Search query automatically resets on drawer dismiss.
- 4 JVM unit tests for search filtering (`UT-005-01` through `UT-005-04`) in `AppListNormalizerTest`.

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (21s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (16s, 19 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (33s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 4 — App Discovery & Launching — 2026-11-21

**Added:**
- Targeted launcher-activity discovery, alphabetical de-duplication, icon caching, safe app
  launching, and package-change refresh.
- HUD app drawer grid and real Quick Launch entries.
- JVM coverage for app-list sorting, duplicate removal, and empty input (`AppListNormalizerTest`).

**Validation:**
- Resolved build environment blocker B-003 by patching the Gradle AAPT2 binary ELF interpreter for NixOS glibc.
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (41s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (31s, 15 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (53s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### [0.1.1-planning] — Planning Review Corrections — 2026-10-25

**Changed:**
- Added a verified Gradle 8.9 wrapper-bootstrap procedure and associated decision/risk handling.
- Corrected battery, widget-binding, animation, ViewModel-factory, test-boundary, and Phase 7 dependency documentation.
- Corrected the tracker commit reference and updated the planning review handoff.
- Updated the feature-plan total to 102 phases and the decision-log count to 12.
- Added the `reviews/` planning-audit and Phase 1 readiness reports.

### [0.1.0-dev] — Phase 0 Complete — 2026-10-25

**Added:**
- Full project planning documentation:
  - `AGENTS.md` — Agent session instructions and workflow rules
  - `README.md` — Project overview and setup guide
  - `docs/PROJECT_PLAN.md` — Full roadmap and milestones
  - `docs/FEATURE_PLAN.md` — 19 features across P0-P3 with 102 phases
  - `docs/ARCHITECTURE.md` — Layered architecture and package structure
  - `docs/TECH_STACK.md` — Technology choices and version catalog TOML
  - `docs/UI_UX_SPECIFICATION.md` — HUD visual design system specification
  - `docs/REQUIREMENTS.md` — Functional and non-functional requirements
  - `docs/TEST_PLAN.md` — Testing strategy and acceptance criteria
  - `docs/PROGRESS_TRACKER.md` — Authoritative project tracker
  - `docs/PHASE_LOG.md` — Chronological phase log
  - `docs/DECISIONS.md` — 12 architectural decisions recorded
  - `docs/BUG_TRACKER.md` — Bug tracking log (initially empty)
  - `docs/RISK_REGISTER.md` — 13 technical and platform risks documented
  - `docs/NEXT_STEPS.md` — Session handoff and Phase 1 task definition
  - `docs/phases/PHASE-00-PLANNING.md` — Phase 0 detail document
  - `docs/phases/PHASE-01-FOUNDATION.md` — Phase 1 detail document (plan only)

**Decided:**
- Min SDK 26, Target SDK 35
- Compose BOM 2024.11.00
- AGP 8.7.3 with Gradle 8.9
- Single `app` module for initial phases
- No DI framework for initial phases
- DataStore Preferences for settings
- No external image loading library initially
- No fake telemetry, ever

---

## [Future] — Planned

### [0.1.0-alpha] — Phase 1-3 Complete (Planned)
- Minimal launcher registered as home app
- HUD design system
- Live clock and battery telemetry

### [0.1.0-beta] — Phase 4-6 Complete (Planned)
- Application discovery and drawer
- Search
- Animated radar graphics

### [0.1.0] — Initial Personal Release (Planned)
- All P0 and P1 features complete
- Performance and reliability tested on physical device
- Documentation finalized
