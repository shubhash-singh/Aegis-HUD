# FEATURE_PLAN.md — AEGIS HUD Launcher

**Version:** 0.1.1-planning
**Last Updated:** 2026-10-25 (Correction Task)

> **Source of truth:** This file is the authoritative feature inventory for AEGIS HUD Launcher.
> Feature scope additions or removals must be made here first, then reflected in PROGRESS_TRACKER.md.

---

## Feature Inventory

### P0 — Essential Launcher Functionality

---

#### F-001: Android Launcher Foundation

**Priority:** P0
**Dependencies:** None
**Implementation Phase:** Phase 1
**Status:** Not Started

**Description:**
Register the application as a valid Android home-screen launcher. Implement the main launcher activity. Handle lifecycle events and recovery.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-001-P1 | Manifest declaration and launcher registration | Not Started |
| 2 | F-001-P2 | MainActivity implementation with Compose scaffold | Not Started |
| 3 | F-001-P3 | Lifecycle handling: back press, recents, resume | Not Started |
| 4 | F-001-P4 | Process recreation and configuration change handling | Not Started |
| 5 | F-001-P5 | First-run default launcher selection flow | Not Started |

**Acceptance Criteria:**
- App appears in the "Select Home App" dialog.
- App starts when the home button is pressed after selection.
- App resumes correctly after launching another app.
- Process recreation (rotation, memory pressure) does not crash the app.
- No internet connection is required for basic operation.

**Testing Requirements:**
- Unit: Activity starts without crash; back press does not finish Activity; process recreation does not lose critical state.
- Device: Appears in home app dialog; home button triggers AEGIS.

**Platform Notes:**
- `android.intent.category.HOME` and `android.intent.category.DEFAULT` required.
- `android.intent.action.MAIN` required.
- `RoleManager.ROLE_HOME` (API 29+) preferred; fallback to `ACTION_HOME_SETTINGS` for API 26-28.

---

#### F-002: HUD Design System

**Priority:** P0
**Dependencies:** F-001
**Implementation Phase:** Phase 2
**Status:** Not Started

**Description:**
Define the complete visual design system: color palette, typography, spacing, HUD panel components, status indicators, and layout primitives.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-002-P1 | Color palette and theme tokens (MaterialTheme + custom) | Not Started |
| 2 | F-002-P2 | Typography: monospaced technical typeface | Not Started |
| 3 | F-002-P3 | HUD panel and border composables | Not Started |
| 4 | F-002-P4 | Status indicator composables (green/amber/red) | Not Started |
| 5 | F-002-P5 | Responsive layout and inset handling | Not Started |

**Acceptance Criteria:**
- A static HUD shell renders on screen without crashes.
- Colors, fonts, and spacing match the UI/UX specification.
- System insets (status bar, navigation bar) are handled correctly.
- No hardcoded colors or dimensions in composables.

**Testing Requirements:**
- Unit: AegisTheme renders without crash; color tokens are non-null; HudPanel renders with and without content.

---

#### F-003: HUD Home Screen

**Priority:** P0
**Dependencies:** F-002
**Implementation Phase:** Phase 2 / 3
**Status:** Not Started

**Description:**
Create the main dashboard. Display a live digital clock, date, system status, quick-launch slots, and a path to the application drawer.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-003-P1 | Static dashboard layout | Not Started |
| 2 | F-003-P2 | Live digital clock composable | Not Started |
| 3 | F-003-P3 | Date and formatted system label | Not Started |
| 4 | F-003-P4 | Quick-launch application slots (static, then dynamic) | Not Started |
| 5 | F-003-P5 | Drawer access button/gesture area | Not Started |
| 6 | F-003-P6 | Configurable panel visibility | Not Started |

**Acceptance Criteria:**
- Clock updates every second without recomposing unnecessary UI.
- Date displays correctly in target locale.
- Quick-launch slots show real apps or graceful empty states.
- Drawer button is reachable and functional.

**Testing Requirements:**
- Unit: Clock format function; clock updates after 1 second (ViewModel with test dispatcher); date label string; home screen renders without crash.

---

#### F-004: Application Discovery and Launching

**Priority:** P0
**Dependencies:** F-001
**Implementation Phase:** Phase 4
**Status:** Implementation Complete — Build Verified (Device Validation Pending)

