# CHANGELOG.md — AEGIS HUD Launcher

---

## [Unreleased] — Development

### Phase 13 — Context-Aware HUD — 2026-11-22

**Added:**
- `ContextModels.kt`: sealed interfaces `ContextTrigger` (TimeWindow, BatteryLevelBelow, PowerSaveActive, NetworkDisconnected) and `ContextAction` (SetAccent, SetAnimationSpeed, ShowAlert); `ContextRule`, `ContextSnapshot`, `ContextEvaluationResult` domain models.
- `StandardContextRules` object with 5 predefined protocols: `LOW_BATTERY` (p=100, Crimson + animation off), `POWER_SAVE` (p=80, Slow), `NETWORK_OFFLINE` (p=50, alert banner), `NIGHT_OPS` (p=40, Amber), `MORNING_PROTOCOL` (p=30, Green).
- `ContextRuleEvaluator` pure Kotlin evaluation engine: priority-ordered, deterministic, midnight wrap-around support, no background service, zero location API usage.
- `TacticalAlertBanner` composable: pulsing cyan/warning alert overlay shown on home screen when an alert protocol is active.
- `UserPreferences` extended with `contextAwareHudEnabled: Boolean` and `enabledContextRules: Set<String>` with DataStore persistence.
- `PreferencesRepository` extended with `setContextAwareHudEnabled`, `toggleContextRule`, `setEnabledContextRules`.
- `SettingsViewModel` extended with `setContextAwareHudEnabled` and `toggleContextRule`.
- `SettingsSheet` Context-Aware HUD section: master toggle, per-protocol enable toggles showing live `ACTIVE`/`STANDBY` status.
- `HomeScreen` wired: context snapshot built from live telemetry + clock, evaluated on every relevant state change, effective accent/speed overrides applied, alert banner displayed.
- 11 new unit tests in `ContextRuleEvaluatorTest` covering time-based, battery, power-save, network, priority, and master-toggle scenarios (98 total project tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (2m 7s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (31s, 98 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (52s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 12 — Developer Console — 2026-11-22

**Added:**
- Real-time `DeveloperConsoleScreen` cyber-tactical HUD overlay accessible via `SettingsSheet` ("OPEN DEVELOPER CONSOLE [>]").
- Genuine JVM runtime memory diagnostics (`Runtime.getRuntime()`) showing allocated heap, maximum heap, heap utilization percentage, and launchable/favorite app counts.
- Real system hardware and OS platform audit panel displaying manufacturer, model, build ID, Android version, SDK API level, security patch level, and CPU ABI.
- Permission audit panel inspecting manifest permissions (`ACCESS_NETWORK_STATE`, `CAMERA`, etc.) reporting live granted/denied statuses.
- In-memory circular log ring buffer (`AegisLogBuffer`) capturing up to 100 recent launcher events across `DEBUG`, `INFO`, `WARN`, `ERROR` levels with zero disk I/O.
- Log level filter tabs (`ALL`, `DEBUG`, `INFO`, `WARN`, `ERROR`) and log clear action in the console.
- Formatted markdown report generation and one-tap clipboard copy (`COPY DIAGNOSTICS`) with visual confirmation toast.
- 8 new unit tests across `AegisLogBufferTest`, `DiagnosticsSnapshotTest`, and `DeveloperConsoleViewModelTest` (87 total project tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (22s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (17s, 87 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (1m 6s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 11 — Application Organization — 2026-11-22

**Added:**
- Persistent application pinning/favoriting backed by DataStore Preferences (`favoriteApps: Set<String>`).
- Long-press on app drawer cells to pin/unpin apps with haptic/tactile feedback and glowing cyan star badges (`★`).
- Dedicated "PINNED // QUICK LAUNCH" section at the top of the App Drawer displaying pinned favorites with instant 1-tap launching.
- Dynamic sort order system (`AppSortOrder`): Alphabetical (A-Z), Alphabetical (Z-A), Favorites First, and Recently Installed.
- Package install timestamp tracking (`AppInfo.installTimeMillis`) for installation date sorting.
- Configurable drawer grid layout with 3, 4, or 5 columns.
- Interactive header controls in the App Drawer to cycle sort modes (`[SORT: A-Z]`) and grid density (`[N COL]`).
- App Drawer configuration section in `SettingsSheet` for sort selection, column count, and favorites clearing.
- 13 new unit tests across sorting, preferences, and ViewModel state flow (79 total project tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (27s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (13s, 79 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (1m 6s, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

### Phase 10 — Full-Screen AI Command Interface — 2026-11-21

**Added:**
- Redesigned the AEGIS home screen into a full-screen futuristic AI command interface.
- Created `AICoreVisualization` full-screen Canvas neural brain visualization matching the anatomical design reference image with 7 visual layers: surrounding tech HUD rings & indicators, horizontal flowing wave ribbons, downward holographic projection cone with 14 fanning ray lines, 3D perspective concentric energy platform with central white-cyan hotspot core, 3D anatomical brain neural mesh (66+ nodes across 7 lobes, geodesic proximity edges, edge-traveling data pulses), upper glowing measurement arc with radial ticks, and ambient floating particles.
- Added `HudCornerBrackets` framing the screen corners with 4 tactical cyan L-brackets.
- Built crisp Canvas vector micro-icons in `HudIcons.kt` (`HudCubeIcon`, `HudCpuIcon`, `HudRamIcon`, `HudTempIcon`, `HudBatteryIcon`, `HudIntelligenceIcon`, `HudTelemetryIcon`, `HudVoltageIcon`, `HudClockIcon`, `HudStackIcon`).
- Implemented `SegmentedProgressBar` with 8 horizontal rectangular blocks for CPU, RAM, TEMP, and BATT indicators.
- Implemented `NeuralGeometry` cached geometry generator for anatomical human brain coordinates, geodesic edges preserving cortical fissures, and platform dimensions.
- Implemented `NeuralAnimationState` with isolated `InfiniteTransition` animations; accepting only stable parameters to isolate visualization from floating panel recompositions.
- Created base `FloatingHudPanel` container with semi-transparent dark fill (`#031422` at 82% alpha), subtle glowing cyan borders (`#00E5FF`), and rounded corners.
- Implemented 7 floating panels:
  - `FloatingCorePanel` (top-left: isometric cube icon + `AEGIS AI CORE ////` + version `v0.9.4-α`)
  - `FloatingAiStatusPanel` (top-right: `AI STATUS` with emerald green `● IDLE` / PROCESSING / ACTIVE real states)
  - `FloatingPerformancePanel` (left: CPU & RAM utilization with 8-segment progress bars)
  - `FloatingDevicePanel` (right: Temperature & Battery percentage with 8-segment progress bars)
  - `FloatingIntelligencePanel` (lower-left: CONTEXT, VOICE, VISION, AUTOMATION with green checkmarks)
  - `FloatingTelemetryPanel` (lower-right: Battery temperature, uptime, voltage with micro-icons)
  - `FloatingSystemStatusPanel` (bottom: Network connectivity, download/upload speeds, storage status)
- Created `AppDrawerGestureIndicator` with double-stacked chevrons, flanking horizontal gradient lines, and breathing animation (`∧∧ SWIPE UP TO OPEN APP DRAWER`).
- Implemented upward vertical drag gesture detection (`detectVerticalDragGestures`) to open drawer smoothly from anywhere on home screen.
- Removed old bottom launcher dock, app shortcut row, search bar, APPS button, and stacked cards from home screen.
- Added `SystemPerformanceState` and `IntelligenceState` domain models; implemented `AndroidSystemInfoRepository` reading real RAM, storage, CPU, and hardware capabilities.
- Enhanced `TelemetryViewModel` to provide unified state for all floating panels.
- Added 8 new JVM unit tests across `NeuralGeometryTest` and `FloatingPanelFormatterTest` (66 total project tests passing).

**Validation:**
- `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (16s).
- `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (23s, 66 tests passed across all suites).
- `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (1m, 0 errors).
- Device testing deferred pending physical device / emulator (B-002).

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
