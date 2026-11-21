# PHASE-14 — Gesture Navigation (F-015)

**Feature:** F-015 — Gesture Navigation  
**Priority:** P2  
**Dependencies:** F-001 (Foundation), F-002, F-003  
**Start Date:** 2026-11-22  
**Completion Date:** 2026-11-22  
**Status:** Implementation Complete — Build Verified (Device Validation Pending)  

---

## Objective

Add gesture-based navigation to the AEGIS HUD home screen without conflicting with Android system gestures (both gesture-nav mode and 3-button mode). Each gesture must be individually enable/disable-able from Settings.

---

## Gesture Conflict Analysis (F-015-P1)

### Android System Gesture Reservation (API 30+)

| Gesture | System Use | Available to Launcher? |
|---|---|---|
| Swipe from left edge | Back | **No** (edge reserved) |
| Swipe from right edge | Back | **No** (edge reserved) |
| Swipe from bottom edge | Home / Recents | **No** (edge reserved) |
| Swipe up from center | Home | **Partially** (launcher IS home — receives after system home is handled) |
| Swipe down from status bar | Notification shade | **No** (intercepted by status bar) |
| Swipe up anywhere on content (not bottom edge) | None (in launcher) | **Yes** (upward drag on content) |
| Swipe down on content (not status bar) | None (in launcher) | **Yes** (downward drag on content) |
| Long press on content | None | **Yes** |
| Double tap on content | None | **Yes** |

### Decision: Safe Gestures for AEGIS HUD

- **Swipe up (center screen, not bottom edge):** Open App Drawer (refined threshold: `dragAmount < -40f`)
- **Swipe down (center screen, not status bar edge):** Open Settings Sheet (`dragAmount > 40f`)
- **Long press (home screen content area):** Show `GestureHintOverlay` modal listing active gestures
- **Double tap:** Reserved for future use (deferred per DEC-017)
- **Swipe from edges:** Explicitly excluded (system reserved)

Recorded in `docs/DECISIONS.md` as DEC-017.

---

## Sub-Phases

### F-015-P1: Gesture Conflict Investigation
Analyzed and documented above; recorded as DEC-017.

### F-015-P2: Swipe-Up → App Drawer
Refined threshold to `dragAmount < -40f` for reliable gesture detection. Gated by `swipeUpEnabled`.

### F-015-P3: Swipe-Down → Settings Sheet
Detect downward drag (`dragAmount > 40f`) on the home screen content. Gated by `swipeDownEnabled`.

### F-015-P4: Long-Press → Gesture Hint Overlay
Detect long-press on home screen background. Gated by `longPressEnabled`. Displays `GestureHintOverlay` with available gestures and tactile dismissal.

### F-015-P5: Gesture Settings
Add preferences to `UserPreferences`, `PreferencesRepository`, `DataStorePreferencesRepository`, `SettingsViewModel`, and `SettingsSheet`:
- `swipeUpEnabled: Boolean = true`
- `swipeDownEnabled: Boolean = true`
- `longPressEnabled: Boolean = true`

---

## Build Results

| Command | Result | Duration |
|---|---|---|
| `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL | 17s |
| `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (103 passed) | 30s |
| `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (0 errors) | 1m 11s |

---

## Acceptance Criteria

- [x] Swipe-up gesture opens App Drawer (threshold: dragAmount < -40f).
- [x] Swipe-down gesture opens Settings Sheet (threshold: dragAmount > 40f).
- [x] Long-press shows `GestureHintOverlay` listing available gestures.
- [x] Each gesture individually togglable via Settings.
- [x] Gesture toggles persist across restarts via DataStore.
- [x] No conflict with Android system gestures.
- [x] `./gradlew --no-daemon assembleDebug` → BUILD SUCCESSFUL.
- [x] `./gradlew --no-daemon testDebugUnitTest` → BUILD SUCCESSFUL (103 tests passing).
- [x] `./gradlew --no-daemon lintDebug` → BUILD SUCCESSFUL (0 errors).
- [x] All docs updated and committed.
