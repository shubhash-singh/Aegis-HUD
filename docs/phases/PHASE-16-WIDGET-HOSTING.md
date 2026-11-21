# PHASE-16 — Widget Hosting (F-012)

## Phase Overview

| Field | Value |
|---|---|
| Phase ID | PHASE-16 |
| Feature ID | F-012 |
| Priority | P2 |
| Title | Android AppWidget Hosting & Tactical Widget Deck |
| Status | Implementation Complete — Build Verified (Device Validation Pending) |
| Dependencies | F-001 (Launcher Foundation), F-002 (HUD Design System), F-010 (Customization) |
| Key Deliverables | `AegisAppWidgetHost`, `WidgetRepository`, `DataStoreWidgetRepository`, `WidgetHostController`, `WidgetHostViewModel`, `HudWidgetContainer`, `WidgetPickerSheet`, `TacticalWidgetDeck`, `WidgetSerializer` |
| Verification Commands | `./gradlew --no-daemon testDebugUnitTest`<br>`./gradlew --no-daemon assembleDebug`<br>`./gradlew --no-daemon lintDebug` |

---

## 1. Objectives & Scope

### In Scope

- **F-012-P1: Feasibility & Permission Analysis**:
  - Investigated `BIND_APPWIDGET` permission requirements. Standard third-party launchers cannot obtain `BIND_APPWIDGET` silently on modern Android; they must invoke the system prompt `AppWidgetManager.ACTION_APPWIDGET_BIND`. Declared `<uses-permission android:name="android.permission.BIND_APPWIDGET" />` in `AndroidManifest.xml` and documented architectural contract in DEC-018.
- **F-012-P2: AppWidgetHost & Manager Integration**:
  - Created `AegisAppWidgetHost` with launcher host ID `2048`.
  - Tied `startListening()` in `onStart()` and `stopListening()` in `onStop()` in `MainActivity` with exception-safe `runCatching` blocks.
- **F-012-P3: Widget Selection & Configuration**:
  - Implemented `WidgetPickerSheet` listing installed widget providers queried via `AppWidgetManager.installedProviders`.
  - Filterable by application name or widget label via `HudSearchBar`.
  - Handled `ACTION_APPWIDGET_CONFIGURE` when provider specifies a configure activity, cleaning up allocated widget IDs if canceled.
- **F-012-P4: Cyber-HUD Widget Deck & Container**:
  - Implemented `HudWidgetContainer` framing third-party `RemoteViews` in frosted glassmorphism cyber-cards with glowing neon borders, specular highlights, and corner accents.
  - Provided `TacticalWidgetDeck` modal stack displaying all docked widgets with quick actions to add, resize, or purge modules.
- **F-012-P5: Widget Persistence**:
  - Implemented `WidgetRepository` and `DataStoreWidgetRepository` storing allocated widget IDs, provider components, and height allocations in AndroidX DataStore Preferences.
  - Implemented deterministic, zero-dependency `WidgetSerializer` using URL encoding for safe delimiter parsing.
- **F-012-P6: Resize & Error Handling**:
  - Supported height cycling between 140 DP (Compact), 200 DP (Medium), and 280 DP (Expanded).
  - Sent updated options bundles via `AppWidgetManager.updateAppWidgetOptions`.
  - Handled orphaned widget providers gracefully (uninstalled apps) with fallback UI and purge actions.
  - Wrapped `createView` in `runCatching` to safeguard the launcher against faulty third-party widget code.

### Out of Scope

- Arbitrary free-form desktop drag-and-drop widget resizing (HUD uses tactical modular slot heights).
- Sideloading external widget APKs outside standard Android provider APIs.

---

## 2. Architecture & Key Files

### Key Classes

| File | Purpose |
|---|---|
| `domain/widget/HostedWidget.kt` | Data models: `HostedWidget`, `WidgetProviderSummary`, `WidgetHostUiState` |
| `domain/widget/WidgetSerializer.kt` | Pure Kotlin delimiter serializer with URL-encoding for DataStore |
| `data/widget/WidgetRepository.kt` | Repository interface for hosted widgets |
| `data/widget/DataStoreWidgetRepository.kt` | DataStore-backed persistent storage of hosted widgets |
| `data/widget/AegisAppWidgetHost.kt` | Specialized `AppWidgetHost` with host ID `2048` |
| `ui/widget/WidgetHostController.kt` | Controller contract and `LocalWidgetHostController` CompositionLocal |
| `ui/widget/WidgetHostViewModel.kt` | State management for widgets, search filtering, height cycling, and modals |
| `ui/widget/HudWidgetContainer.kt` | Cyber-styled HUD container framing the `AndroidView(AppWidgetHostView)` |
| `ui/widget/WidgetPickerSheet.kt` | Cybernetic bottom sheet modal for searching and selecting widget providers |
| `ui/widget/TacticalWidgetDeck.kt` | Tactical HUD stack modal displaying all docked widgets |
| `MainActivity.kt` | Lifecycle management (`startListening`/`stopListening`) and ActivityResult contracts |
| `ui/home/TopBar.kt` | Top bar header with optional `[WIDGETS: N]` pill |
| `ui/home/HomeScreen.kt` | Wired `WidgetHostViewModel`, `TacticalWidgetDeck`, and `WidgetPickerSheet` |
| `ui/settings/SettingsSheet.kt` | Added "Tactical Widgets" configuration section |

---

## 3. Decisions & Trade-offs

- **DEC-018: AppWidgetHost Architecture, Permission Model, and Cybernetic Containment**:
  - Detailed in `docs/DECISIONS.md`. Host ID pinned to `2048`.
  - User authorization handled via system prompt (`ACTION_APPWIDGET_BIND`).
  - Isolated third-party crashes with `runCatching` inside `AndroidView` factory.

---

## 4. Verification & Validation

### Automated Tests

- `WidgetSerializerTest`: 6 unit tests covering round-trips, empty inputs, malformed strings, special characters/punctuation in labels, and dimension cycling.
- `WidgetHostViewModelTest`: 7 unit tests covering initial state, toggle transitions, adding/removing widgets, height cycling, and search query filtering.

### Verification Commands

```bash
./gradlew --no-daemon testDebugUnitTest
./gradlew --no-daemon assembleDebug
./gradlew --no-daemon lintDebug
```

---

## 5. Artifacts Produced

- `app/build/outputs/apk/debug/app-debug.apk`
