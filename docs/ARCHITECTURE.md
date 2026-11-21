# ARCHITECTURE.md — AEGIS HUD Launcher

**Version:** 0.2.0-redesign
**Last Updated:** 2026-11-21 (Phase 10 — Full-Screen AI Command Interface Redesign)

---

## 1. Overview

AEGIS HUD Launcher is a single-module Android application (Kotlin + Jetpack Compose) for the initial release. The architecture follows the standard Android unidirectional data flow pattern: UI observes state, events flow up to ViewModels and repositories, data flows down to composables.

Module boundaries will be introduced when complexity justifies it. Do not pre-create empty modules.

---

## 2. Architectural Layers

```
┌─────────────────────────────────────────────┐
│   UI Layer (Jetpack Compose Composables)    │
│   - Screens, components, animations        │
│   - Observes StateFlow via collectAsStateWithLifecycle │
└────────────────────┬────────────────────────┘
                     │ state down / events up
┌────────────────────▼────────────────────────┐
│   ViewModel Layer (AndroidX ViewModel)      │
│   - Holds and transforms UI state           │
│   - Scoped to Activity or NavBackStack      │
│   - Exposes StateFlow / SharedFlow          │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│   Repository / Domain Layer                 │
│   - AppRepository, BatteryRepository, etc. │
│   - Abstracts platform sources              │
│   - Uses Kotlin coroutines + Flow           │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│   Data / Platform Layer                     │
│   - PackageManager, BatteryManager          │
│   - ConnectivityManager                     │
│   - DataStore (preferences)                 │
│   - BroadcastReceiver (package/battery)     │
└─────────────────────────────────────────────┘
```

---

## 3. Package Structure

The initial package structure for `dev.ragnar.aegis`:

```
dev.ragnar.aegis/
├── MainActivity.kt                        # Launcher Activity entry point
│
├── core/
│   ├── design/
│   │   ├── AegisTheme.kt                  # MaterialTheme wrapper + HUD tokens
│   │   ├── Color.kt                       # Color palette
│   │   ├── Typography.kt                  # Monospaced + UI typefaces
│   │   ├── Spacing.kt                     # Dimension tokens
│   │   └── components/
│   │       ├── HudPanel.kt                # Bordered HUD panel composable
│   │       ├── HudBorder.kt               # Technical border/tick composable
│   │       ├── StatusIndicator.kt         # Green/amber/red dot indicator
│   │       └── ScanLine.kt                # Scan-line overlay composable
│   ├── model/
│   │   ├── AppInfo.kt                     # Data class for discovered apps
│   │   ├── BatteryState.kt                # Battery data model
│   │   └── NetworkState.kt                # Network state data model
│   └── common/
│       └── Extensions.kt                  # Kotlin extension utilities
│
├── launcher/
│   ├── home/
│   │   ├── HomeScreen.kt                  # Full-screen AI command interface root composable
│   │   ├── HomeViewModel.kt               # Home screen ViewModel (AI status + gesture state)
│   │   ├── ClockDisplay.kt                # Live clock composable
│   │   │
│   │   ├── visualization/
│   │   │   ├── AICoreVisualization.kt     # Full-screen holographic neural brain (Canvas)
│   │   │   ├── NeuralGeometry.kt          # Static geometry cache: node positions, ring radii
│   │   │   └── NeuralAnimationState.kt    # Encapsulated animation state (InfiniteTransition)
│   │   │
│   │   └── panels/
│   │       ├── FloatingCorePanel.kt        # Top-left: AEGIS AT CORE + version
│   │       ├── FloatingAiStatusPanel.kt    # Top-right: AI STATUS (IDLE/PROCESSING/ACTIVE)
│   │       ├── FloatingPerformancePanel.kt # Left: CPU + RAM utilization bars
│   │       ├── FloatingDevicePanel.kt      # Right: Temperature + Battery
│   │       ├── FloatingIntelligencePanel.kt# Lower-left: CONTEXT/VOICE/VISION/AUTOMATION
│   │       ├── FloatingTelemetryPanel.kt   # Lower-right: Batt temp, uptime, voltage
│   │       └── FloatingSystemStatusPanel.kt# Bottom: Network + storage
│   │
│   ├── drawer/
│   │   ├── AppDrawerScreen.kt             # Full app drawer composable (swipe-up)
│   │   ├── AppDrawerViewModel.kt          # Drawer ViewModel
│   │   └── AppDrawerGestureIndicator.kt   # Bottom chevron + "SWIPE UP" indicator
│   ├── search/
│   │   ├── SearchBar.kt                   # Search input composable (drawer-only)
│   │   └── SearchViewModel.kt             # Search ViewModel (or merged into drawer)
│   └── shortcuts/
│       └── QuickActions.kt                # Quick-action buttons composable
│
├── telemetry/
│   ├── battery/
│   │   ├── BatteryRepository.kt           # Reads BatteryManager broadcast
│   │   └── BatteryDisplay.kt              # Battery HUD panel composable
│   ├── network/
│   │   ├── NetworkRepository.kt           # ConnectivityManager observer
│   │   └── NetworkDisplay.kt              # Network HUD panel composable
│   └── system/
│       ├── SystemInfoRepository.kt        # CPU + RAM + uptime + storage telemetry
│       └── SystemTelemetryState.kt        # Data models for system stats
│
├── settings/
│   ├── SettingsScreen.kt                  # Customization screen composable
│   ├── SettingsViewModel.kt               # Settings ViewModel
│   └── PreferencesRepository.kt           # DataStore wrapper
│
├── widgets/                               # Phase 11 — placeholder only
│
└── diagnostics/
    └── DevConsoleScreen.kt                # Phase 12 — developer console
```

