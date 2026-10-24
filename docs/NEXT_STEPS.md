# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-10-25 (Phase 1 implementation complete)
**Current Phase:** PHASE-01 — Implementation Complete, Device Validation Pending
**Next Phase:** PHASE-02 — HUD Design System

---

## Immediate Next Tasks

### Task 1: Phase 1 Device Validation (when device is available)

**Phase:** PHASE-01 — F-001-P3 through F-001-P5

Connect a physical Android device and run:

1. `./gradlew installDebug` or `adb install -r app/build/outputs/apk/debug/app-debug.apk`
2. Open Settings > Apps > Default Apps > Home App. Verify AEGIS appears (AC-LAUNCH-01).
3. Select AEGIS as default home. Press Home. Verify AEGIS renders (AC-LAUNCH-02).
4. Press Back on home screen. Verify launcher does not close (AC-LAUNCH-03, DT-MANIFEST-01).
5. Open Recents. Verify AEGIS does not appear as a card (DT-MANIFEST-02).
6. Press Home repeatedly. Verify no stacked instances (DT-MANIFEST-01).
7. Run `adb shell am kill dev.ragnar.aegis`, press Home. Verify clean restart (DT-MANIFEST-03, DT-MANIFEST-04).

After device validation passes, update Phase 1 status from "Implementation Complete — Device Validation Pending" to "Complete" in PROGRESS_TRACKER.md.

### Task 2: Begin Phase 2 — HUD Design System

**Phase:** PHASE-02 — F-002

**Objective:**
Implement the full AEGIS HUD design system: color palette with CompositionLocal, typography with bundled JetBrains Mono and Inter fonts, spacing tokens, HUD panel components, status indicators, and responsive layout handling.

**Prerequisites:**
- Phase 1 device validation passed (or device validation documented as pending with no blocking issues).

**Read before starting:**
- `docs/phases/PHASE-02-DESIGN-SYSTEM.md` (to be created)
- `docs/UI_UX_SPECIFICATION.md`
- `docs/ARCHITECTURE.md` Section 7 (Theming)

---

## Phase 1 Completion Summary

**What was completed:**
- Full Android project scaffold (Gradle, Kotlin, Compose)
- Gradle 8.9 wrapper bootstrapped with SHA-256 verification (DEC-012)
- AndroidManifest.xml with launcher registration, singleTask, stateNotNeeded, excludeFromRecents
- MainActivity with enableEdgeToEdge() and setContent
- Minimal HomeScreen with BackHandler and "AEGIS" placeholder
- AegisTheme, Color, Typography stubs with spec-compliant tokens
- Stub unit and instrumented tests

**What was validated:**
- `./gradlew assembleDebug` — BUILD SUCCESSFUL (1m 46s)
- `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL (36s, 2 tests)
- `./gradlew lintDebug` — 0 errors, 34 warnings (all advisory)
- APK: 9.4MB at `app/build/outputs/apk/debug/app-debug.apk`

**What remains unverified:**
- DT-MANIFEST-01: Repeated Home presses don't stack instances
- DT-MANIFEST-02: AEGIS not in Recents
- DT-MANIFEST-03: Clean restart after process kill
- DT-MANIFEST-04: No crash from null bundle in onCreate

**Blockers:**
- B-002: No physical device connected during this session.

---

## Files to Inspect When Resuming

1. `AGENTS.md` — session instructions
2. `docs/PROGRESS_TRACKER.md` — overall status
3. `docs/NEXT_STEPS.md` — this file
4. `docs/UI_UX_SPECIFICATION.md` — HUD visual design for Phase 2
5. `docs/ARCHITECTURE.md` Section 7 — theming architecture

---

## Commands to Run When Resuming

```bash
git status
git log --oneline -5
./gradlew assembleDebug
cat docs/NEXT_STEPS.md
```
