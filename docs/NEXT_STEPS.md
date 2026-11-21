# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22 (Phase 14 build verified)
**Current Phase:** PHASE-14 — Gesture Navigation (F-015) — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-15 — Reliability and Performance (F-016)

---

## Completed This Session

- Implemented **PHASE-14 (Gesture Navigation — F-015)**:
  - Conducted gesture conflict analysis with Android system navigation (DEC-017).
  - Implemented swipe-up gesture (`dragAmount < -40f`) to open App Drawer (gated by `swipeUpEnabled`).
  - Implemented swipe-down gesture (`dragAmount > 40f`) to open Settings Sheet (gated by `swipeDownEnabled`).
  - Implemented long-press gesture on home screen to show `GestureHintOverlay` modal (gated by `longPressEnabled`).
  - Created `GestureHintOverlay` composable with semi-transparent background and monospace tactical styling.
  - Added `swipeUpEnabled`, `swipeDownEnabled`, and `longPressEnabled` to `UserPreferences`, `PreferencesRepository`, `DataStorePreferencesRepository`, `SettingsViewModel`, and `SettingsSheet`.
  - Added Gesture Navigation section to `SettingsSheet` with individual gesture toggles.
  - Added 5 new unit tests in `GestureSettingsViewModelTest` (103 total project tests passing).
  - Validated: `assembleDebug` (17s), `testDebugUnitTest` (30s, 103 passed), `lintDebug` (1m 11s, 0 errors).

---

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 14. (Low severity; development can proceed).

---

## Exact Next Task

Begin **PHASE-15 (Reliability and Performance — F-016)**:
1. Review `docs/FEATURE_PLAN.md` for F-016 scope:
   - F-016-P1: Define measurable performance targets (startup time, frame rate, memory).
   - F-016-P2: Startup time profiling & baseline architecture validation.
   - F-016-P3: Frame rate profiling: radar animation and drawer scroll.
   - F-016-P4: Battery consumption analysis (idle, animation, app launch).
   - F-016-P5: Lifecycle stress testing: repeated home presses, configuration changes, process death recovery.
   - F-016-P6: Memory leak detection: receiver/callback registration audit.
   - F-016-P7: Full regression test run across all features.
2. Create `docs/phases/PHASE-15-RELIABILITY-AND-PERFORMANCE.md`.
3. Audit memory leaks, receivers, callbacks, and lifecycle hooks.
4. Add performance verification unit tests.
5. Validate via `./gradlew`.

---

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-14-GESTURE-NAVIGATION.md`
- `docs/FEATURE_PLAN.md` (F-016)
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
