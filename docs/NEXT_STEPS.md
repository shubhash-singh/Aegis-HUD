# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21 (Phase 4 build verified)
**Current Phase:** PHASE-04 — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-05 — Search and Application Drawer (or device validation of Phase 1 & 4 when device available)

---

## Completed This Session

- Resolved NixOS AAPT2 dynamic loader blocker (B-003) via `patchelf --set-interpreter` (DEC-013).
- Fixed Compose compilation issues in `AppDrawer.kt` (removed ambiguous internal weight import) and `QuickLaunchRow.kt` (added missing items extension import).
- Validated Phase 4 implementation:
  - `./gradlew --no-daemon assembleDebug` — BUILD SUCCESSFUL (41s).
  - `./gradlew --no-daemon testDebugUnitTest` — BUILD SUCCESSFUL (31s, 15 tests passed with 0 failures).
  - `./gradlew --no-daemon lintDebug` — BUILD SUCCESSFUL (53s, 0 errors).
- Synchronized all project documentation (Progress Tracker, Bug Tracker, Risk Register, Decisions, Changelog, Phase 4 doc).

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phase 4 app discovery,
   icon rendering, launching, and package change observation. (Low severity; development can proceed to Phase 5).

## Exact Next Task

Begin **PHASE-05 (Application Search)**:
1. Create `docs/phases/PHASE-05-SEARCH.md`.
2. Implement real-time search filtering in `AppDrawerViewModel` / `AppDrawer`.
3. Wire the search bar in `BottomNavBar` / `HomeScreen`.
4. Add JVM unit tests for search filtering (case-insensitivity, prefix/substring matching, empty query).
5. Run build, unit tests, and lint to verify.

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-04-APP-DISCOVERY.md`
- `docs/FEATURE_PLAN.md` (F-005: Application Search)
- `app/src/main/kotlin/dev/ragnar/aegis/ui/apps/AppDrawerViewModel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/apps/AppDrawer.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/BottomNavBar.kt`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
```