**Description:**
Query the Android package manager for launchable applications. Display icons and names. Launch apps reliably.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-004-P1 | PackageManager query for launchable apps | Implemented — Build Verified |
| 2 | F-004-P2 | AppInfo data model | Implemented — Build Verified |
| 3 | F-004-P3 | Icon loading and caching | Implemented — Build Verified |
| 4 | F-004-P4 | App launch with error handling | Implemented — Build Verified |
| 5 | F-004-P5 | Broadcast receiver for package changes | Implemented — Build Verified |
| 6 | F-004-P6 | Sorting (alphabetical default) | Implemented — Build Verified |

**Acceptance Criteria:**
- All user-launchable apps appear in the list.
- Icons load and display correctly.
- Tapping an app launches it.
- Disabled or missing apps are handled gracefully.
- App list updates when packages are installed or removed.

**Testing Requirements:**
- Unit: AppInfo maps from ResolveInfo; list sorts alphabetically; duplicates removed; empty list handled; repository emits updated list on package change.

**Platform Notes:**
- Requires `<queries>` element in manifest (API 30+).
- Use `Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER)` for discovery.

---

#### F-005: Application Search

**Priority:** P0
**Dependencies:** F-004
**Implementation Phase:** Phase 5
**Status:** Implementation Complete — Build Verified (Device Validation Pending)

**Description:**
Real-time search by app name. Filter as the user types. Handle empty and no-results states.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-005-P1 | Search composable (text field + clear) | Implemented — Build Verified |
| 2 | F-005-P2 | Filter logic wired to app list | Implemented — Build Verified |
| 3 | F-005-P3 | Empty state and no-results state | Implemented — Build Verified |
| 4 | F-005-P4 | Keyboard handling and IME actions | Implemented — Build Verified |

**Acceptance Criteria:**
- Results update as each character is typed.
- Empty query shows all apps.
- No-results state is communicated clearly.
- Pressing Enter or the search action on the keyboard launches the top result.

**Testing Requirements:**
- Unit: Exact-name filter; partial/case-insensitive filter; empty query returns all; no-match returns empty list.

---

#### F-006: Battery Telemetry

**Priority:** P0
**Dependencies:** F-002
**Implementation Phase:** Phase 7
**Status:** Implementation Complete — Build Verified (Device Validation Pending)

**Description:**
Display live battery level, charge state, and temperature. No polling; use sticky broadcast.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-006-P1 | `Intent.ACTION_BATTERY_CHANGED` sticky broadcast integration | Implemented — Build Verified |
| 2 | F-006-P2 | Battery percentage display composable | Implemented — Build Verified |
| 3 | F-006-P3 | Charging state indicator (charging / full / discharging) | Implemented — Build Verified |
| 4 | F-006-P4 | Battery temperature display | Implemented — Build Verified |
| 5 | F-006-P5 | Unavailable value handling | Implemented — Build Verified |

**Acceptance Criteria:**
- Percentage matches Android status bar.
- Charging state changes are reflected within 5 seconds.
- Temperature shows real value or "N/A" when unavailable.
- No background services created.

**Testing Requirements:**
- Unit: BatteryState maps correctly from Intent extras; charging state identified; temperature conversion; missing extras produce safe defaults.

---

### P1 — Developer-Grade HUD Experience

---

#### F-007: Animated Radar and HUD Graphics

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 6
**Status:** Implementation Complete — Build Verified (Device Validation Pending)

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-007-P1 | Radar rings and tick marks (Canvas composable) | Implemented — Build Verified |
| 2 | F-007-P2 | Rotating sweep animation | Implemented — Build Verified |
| 3 | F-007-P3 | Grid and scan-line overlay | Implemented — Build Verified |
| 4 | F-007-P4 | Configurable animation speed | Implemented — Build Verified |
| 5 | F-007-P5 | Pause/reduce on low-power or battery-saver mode | Implemented — Build Verified |

**Acceptance Criteria:**
- Radar renders at consistent frame rate without jank.
- Animation can be slowed or paused.
- No perceptible battery drain from animation alone in testing.

**Testing Requirements:**
- Compose: Radar composable renders without crash; animation does not throw exceptions.
- Device (Phase 14): Animation frame rate observed on physical device.

---

#### F-008: System Telemetry Dashboard

**Priority:** P1
**Dependencies:** F-006, F-002
**Implementation Phase:** Phase 7
**Status:** Implementation Complete — Build Verified (Device Validation Pending)

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-008-P1 | Network connectivity state (ConnectivityManager) | Implemented — Build Verified |
| 2 | F-008-P2 | Wi-Fi or mobile indicator | Implemented — Build Verified |
| 3 | F-008-P3 | Telemetry panel composable (combines battery + network) | Implemented — Build Verified |
| 4 | F-008-P4 | Data freshness timestamp | Implemented — Build Verified |
| 5 | F-008-P5 | Unavailable / permission denied handling | Implemented — Build Verified |

