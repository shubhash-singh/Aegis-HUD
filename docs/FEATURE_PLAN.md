# FEATURE_PLAN.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

---

## Feature Inventory

### P0 — Essential Launcher Functionality

---

#### F-001: Android Launcher Foundation

**Priority:** P0
**Dependencies:** None
**Implementation Phase:** Phase 1
**Status:** Not started

**Description:**
Register the application as a valid Android home-screen launcher. Implement the main launcher activity. Handle lifecycle events and recovery.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-001-P1 | Manifest declaration and launcher registration | Not started |
| 2 | F-001-P2 | MainActivity implementation with Compose scaffold | Not started |
| 3 | F-001-P3 | Lifecycle handling: back press, recents, resume | Not started |
| 4 | F-001-P4 | Process recreation and configuration change handling | Not started |
| 5 | F-001-P5 | First-run default launcher selection flow | Not started |

**Acceptance Criteria:**
- App appears in the "Select Home App" dialog.
- App starts when the home button is pressed after selection.
- App resumes correctly after launching another app.
- Process recreation (rotation, memory pressure) does not crash the app.
- No internet connection is required for basic operation.

**Platform Notes:**
- `android.intent.category.HOME` and `android.intent.category.DEFAULT` required.
- `android.intent.action.MAIN` required.
- `RoleManager.ROLE_HOME` (API 29+) preferred; fallback to `ACTION_HOME_SETTINGS`.

---

#### F-002: HUD Design System

**Priority:** P0
**Dependencies:** F-001
**Implementation Phase:** Phase 2
**Status:** Not started

**Description:**
Define the complete visual design system: color palette, typography, spacing, HUD panel components, status indicators, and layout primitives.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-002-P1 | Color palette and theme tokens (MaterialTheme + custom) | Not started |
| 2 | F-002-P2 | Typography: monospaced technical typeface | Not started |
| 3 | F-002-P3 | HUD panel and border composables | Not started |
| 4 | F-002-P4 | Status indicator composables (green/amber/red) | Not started |
| 5 | F-002-P5 | Responsive layout and inset handling | Not started |

**Acceptance Criteria:**
- A static HUD shell renders on screen without crashes.
- Colors, fonts, and spacing match the UI/UX specification.
- System insets (status bar, navigation bar) are handled correctly.
- No hardcoded colors or dimensions in composables.

---

#### F-003: HUD Home Screen

**Priority:** P0
**Dependencies:** F-002
**Implementation Phase:** Phase 2 / 3
**Status:** Not started

**Description:**
Create the main dashboard. Display a live digital clock, date, system status, quick-launch slots, and a path to the application drawer.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-003-P1 | Static dashboard layout | Not started |
| 2 | F-003-P2 | Live digital clock composable | Not started |
| 3 | F-003-P3 | Date and formatted system label | Not started |
| 4 | F-003-P4 | Quick-launch application slots (static, then dynamic) | Not started |
| 5 | F-003-P5 | Drawer access button/gesture area | Not started |
| 6 | F-003-P6 | Configurable panel visibility | Not started |

**Acceptance Criteria:**
- Clock updates every second without recomposing unnecessary UI.
- Date displays correctly in target locale.
- Quick-launch slots show real apps or graceful empty states.
- Drawer button is reachable and functional.

---

#### F-004: Application Discovery and Launching

**Priority:** P0
**Dependencies:** F-001
**Implementation Phase:** Phase 4
**Status:** Not started

**Description:**
Query the Android package manager for launchable applications. Display icons and names. Launch apps reliably.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-004-P1 | PackageManager query for launchable apps | Not started |
| 2 | F-004-P2 | AppInfo data model | Not started |
| 3 | F-004-P3 | Icon loading and caching | Not started |
| 4 | F-004-P4 | App launch with error handling | Not started |
| 5 | F-004-P5 | Broadcast receiver for package changes | Not started |
| 6 | F-004-P6 | Sorting (alphabetical default) | Not started |

**Acceptance Criteria:**
- All user-launchable apps appear in the list.
- Icons load and display correctly.
- Tapping an app launches it.
- Disabled or missing apps are handled gracefully.
- App list updates when packages are installed or removed.

**Platform Notes:**
- Requires `<queries>` element in manifest (API 30+).
- Use `Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER)` for discovery.

---

#### F-005: Application Search

**Priority:** P0
**Dependencies:** F-004
**Implementation Phase:** Phase 5
**Status:** Not started

**Description:**
Real-time search by app name. Filter as the user types. Handle empty and no-results states.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-005-P1 | Search composable (text field + clear) | Not started |
| 2 | F-005-P2 | Filter logic wired to app list | Not started |
| 3 | F-005-P3 | Empty state and no-results state | Not started |
| 4 | F-005-P4 | Keyboard handling and IME actions | Not started |

**Acceptance Criteria:**
- Results update as each character is typed.
- Empty query shows all apps.
- No-results state is communicated clearly.
- Pressing Enter or the search action on the keyboard launches the top result.

---

#### F-006: Battery Telemetry