> [!NOTE]
> `visualization/` and `panels/` are new sub-packages introduced in Phase 10 (Full-Screen AI Command Interface). The old `RadarDisplay.kt` composable is superseded by `AICoreVisualization.kt`.

---

## 4. Navigation

**Initial Approach:** Simple Compose navigation with `NavHost` and named routes.

Routes:
- `home` — Main HUD home screen (start destination)
- `drawer` — Application drawer
- `search` — Search overlay (may be presented as bottom sheet or overlay on `drawer`)
- `settings` — Customization settings screen
- `dev_console` — Developer console (Phase 12)

No nested navigation graphs until Phase 5+.

---

## 5. State Management

### Rules

- All mutable UI state lives in ViewModels or composable-local `remember` state.
- No global singletons holding mutable state.
- Platform data (battery, network) flows as `Flow` from repositories → ViewModel → UI.
- Preferences flow as `Flow<Preferences>` from DataStore → ViewModel → UI.

### Battery State Example Flow

```
Intent.ACTION_BATTERY_CHANGED sticky broadcast (platform)
    → BatteryRepository (produces Flow<BatteryState>)
    → HomeViewModel (collects, maps to UI state)
    → HomeScreen (observes via collectAsStateWithLifecycle)
    → BatteryDisplay composable (renders)
```

---

## 6. Android Launcher-Specific Concerns

### Activity Manifest Attributes

The launcher's `MainActivity` uses three non-default attributes. Each is documented here with its justification. Decisions are also recorded in `docs/DECISIONS.md` (DEC-010).

#### `android:launchMode="singleTask"` — Accepted

**Behavior:** Android ensures only one instance of the Activity exists at a time. If the launcher is already running and a new Home intent arrives, Android routes it to `onNewIntent()` rather than creating a new instance.

**Justification:** Without `singleTask`, pressing Home while the launcher is already displayed could push a second instance of `MainActivity` onto the back stack, resulting in nested launcher screens and broken navigation. A launcher whose sole task is to be the home screen benefits from a single guaranteed instance. Standard practice for Android launcher activities.

**Test requirement:** Pressing the Home button repeatedly must not stack multiple Activity instances. Verified by confirming the back stack contains exactly one root entry after multiple Home presses.

