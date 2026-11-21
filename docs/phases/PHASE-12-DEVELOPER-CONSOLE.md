# PHASE-12 — Developer Console

**Feature:** F-014 (Developer Console)  
**Dependencies:** F-002 (HUD Design System), F-008 (System Telemetry Dashboard), F-010 (Customization)  
**Status:** Implementation Complete — Build Verified (Device Validation Pending)  
**Started:** 2026-11-22  
**Completed:** 2026-11-22 (Build Verified)  

---

## Objective

Provide a developer-facing, genuine HUD diagnostics interface:
1. Display authentic launcher runtime diagnostics (heap memory, process uptime, activity restarts, package count).
2. Display system hardware and platform details (Android OS version, API level, security patch, device model, CPU ABI, display metrics).
3. Display real-time Android permission audit statuses (granted vs. denied).
4. Provide an in-memory ring-buffer log viewer capturing recent launcher events without disk I/O.
5. Provide a one-tap "COPY DIAGNOSTICS" button to export formatted system telemetry to the clipboard.
6. Zero simulated or fake values (DEC-009 compliant). ADB and shell access are strictly out of scope.

---

## Sub-Phases

| ID | Title | Scope | Status |
|---|---|---|---|
| **F-014-P1** | Console Scaffold & Navigation | Full-screen tactical HUD console overlay accessible from `SettingsSheet` ("DEV CONSOLE [>]") with dismiss/back navigation. | Implemented — Build Verified |
| **F-014-P2** | Launcher Diagnostics | Real runtime metrics: JVM heap allocated/max, native memory, process uptime, total launchable apps tracked. | Implemented — Build Verified |
| **F-014-P3** | System Hardware & OS Info | Device manufacturer, model, board, Android version, SDK API level, security patch level, primary CPU ABI. | Implemented — Build Verified |
| **F-014-P4** | Permission Audit Panel | Check and report granted status for permissions declared in the manifest (`ACCESS_NETWORK_STATE`, `CAMERA`, etc.). | Implemented — Build Verified |
| **F-014-P5** | In-Memory Log Viewer | Thread-safe in-memory ring buffer (`AegisLogBuffer`) capturing recent log statements with level filtering (`DEBUG`, `INFO`, `WARN`, `ERROR`). | Implemented — Build Verified |
| **F-014-P6** | Export Diagnostics Snapshot | Formatted markdown text generation and clipboard copy via `ClipboardManager` with copy confirmation. | Implemented — Build Verified |

---

## Architecture & Data Flow

### 1. Domain Models (`dev.ragnar.aegis.domain.diagnostics`)

#### `DiagnosticsSnapshot`
```kotlin
data class DiagnosticsSnapshot(
    val launcherDiagnostics: LauncherDiagnostics,
    val systemInfo: SystemHardwareInfo,
    val permissions: List<PermissionStatus>,
    val recentLogs: List<LogEntry>,
)
```

#### `LauncherDiagnostics`
```kotlin
data class LauncherDiagnostics(
    val processUptimeMillis: Long,
    val processUptimeFormatted: String,
    val heapAllocatedMb: Float,
    val heapMaxMb: Float,
    val heapPercent: Int,
    val launchableAppsCount: Int,
    val favoriteAppsCount: Int,
)
```

#### `SystemHardwareInfo`
```kotlin
data class SystemHardwareInfo(
    val deviceModel: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildId: String,
    val cpuAbi: String,
    val securityPatch: String,
)
```

#### `PermissionStatus`
```kotlin
data class PermissionStatus(
    val permission: String,
    val isGranted: Boolean,
)
```

#### `LogEntry` & `LogLevel`
```kotlin
enum class LogLevel { DEBUG, INFO, WARN, ERROR }

data class LogEntry(
    val timestampMillis: Long,
    val timestampFormatted: String,
    val level: LogLevel,
    val tag: String,
    val message: String,
)
```

### 2. Logging Buffer (`dev.ragnar.aegis.data.diagnostics`)

`AegisLogBuffer`:
- In-memory thread-safe circular buffer (capacity = 100 entries).
- Methods: `log(level, tag, message)`, `getEntries()`, `clear()`.
- Thread-safe via concurrent locks or synchronized list.

### 3. Repository (`dev.ragnar.aegis.data.diagnostics`)

`DiagnosticsRepository`:
- `fun getSnapshot(): DiagnosticsSnapshot`
- `fun copyToClipboard(text: String): Boolean`

`AndroidDiagnosticsRepository`:
- Queries `Runtime.getRuntime()` for heap allocations.
- Queries `android.os.Build` for hardware & OS details.
- Queries `ContextCompat.checkSelfPermission` for manifest permissions.
- Queries `android.content.ClipboardManager` for clipboard export.

### 4. ViewModel & UI (`dev.ragnar.aegis.ui.console`)

- `DeveloperConsoleViewModel`: Exposes `StateFlow<DeveloperConsoleUiState>`, handles refresh, log filtering, and copy snapshot.
- `DeveloperConsoleScreen`: Tactical HUD styling matching AEGIS aesthetic:
  - Header: `AEGIS // DEV CONSOLE` with Close [✕] and Copy [📋] buttons.
  - Section 1: Launcher Process & Memory (Allocated Heap, Max Heap, Apps Tracked).
  - Section 2: Platform & Hardware (Model, Android Version, API, ABI, Security Patch).
  - Section 3: Permission Status (Grid of permissions with `[GRANTED]` / `[DENIED]`).
  - Section 4: Event Log Stream (Scrollable monospace terminal output with level filtering).

---

## Acceptance Criteria

- [x] Developer Console is accessible via `SettingsSheet`.
- [x] Displays genuine memory usage from `Runtime.getRuntime()`.
- [x] Displays genuine device manufacturer, model, Android version, and API level.
- [x] Displays actual permission check statuses for declared permissions.
- [x] In-memory ring buffer stores up to 100 entries without memory leak or disk I/O.
- [x] Copy Diagnostics generates a clean formatted report and copies to clipboard.
- [x] Monospace, cyber-tactical HUD aesthetic matching `AegisTheme`.
- [x] Unit tests cover log buffer circular capacity, snapshot formatting, and ViewModel state.
- [x] `./gradlew testDebugUnitTest` passes.
- [x] `./gradlew assembleDebug` passes.
- [x] `./gradlew lintDebug` passes with 0 errors.