**Acceptance Criteria:**
- Connectivity state reflects real network condition.
- Values clearly indicate when they are unavailable.
- No ACCESS_FINE_LOCATION or elevated permissions used.

**Testing Requirements:**
- Unit: NetworkState maps from NetworkCapabilities (with fake); offline state produces correct NetworkState.

---

#### F-009: Quick Actions

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 8
**Status:** Not Started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-009-P1 | Settings shortcut composable | Not Started |
| 2 | F-009-P2 | Flashlight toggle (CameraManager API) | Not Started |
| 3 | F-009-P3 | Wi-Fi settings intent | Not Started |
| 4 | F-009-P4 | Bluetooth settings intent | Not Started |
| 5 | F-009-P5 | Action state feedback and error handling | Not Started |

**Acceptance Criteria:**
- Each action performs its intended function or opens the correct Settings screen.
- Actions that require confirmation display a clear indicator.
- No attempt to use restricted or deprecated system APIs.

**Testing Requirements:**
- Unit: Flashlight action calls a fake `TorchController` facade.
- Integration / device: `CameraManager` adapter invokes `setTorchMode` and handles unsupported hardware.
- Device: Settings intent resolves correctly.

---

#### F-010: Customization

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 9
**Status:** Not Started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-010-P1 | DataStore preferences setup | Not Started |
| 2 | F-010-P2 | Accent color selection | Not Started |
| 3 | F-010-P3 | Panel visibility toggles | Not Started |
| 4 | F-010-P4 | Animation speed setting | Not Started |
| 5 | F-010-P5 | Clock style selection | Not Started |

**Acceptance Criteria:**
- Settings persist across app restarts.
- Changes apply immediately without restart.
- Default values provide a sensible experience without configuration.

**Testing Requirements:**
- Unit: Default preferences produce expected values (in-memory DataStore); accent color reads back correctly; animation speed setting persists.

---

#### F-011: Application Organization

**Priority:** P1
**Dependencies:** F-004, F-010
**Implementation Phase:** Phase 10
**Status:** Not Started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-011-P1 | Pin / favorite applications | Not Started |
| 2 | F-011-P2 | Configurable quick-launch slots | Not Started |
| 3 | F-011-P3 | Sort by name or user preference | Not Started |
| 4 | F-011-P4 | Grid layout configuration | Not Started |

**Acceptance Criteria:**
- Favorites persist across app restarts.
- Quick-launch slots update when apps are changed.
- Sort order applies immediately.

**Testing Requirements:**
- Unit: Favorites list persists; sort order logic; grid config reads back correctly.

---

### P2 — Advanced Functionality

---

#### F-012: Widget Hosting

**Priority:** P2
**Dependencies:** F-001
**Implementation Phase:** Phase 11
**Status:** Not Started

**Description:**
Investigate the feasibility of hosting Android app widgets on the AEGIS home screen using the AppWidgetHost API, then implement if feasible.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-012-P1 | Feasibility investigation: AppWidgetHost API, permissions, lifecycle | Not Started |
| 2 | F-012-P2 | AppWidgetHost and AppWidgetManager integration | Not Started |
| 3 | F-012-P3 | Widget picker UI | Not Started |
| 4 | F-012-P4 | Widget placement and persistence on home screen | Not Started |
| 5 | F-012-P5 | Widget lifecycle: bind, configure, update, remove | Not Started |
| 6 | F-012-P6 | Widget resize handling | Not Started |

**Acceptance Criteria (per phase):**
- F-012-P1: Feasibility report documents API complexity, required permissions (BIND_APPWIDGET), and launcher integration requirements.
- F-012-P2 through F-012-P6: Widget hosts, binds, updates, and removes correctly without lifecycle leaks; widgets from at least 3 third-party apps load and display; placement persists across launcher restarts.

**Risks:**
- The custom widget-host binding flow declares `BIND_APPWIDGET`; if binding is not already allowed, it must request explicit user approval through the system binding flow.
- Widget configuration activities require careful lifecycle management.
- If F-012-P1 investigation reveals insurmountable restrictions, F-012 will be moved to Deferred and documented in DECISIONS.md.

**Testing Requirements:**
- F-012-P1: Manual investigation only; document findings.
- F-012-P2 through F-012-P6: Widget lifecycle tests (physical device required).

---

#### F-013: Context-Aware HUD

**Priority:** P2
**Dependencies:** F-010
**Implementation Phase:** Phase 11
**Status:** Not Started

