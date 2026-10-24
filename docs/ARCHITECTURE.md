# ARCHITECTURE.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

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
│   - Observes StateFlow via collectAsState   │
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
│   │   ├── HomeScreen.kt                  # Main HUD home screen composable
│   │   ├── HomeViewModel.kt               # Home screen ViewModel
│   │   ├── ClockDisplay.kt                # Live clock composable
│   │   └── QuickLaunchRow.kt              # Quick-launch app slots
│   ├── drawer/
│   │   ├── AppDrawerScreen.kt             # Full app drawer composable
│   │   └── AppDrawerViewModel.kt          # Drawer ViewModel
│   ├── search/
│   │   ├── SearchBar.kt                   # Search input composable
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
│       └── SystemInfoRepository.kt        # Future: additional system info
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
BatteryManager sticky broadcast (platform)
    → BatteryRepository (produces Flow<BatteryState>)
    → HomeViewModel (collects, maps to UI state)
    → HomeScreen (observes via collectAsStateWithLifecycle)
    → BatteryDisplay composable (renders)
```

---

## 6. Android Launcher-Specific Concerns

### Activity Flags

`MainActivity` must use:
```xml
<activity
    android:launchMode="singleTask"
    android:stateNotNeeded="true"
    android:excludeFromRecents="true" />
```

`stateNotNeeded="true"` allows Android to kill the launcher process without saving state, which is expected behavior. The launcher must rebuild from scratch on each start without depending on saved instance state for critical data.

`excludeFromRecents="true"` prevents the launcher from appearing in the recents screen.

### Back Press

In a launcher, the back press from the home screen should do nothing (or optionally show a quick settings overlay). Do not `finish()` on back press from the home screen. Use `BackHandler { /* no-op or show overlay */ }` in Compose.

### Recents and Task Stack

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

ViewModels are created via `viewModel { MyViewModel(repository) }` with `ViewModelProvider.Factory`.

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