#### `android:stateNotNeeded="true"` — Accepted

**Behavior:** Informs the Android OS that this Activity can be killed without saving instance state via `onSaveInstanceState()`. The system will not call `onSaveInstanceState()`, and `onCreate()` will always receive a null bundle.

**Justification:** A launcher's home screen is always reconstructed from live data sources (PackageManager, BatteryManager, DataStore), not from a previously saved UI bundle. Declaring `stateNotNeeded="true"` makes this design contract explicit to the OS and avoids unnecessary bundle serialization. It is a deliberate choice that fits the stateless-reconstruction model, not a universal requirement for launchers.

**Design consequence:** No critical launcher state may depend on the saved instance state bundle. All persistent data must come from DataStore or live platform APIs. `onCreate()` must handle a null bundle correctly at all times. Transient UI positions (e.g., scroll offset in the app drawer) will be lost on process death; this is acceptable because the launcher should always return the user to the home screen after a restart, not to a mid-scroll position in a sub-screen.

**Test requirement:** Kill the launcher process via `adb shell am kill dev.ragnar.aegis`, then press Home. AEGIS must restart and display the home screen correctly with no crash or missing data.

#### `android:excludeFromRecents="true"` — Accepted

**Behavior:** The task containing this Activity is not shown in the Android Overview (Recents) screen.

**Justification for launchers:** The home screen launcher should not appear as a card in the Recents screen alongside regular apps. Appearing in Recents would allow the user to accidentally dismiss the launcher task, which is unexpected behavior for a home screen. This attribute is standard for launcher activities.

**Test requirement:** Open the Recents screen (via swipe up or Recents button). AEGIS must not appear as a card.

---

### Back Press

In a launcher, the back press from the home screen should do nothing (or optionally show a quick settings overlay). Do not `finish()` on back press from the home screen. Use `BackHandler { /* no-op or show overlay */ }` in Compose.

### App Launch Task Flags

Apps launched from the launcher should start in their own task. Use `Intent.FLAG_ACTIVITY_NEW_TASK` when launching apps.

---

## 7. Theming and Design System

### Custom HUD Theme

A custom `AegisTheme` wraps `MaterialTheme` for Material 3 compatibility where needed (e.g., text fields) but overrides with HUD-specific tokens.

All colors, fonts, and spacings are accessed through `AegisTheme.colors`, `AegisTheme.typography`, and `AegisTheme.spacing` — never hardcoded in composables.

### CompositionLocal for HUD Tokens

```kotlin
val LocalAegisColors = compositionLocalOf { AegisColorScheme.default() }
val LocalAegisTypography = compositionLocalOf { AegisTypography.default() }
val LocalAegisSpacing = compositionLocalOf { AegisSpacing.default() }
```

---

## 8. Dependency Injection

**Phase 1-5:** Manual dependency wiring. No DI framework.

ViewModels with constructor dependencies are created through a `ViewModelProvider.Factory`, passed to Compose as `viewModel<MyViewModel>(factory = myViewModelFactory)`. The factory owns the manual repository wiring.

**Phase 6+:** Evaluate Hilt if the number of dependencies justifies it. Decision to be recorded in `DECISIONS.md`.

---

## 9. Background Work Policy

- No long-running background services for telemetry.
- Use `BroadcastReceiver` (registered in code, not manifest) for battery and package changes.
- Use `ConnectivityManager.NetworkCallback` for network state (lifecycle-aware, registered in ViewModel or Activity).
- All coroutines scoped to `viewModelScope` or `lifecycleScope`. No unbounded `GlobalScope` usage.

---

## 10. Testing Architecture

- Unit tests in `app/src/test/` — test ViewModels, repositories, and pure logic.
- UI tests in `app/src/androidTest/` using Compose UI testing APIs.
- No instrumentation tests that require real device in Phase 1 (build + unit tests only).
- Device tests deferred to Phase 14.