**Description:**
Automatically adapt the HUD display based on configurable user-defined rules (time of day, battery level, network state). No background services, no location data, no restricted permissions.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-013-P1 | Rule model design: define rule types, trigger conditions, and actions | Not Started |
| 2 | F-013-P2 | Rule evaluation engine (pure Kotlin, no background service) | Not Started |
| 3 | F-013-P3 | Time-based rules (morning / evening / night profiles) | Not Started |
| 4 | F-013-P4 | Battery-level-based rules (low battery HUD mode) | Not Started |
| 5 | F-013-P5 | Network-state-based rules (offline indicator) | Not Started |
| 6 | F-013-P6 | Rule configuration UI | Not Started |
| 7 | F-013-P7 | Rule persistence via DataStore | Not Started |

**Acceptance Criteria:**
- Rules evaluate correctly based on current device state when the launcher is foregrounded.
- No location data used. No background service runs for rule evaluation.
- Rules persist across restarts.
- Default state (no rules configured) produces normal HUD behavior.

**Privacy Constraints:** No location APIs. No usage statistics. On-device only.

**Testing Requirements:**
- Unit: Rule evaluation with mock state inputs; persistence reads back correctly.
- Device: Profile switch observed on home screen when trigger condition is met.

---

#### F-014: Developer Console

**Priority:** P2
**Dependencies:** F-008
**Implementation Phase:** Phase 12
**Status:** Not Started

**Description:**
Provide a developer-facing diagnostics screen showing genuine launcher diagnostic information. Real data only — no simulated values. ADB and shell operations are explicitly out of scope.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-014-P1 | Console screen scaffold and navigation route | Not Started |
| 2 | F-014-P2 | Launcher diagnostics: uptime, restart count, last package update | Not Started |
| 3 | F-014-P3 | System information panel: Android version, API level, device model | Not Started |
| 4 | F-014-P4 | Permission status display (which permissions are granted) | Not Started |
| 5 | F-014-P5 | Log viewer: recent launcher log entries (in-memory ring buffer) | Not Started |
| 6 | F-014-P6 | Export/copy diagnostic snapshot | Not Started |

**Acceptance Criteria:**
- Console is accessible via a developer option toggle.
- All displayed values are real data from Android APIs, not simulated.
- No ADB commands or shell access performed by the launcher.
- Console can be hidden for non-developer use.

**Scope Boundary:**
- In scope: Launcher-internal diagnostics, system info via public Android APIs, in-memory log buffer.
- Out of scope: ADB, shell commands, root access, process manipulation.

**Testing Requirements:**
- Unit: Diagnostic data models map correctly; log buffer ring behaviour.
- Device: All panels display real values; export copies to clipboard.

---

#### F-015: Gesture Navigation

**Priority:** P2
**Dependencies:** F-001
**Implementation Phase:** Phase 11
**Status:** Not Started

**Description:**
Add gesture-based interactions to the home screen. Must not conflict with Android system gesture navigation (both gesture-nav mode and button-nav mode).

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-015-P1 | Investigation: gesture conflict analysis with Android system navigation | Not Started |
| 2 | F-015-P2 | Swipe-up gesture to open app drawer | Not Started |
| 3 | F-015-P3 | Swipe-down gesture to open quick settings or notification shade | Not Started |
| 4 | F-015-P4 | Long-press gesture on home screen | Not Started |
| 5 | F-015-P5 | Gesture customization settings | Not Started |

**Acceptance Criteria (per phase):**
- F-015-P1: Documents which gestures are safely available without conflicting with system navigation in both nav modes.
- F-015-P2 through F-015-P5: Each gesture works reliably on the target device; gestures can be individually disabled in settings.

**Risks:**
- Android gesture navigation (API 30+) reserves swipe-from-edge gestures for system use.
- F-015-P1 investigation may result in some gestures being removed from scope.

**Testing Requirements:**
- F-015-P1: Manual investigation; document findings in DECISIONS.md.
- F-015-P2 through F-015-P5: Gesture tests on device; verify no system conflict.

---

#### F-016: Launcher Reliability and Performance

**Priority:** P2
**Dependencies:** All P0 features
**Implementation Phase:** Phase 13
**Status:** Not Started

**Description:**
Define and enforce measurable reliability and performance targets. Conduct systematic profiling, battery testing, lifecycle testing, and regression coverage.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-016-P1 | Define measurable performance targets (startup time, frame rate, memory) | Not Started |
| 2 | F-016-P2 | Startup time profiling and optimization | Not Started |
| 3 | F-016-P3 | Frame rate profiling: radar animation and drawer scroll | Not Started |
| 4 | F-016-P4 | Battery consumption measurement: idle, animation, app launch | Not Started |
| 5 | F-016-P5 | Lifecycle stress test: repeated home presses, process death recovery | Not Started |
| 6 | F-016-P6 | Memory leak detection: receiver/callback registration audit | Not Started |
| 7 | F-016-P7 | Full regression test run across all P0 and P1 features | Not Started |

