# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-01 (Phase 2 complete)
**Current Phase:** PHASE-02 — Implementation Complete, Build Verified
**Next Phase:** PHASE-03 — HUD Home Screen

---

## Immediate Next Tasks

### Task 1: Phase 1 Device Validation (still pending)

**Phase:** PHASE-01 — B-002

Connect a physical Android device and run:

1. `./gradlew installDebug`
2. Settings > Default Apps > Home > select AEGIS
3. Verify AEGIS renders when Home is pressed
4. Verify Back does not exit, AEGIS not in Recents, no stacked instances
5. `adb shell am kill dev.ragnar.aegis` > press Home > verify clean restart

### Task 2: Begin Phase 3 — HUD Home Screen

**Phase:** PHASE-03 — F-003

**Objective:**
Build the full HUD home screen dashboard: live clock display, system status bar, telemetry panels with real data (battery, network), quick-launch app row, and the overall screen layout from UI_UX_SPECIFICATION.md Section 5.

**Prerequisites:**
- Phase 2 design system complete (done)

**Read before starting:**
- `docs/phases/PHASE-03-HOME-SCREEN.md` (to be created)
- `docs/UI_UX_SPECIFICATION.md` Section 5 (layout), Section 6 (components)
- `docs/ARCHITECTURE.md` Section 9 (background work policy)

---

## Phase 2 Completion Summary

**What was completed:**
- Bundled JetBrains Mono (Regular, Bold) and Inter (Regular, Medium, SemiBold) — 5 TTF files
- CompositionLocal theming: `AegisColorScheme`, `AegisTypographyScheme`, `AegisSpacing`
- `AegisTheme` composable + accessor object (`AegisTheme.colors`, `.typography`, `.spacing`)
- `HudPanel` composable with borders, corner ticks, section header
- `StatusIndicator` composable with pulse animation
- `HudDivider` composable
- Updated HomeScreen demonstrating all components
- 5 new unit tests in ThemeTokensTest

**What was validated:**
- `./gradlew assembleDebug` — BUILD SUCCESSFUL (13s)
- `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL (3s, 7 tests)
- `./gradlew lintDebug` — 0 errors, 34 warnings
- APK: 11MB at `app/build/outputs/apk/debug/app-debug.apk`

**Blockers:**
- B-002: Phase 1 device tests still pending (no device connected)

---

## Files to Inspect When Resuming

1. `AGENTS.md`
2. `docs/PROGRESS_TRACKER.md`
3. `docs/NEXT_STEPS.md`
4. `docs/UI_UX_SPECIFICATION.md` Section 5
5. `docs/ARCHITECTURE.md` Section 9

---

## Commands to Run When Resuming

```bash
git status
git log --oneline -5
./gradlew assembleDebug
cat docs/NEXT_STEPS.md
```
