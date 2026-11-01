# PHASE-03-HOME-SCREEN.md — AEGIS HUD Launcher

**Phase ID:** PHASE-03
**Title:** HUD Home Screen Dashboard
**Status:** Implementation Complete — Build Verified (2026-11-01)
**Dependency:** PHASE-02 complete

---

## Objective

Build the full HUD home screen dashboard layout per UI_UX_SPECIFICATION.md Section 5: TopBar zone (system label + live clock + date), center HUD zone (telemetry panels), quick-launch zone (app slots), and bottom nav zone (drawer access). Clock must tick every second without unnecessary recomposition.

---

## Subtasks

| # | Task | Status |
|---|---|---|
| 1 | Create ClockViewModel with live time state (viewModelScope coroutine) | ✅ Done |
| 2 | Create HudClock composable (HH:MM:SS with seconds dimmed) | ✅ Done |
| 3 | Create TopBar composable (system label + clock + date + status) | ✅ Done |
| 4 | Create QuickLaunchRow composable (static placeholder slots) | ✅ Done |
| 5 | Create BottomNavBar composable (drawer button placeholder) | ✅ Done |
| 6 | Assemble full dashboard layout in HomeScreen.kt | ✅ Done |
| 7 | Add unit tests for ClockViewModel | ✅ Done (5 tests) |
| 8 | Run ./gradlew assembleDebug | ✅ BUILD SUCCESSFUL (6s) |
| 9 | Run ./gradlew testDebugUnitTest | ✅ BUILD SUCCESSFUL (12 tests) |
| 10 | Run ./gradlew lintDebug | ✅ 0 errors |
| 11 | Update documentation and commit | ✅ Done |

---

## Acceptance Criteria

- [ ] Dashboard layout matches UI_UX_SPECIFICATION.md Section 5 zones.
- [ ] Clock updates every second without recomposing the entire screen.
- [ ] Date displays correctly.
- [ ] System label shows "RAGNAR // AEGIS".
- [ ] Quick-launch slots render as empty placeholders (real app data deferred to Phase 4).
- [ ] Drawer button is visible and tappable (navigation deferred to Phase 4).
- [ ] All composables use AegisTheme.* tokens — no hardcoded values.
- [ ] `./gradlew assembleDebug` succeeds.
- [ ] `./gradlew testDebugUnitTest` succeeds.