**Preliminary Performance Targets (to be finalized in F-016-P1):**
- Cold start to visible home screen: <= 1 second on target device.
- Drawer scroll: sustained 60fps on target device.
- Idle battery drain: <= 1% per hour in isolation.
- No memory leak from BroadcastReceiver or NetworkCallback after 30 minutes idle.

**Acceptance Criteria:**
- Performance targets defined and documented before testing begins.
- Each target has a measurement method and pass/fail threshold.
- All regressions documented in BUG_TRACKER.md.
- ./gradlew testDebugUnitTest passes with no failures.

**Testing Requirements:**
- Device: All performance tests require physical device (not emulator).
- Automated: Unit test regression suite must pass.
- Manual: Memory, frame rate, battery measured with Android Studio Profiler.

---

### P3 — Optional Future Enhancements

---

#### F-017: Advanced HUD Themes

**Priority:** P3
**Dependencies:** F-010
**Implementation Phase:** Post Phase 13 (Future)
**Status:** Deferred

**Description:**
Support multiple named HUD themes beyond the default AEGIS palette. Each theme defines a complete color scheme, optional typography variation, and visual style.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-017-P1 | Theme model: define theme data structure and serialization format | Deferred |
| 2 | F-017-P2 | Theme registry: built-in themes (AEGIS Default, Amber, Red Alert) | Deferred |
| 3 | F-017-P3 | Theme persistence via DataStore | Deferred |
| 4 | F-017-P4 | Theme selector UI | Deferred |
| 5 | F-017-P5 | Live theme preview without app restart | Deferred |

**Acceptance Criteria:**
- At least 3 named themes available.
- Selected theme persists across restarts.
- Theme switch applies immediately without restart.
- No theme contains hardcoded values in composables.

**Risk:** Theme expansion must not break F-002 design token architecture.

---

#### F-018: Extended Automation

**Priority:** P3
**Dependencies:** F-013 (Context-Aware HUD)
**Implementation Phase:** Post Phase 13 (Future)
**Status:** Deferred

**Description:**
Extend context-aware behavior with additional trigger types and output actions. All automation must use only public Android APIs; no root access or accessibility service required.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-018-P1 | Investigate supported automation mechanisms and required permissions | Deferred |
| 2 | F-018-P2 | Do-Not-Disturb state as trigger (NotificationManager API) | Deferred |
| 3 | F-018-P3 | Charging state as trigger (extends F-013) | Deferred |
| 4 | F-018-P4 | Automation action: launch a specific app | Deferred |
| 5 | F-018-P5 | Automation action: change HUD profile | Deferred |

**Acceptance Criteria:**
- All triggers use documented, non-restricted Android APIs.
- No root access, no accessibility service, no usage stats permission required.
- F-018-P1 investigation documents all required permissions before implementation begins.

**Risk:** Android restricts many automation APIs; investigation must confirm feasibility before implementation.

---

#### F-019: Public Open-Source Preparation

**Priority:** P3
**Dependencies:** All features (full release readiness)
**Implementation Phase:** Phase 15 (Release Prep)
**Status:** Deferred

**Description:**
Prepare the project for potential public open-source release. Includes licensing review, dependency audit, documentation cleanup, reproducible builds, and removal of personal or machine-specific data.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-019-P1 | Dependency license audit: verify all dependencies are permissively licensed | Deferred |
| 2 | F-019-P2 | Remove all personal identifiers and machine-specific paths from source | Deferred |
| 3 | F-019-P3 | Add LICENSE file and copyright headers | Deferred |
| 4 | F-019-P4 | Reproducible build verification (clean build produces identical APK) | Deferred |
| 5 | F-019-P5 | Public README and contribution guide | Deferred |
| 6 | F-019-P6 | Release checklist: all acceptance criteria passing; no critical bugs open | Deferred |

**Acceptance Criteria:**
- All third-party dependencies have compatible licenses (MIT, Apache 2.0, BSD, or similar).
- No personal paths, credentials, or private data in any committed file.
- LICENSE file present and accurate.
- ./gradlew assembleRelease produces a signed, reproducible APK.
- All critical and high-severity bugs resolved before release.

**Risks:**
- GPL-licensed dependencies would impose restrictions on distribution.
- Release signing configuration requires secure key management.