**Priority:** P0
**Dependencies:** F-002
**Implementation Phase:** Phase 3
**Status:** Not started

**Description:**
Display live battery level, charge state, and temperature. No polling; use sticky broadcast.

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-006-P1 | BatteryManager sticky broadcast integration | Not started |
| 2 | F-006-P2 | Battery percentage display composable | Not started |
| 3 | F-006-P3 | Charging state indicator (charging / full / discharging) | Not started |
| 4 | F-006-P4 | Battery temperature display | Not started |
| 5 | F-006-P5 | Unavailable value handling | Not started |

**Acceptance Criteria:**
- Percentage matches Android status bar.
- Charging state changes are reflected within 5 seconds.
- Temperature shows real value or "N/A" when unavailable.
- No background services created.

---

### P1 — Developer-Grade HUD Experience

---

#### F-007: Animated Radar and HUD Graphics

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 6
**Status:** Not started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-007-P1 | Radar rings and tick marks (Canvas composable) | Not started |
| 2 | F-007-P2 | Rotating sweep animation | Not started |
| 3 | F-007-P3 | Grid and scan-line overlay | Not started |
| 4 | F-007-P4 | Configurable animation speed | Not started |
| 5 | F-007-P5 | Pause/reduce on low-power or battery-saver mode | Not started |

**Acceptance Criteria:**
- Radar renders at consistent frame rate without jank.
- Animation can be slowed or paused.
- No perceptible battery drain from animation alone in testing.

---

#### F-008: System Telemetry Dashboard

**Priority:** P1
**Dependencies:** F-006, F-002
**Implementation Phase:** Phase 7
**Status:** Not started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-008-P1 | Network connectivity state (ConnectivityManager) | Not started |
| 2 | F-008-P2 | Wi-Fi or mobile indicator | Not started |
| 3 | F-008-P3 | Telemetry panel composable (combines battery + network) | Not started |
| 4 | F-008-P4 | Data freshness timestamp | Not started |
| 5 | F-008-P5 | Unavailable / permission denied handling | Not started |

**Acceptance Criteria:**
- Connectivity state reflects real network condition.
- Values clearly indicate when they are unavailable.
- No ACCESS_FINE_LOCATION or elevated permissions used.

---

#### F-009: Quick Actions

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 8
**Status:** Not started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-009-P1 | Settings shortcut composable | Not started |
| 2 | F-009-P2 | Flashlight toggle (CameraManager API) | Not started |
| 3 | F-009-P3 | Wi-Fi settings intent | Not started |
| 4 | F-009-P4 | Bluetooth settings intent | Not started |
| 5 | F-009-P5 | Action state feedback and error handling | Not started |

**Acceptance Criteria:**
- Each action performs its intended function or opens the correct Settings screen.
- Actions that require confirmation display a clear indicator.
- No attempt to use restricted or deprecated system APIs.

---

#### F-010: Customization

**Priority:** P1
**Dependencies:** F-002
**Implementation Phase:** Phase 9
**Status:** Not started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-010-P1 | DataStore preferences setup | Not started |
| 2 | F-010-P2 | Accent color selection | Not started |
| 3 | F-010-P3 | Panel visibility toggles | Not started |
| 4 | F-010-P4 | Animation speed setting | Not started |
| 5 | F-010-P5 | Clock style selection | Not started |

**Acceptance Criteria:**
- Settings persist across app restarts.
- Changes apply immediately without restart.
- Default values provide a sensible experience without configuration.

---

#### F-011: Application Organization

**Priority:** P1
**Dependencies:** F-004, F-010
**Implementation Phase:** Phase 10
**Status:** Not started

**Phases:**

| Phase | ID | Title | Status |
|---|---|---|---|
| 1 | F-011-P1 | Pin / favorite applications | Not started |
| 2 | F-011-P2 | Configurable quick-launch slots | Not started |
| 3 | F-011-P3 | Sort by name or user preference | Not started |
| 4 | F-011-P4 | Grid layout configuration | Not started |

---

### P2 — Advanced Functionality

---

#### F-012: Widgets

**Priority:** P2
**Dependencies:** F-001
**Implementation Phase:** Phase 11
**Status:** Not started

**Notes:** AppWidgetHost API is complex. Evaluate feasibility before committing to implementation.

---

#### F-013: Context-Aware HUD

**Priority:** P2
**Dependencies:** F-010
**Implementation Phase:** Phase 11
**Status:** Not started

---

#### F-014: Developer Console

**Priority:** P2
**Dependencies:** F-008
**Implementation Phase:** Phase 12
**Status:** Not started

---

#### F-015: Gesture Navigation

**Priority:** P2
**Dependencies:** F-001
**Implementation Phase:** Phase 11
**Status:** Not started

---

#### F-016: Launcher Reliability and Performance

**Priority:** P2
**Dependencies:** All P0
**Implementation Phase:** Phase 13
**Status:** Not started

---

### P3 — Optional Future Enhancements

---

#### F-017: Advanced HUD Themes — Not started
#### F-018: Extended Automation — Not started
#### F-019: Public Open-Source Preparation — Not started
