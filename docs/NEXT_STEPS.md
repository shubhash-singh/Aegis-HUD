# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22 (hardware diagnosis recorded)
**Current Phase:** PHASE-15 device-performance remediation — B-005
**Project Status:** All planned feature implementation is build verified; target-device performance acceptance failed and blocks release acceptance.

---

## Completed This Session

- Completed an ADB device diagnosis while AEGIS was the top-resumed launcher on a OnePlus CPH2401 (Android 14, 90 Hz).
- Confirmed B-005: sustained roughly 2.5-core CPU load, 36 ms median frame time, 10.69% jank, 160.97 MB PSS / 301.70 MB RSS, and severe 52.0°C skin thermal state.
- Isolated the root cause to display-rate rendering of overlapping 3D and 2D neural meshes plus pulse/glow/ring/code-rain layers.
- Added `docs/reports/2026-11-22-device-performance-diagnosis.md`; updated Phase 15, tracker, and B-005 to reflect the failed hardware performance gate.

---

## Outstanding Blockers

1. **B-005:** Default neural HUD rendering violates the Phase 15 device-performance target. P0 remediation is required before release/device acceptance.
2. **B-002:** Functional device validation remains pending after B-005 remediation: manifest tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`) and manual validation for Phases 4–16.

---

## Exact Next Task

**P0 Neural HUD Performance Remediation (B-005)**:
1. Remove or make optional the legacy 2D neural mesh so the home screen has one mesh renderer.
2. Replace display-rate `InfiniteTransition` invalidation with an explicit quality-tier clock: 30 FPS battery mode and 60 FPS maximum mode.
3. Automatically activate static/low-power rendering for power save, low battery, and severe thermal state; use a capped profile by default.
4. Add device validation that captures 30-second per-thread CPU, `gfxinfo`, `meminfo`, and thermal samples for normal and battery modes.
5. Do not claim Phase 15 device acceptance until all report gates pass.

---

## Files to Inspect Before Resuming

- `docs/PROGRESS_TRACKER.md`
- `docs/reports/2026-11-22-device-performance-diagnosis.md`
- `docs/phases/PHASE-15-RELIABILITY-AND-PERFORMANCE.md`
- `docs/BUG_TRACKER.md` (B-005)
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/AICoreVisualization.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/NeuralAnimationState.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/NeuralMeshDraw.kt`

## Commands to Run When Resuming

```bash
git status
git log --oneline -10
./gradlew --no-daemon testDebugUnitTest
adb devices
./gradlew installDebug
adb shell top -b -n 2 -d 5 -p "$(adb shell pidof dev.ragnar.aegis)" -o PID,TID,%CPU,RES,CMD
adb shell dumpsys gfxinfo dev.ragnar.aegis
adb shell dumpsys meminfo dev.ragnar.aegis
adb shell dumpsys thermalservice
```
