# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22 (Phase 13 build verified)
**Current Phase:** PHASE-13 — Context-Aware HUD (F-013) — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-14 — Gesture Navigation (F-015)

---

## Completed This Session

- Implemented **PHASE-13 (Context-Aware HUD — F-013)**:
  - Created `ContextTrigger` sealed interface: `TimeWindow`, `BatteryLevelBelow`, `PowerSaveActive`, `NetworkDisconnected`.
  - Created `ContextAction` sealed interface: `SetAccent`, `SetAnimationSpeed`, `ShowAlert`.
  - Created `ContextRule`, `ContextSnapshot`, `ContextEvaluationResult`, `StandardContextRules` with 5 predefined protocols.
  - Implemented `ContextRuleEvaluator` — pure evaluation engine, priority-sorted, midnight wrap-around for time windows.
  - Built `TacticalAlertBanner` composable — pulsing HUD alert banner.
  - Added `contextAwareHudEnabled` and `enabledContextRules` to `UserPreferences` and all repository/ViewModel layers.
  - Wired context evaluation into `HomeScreen` — builds live `ContextSnapshot`, applies effective accent and animation speed, shows alert banner.
  - Added Context-Aware HUD section in `SettingsSheet` with master toggle and per-protocol ACTIVE/STANDBY rows.
  - Added 11 unit tests in `ContextRuleEvaluatorTest` (98 total project tests passing).
  - Validated: `assembleDebug` (2m 7s), `testDebugUnitTest` (31s, 98 passed), `lintDebug` (0 errors).

---

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 13. (Low severity; development can proceed).

---

## Exact Next Task

Begin **PHASE-14 (Gesture Navigation — F-015)**:
1. Review `docs/FEATURE_PLAN.md` for F-015 scope.
2. Create `docs/phases/PHASE-14-GESTURE-NAVIGATION.md` with objective, sub-phases, architecture, acceptance criteria.
3. Implement enhanced gesture logic in `HomeScreen.kt`:
   - F-015-P2: Upward swipe → App Drawer (already exists, refine with threshold)
   - F-015-P3: Downward swipe → open Settings Sheet
   - F-015-P4: Long-press → show gesture hint overlay
   - F-015-P5: Per-gesture enable/disable in Settings
4. Add gesture settings to `UserPreferences`, `PreferencesRepository`, `SettingsViewModel`, `SettingsSheet`.
5. Add unit tests for gesture settings logic.
6. Run `assembleDebug`, `testDebugUnitTest`, `lintDebug`.
7. Update all docs and commit.

---

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-14-GESTURE-NAVIGATION.md`
- `docs/FEATURE_PLAN.md` (F-015)
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
