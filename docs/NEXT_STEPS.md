# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22 (Phase 15 build verified)
**Current Phase:** PHASE-15 — Reliability and Performance (F-016) — Implementation Complete, Build Verified (Device Validation Pending)
**Next Phase:** PHASE-16 — Widget Hosting (F-012)

---

## Completed This Session

- Implemented **PHASE-15 (Reliability and Performance — F-016)**:
  - Formulated and documented performance targets: cold start $\le 1$s, 60fps frame rate, idle heap $\le 45$MB, 0 leaks.
  - Optimized 3D neural brain visualization render loop: static precomputation of coordinates/edges and `@Volatile` dimension-keyed memoization in `NeuralGeometry` eliminating frame-by-frame memory allocations.
  - Hardened lifecycle teardown: wrapped all `unregisterReceiver` and `unregisterNetworkCallback` calls in `runCatching` across `AndroidAppRepository`, `AndroidNetworkRepository`, `AndroidBatteryRepository`, and `HomeScreen`.
  - Added 5 new regression tests in `PerformanceReliabilityAuditTest` covering geometry memoization, high-throughput normalizer benchmarks, concurrent log buffer stress tests, and default preference footprint.
  - Validated: `assembleDebug` (21s), `testDebugUnitTest` (20s, 108 passed), `lintDebug` (1m 13s, 0 errors).

---

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest
   tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4 through 15. (Low severity; development can proceed).

---

## Exact Next Task

Begin **PHASE-16 (Widget Hosting — F-012)**:
1. Review `docs/FEATURE_PLAN.md` for F-012 scope:
   - F-012-P1: Feasibility investigation & `BIND_APPWIDGET` permission analysis.
   - F-012-P2: AppWidgetHost lifecycle integration.
   - F-012-P3: Widget selection flow via system picker.
   - F-012-P4: HUD-framed widget host container composable (`AndroidView`).
   - F-012-P5: Widget configuration and persistence in DataStore.
   - F-012-P6: Widget removal and error recovery.
2. Create `docs/phases/PHASE-16-WIDGET-HOSTING.md`.
3. Implement `AppWidgetHost` infrastructure with cyber-HUD container frames.
4. Add unit tests and verify via `./gradlew`.

---

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/phases/PHASE-15-RELIABILITY-AND-PERFORMANCE.md`
- `docs/FEATURE_PLAN.md` (F-012)

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
