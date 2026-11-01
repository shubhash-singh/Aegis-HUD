# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-01 (Phase 3 complete)
**Current Phase:** PHASE-03 — Implementation Complete, Build Verified
**Next Phase:** PHASE-04 — App Discovery & Launching

---

## Immediate Next Tasks

### Task 1: Phase 1 Device Validation (still pending)

**Phase:** PHASE-01 — B-002

Connect a physical Android device and validate launcher behavior (see Phase 1 docs).

### Task 2: Begin Phase 4 — App Discovery & Launching

**Phase:** PHASE-04 — F-004

**Objective:**
Query PackageManager for launchable apps, display icons and names in a grid drawer, and launch apps reliably. Wire the QuickLaunchRow and BottomNavBar drawer button.

**Prerequisites:**
- Phase 3 home screen complete (done)

**Read before starting:**
- `docs/phases/PHASE-04-APP-DISCOVERY.md` (to be created)
- `docs/FEATURE_PLAN.md` F-004 section
- `docs/ARCHITECTURE.md` Section 9 (background work policy — BroadcastReceiver for package changes)

---

## Phase 3 Completion Summary

**What was completed:**
- ClockViewModel with viewModelScope coroutine, second-aligned ticking
- HudClock composable (HH:MM bright + :SS dimmed)
- TopBar (system label, clock, date, status)
- QuickLaunchRow (5 placeholder slots)
- BottomNavBar (search placeholder + drawer button)
- Full dashboard HomeScreen assembling all four layout zones
- 5 new ClockViewModel unit tests

**What was validated:**
- `./gradlew assembleDebug` — BUILD SUCCESSFUL (6s)
- `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL (3s, 12 tests)
- `./gradlew lintDebug` — 0 errors
- APK: 11MB

**Blockers:**
- B-002: Phase 1 device tests still pending

---

## Commands to Run When Resuming

```bash
git status
git log --oneline -5
./gradlew assembleDebug
cat docs/NEXT_STEPS.md
```
