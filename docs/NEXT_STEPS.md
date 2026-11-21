# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21 (Phase 10 — Full-Screen AI Command Interface design finalized)
**Current Phase:** PHASE-10 — Full-Screen AI Command Interface — Planning Complete, Implementation Not Started
**Next Phase:** PHASE-10 implementation

---

## Completed This Session

- Completed **PHASE-09 (Customization — F-010)** — build verified, 58 unit tests passing.
- Received and incorporated the **Full-Screen AI Command Interface redesign brief** into all project documentation:
  - `docs/UI_UX_SPECIFICATION.md` — Full new screen layout, floating panel specifications, neural visualization spec, gesture indicator, removed elements list.
  - `docs/ARCHITECTURE.md` — Updated package structure with new `visualization/` and `panels/` sub-packages; `AICoreVisualization`, 7 floating panel composables, `AppDrawerGestureIndicator`.
  - `docs/FEATURE_PLAN.md` — Updated F-003 (HUD Home Screen) to reflect full-screen redesign; updated F-007 (Animated HUD Graphics) superseded by `AICoreVisualization`; added F-020 (Swipe-Up App Drawer Gesture).
  - `docs/REQUIREMENTS.md` — Updated FR-002 (Home Screen) and added FR-007 (Full-Screen Neural Visualization), FR-008 (Swipe-Up App Drawer), NFR-010 (Canvas Performance).
  - `docs/DECISIONS.md` — Recorded DEC-014 (Full-Screen AI Core replaces Radar), DEC-015 (Floating Panels replace stacked cards), DEC-016 (Swipe-Up replaces launcher dock).
  - `docs/CHANGELOG.md` — Added Phase 10 planning entry.
  - `docs/phases/PHASE-10-AI-CORE-VISUALIZATION.md` — New phase document.
  - `docs/PROGRESS_TRACKER.md` — Added PHASE-10 entry.

---

## Outstanding Blockers

1. **B-002:** Connect a physical Android device or emulator and run the pending Phase 1 manifest tests (`DT-MANIFEST-01` through `DT-MANIFEST-04`), plus manual validation of Phases 4–9. (Low severity; development can proceed.)

---

## Exact Next Task

Begin **PHASE-10 (Full-Screen AI Command Interface)**:

### Step 1 — Read before coding
1. `docs/phases/PHASE-10-AI-CORE-VISUALIZATION.md`
2. `docs/UI_UX_SPECIFICATION.md` (§5 Layout, §6 Components, §7 Animation)
3. `docs/ARCHITECTURE.md` (§3 Package Structure — new `visualization/` and `panels/` sub-packages)

### Step 2 — Inspect existing code
4. Inspect `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`
5. Inspect `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarDisplay.kt`
6. Identify all stacked cards, the launcher dock row, the search bar placement, and the APPS button.

### Step 3 — Remove deprecated UI
7. Remove the bottom launcher dock row (5 app shortcuts).
8. Remove the home-screen search bar.
9. Remove the APPS button.
10. Remove the stacked Telemetry card below the radar.
11. Remove the stacked System Status card below the radar.
12. Remove/archive `RadarDisplay.kt` (superseded by `AICoreVisualization`).

### Step 4 — Implement visualization
13. Create `NeuralGeometry.kt` — static geometry cache (node positions, ring radii, connection edges).
14. Create `NeuralAnimationState.kt` — `InfiniteTransition`-based animation state container.
15. Create `AICoreVisualization.kt` — full-screen Canvas composable with all visual layers.

### Step 5 — Implement floating panels
16. Create `FloatingCorePanel.kt`
17. Create `FloatingAiStatusPanel.kt`
18. Create `FloatingPerformancePanel.kt`
19. Create `FloatingDevicePanel.kt`
20. Create `FloatingIntelligencePanel.kt`
21. Create `FloatingTelemetryPanel.kt`
22. Create `FloatingSystemStatusPanel.kt`

### Step 6 — Implement gesture drawer
23. Create `AppDrawerGestureIndicator.kt` (breathing chevrons).
24. Implement swipe-up gesture detection on `HomeScreen` (using `detectVerticalDragGestures`).
25. Wire drawer open/close animation (`AnimatedVisibility` slide-up).

### Step 7 — Integrate and validate
26. Compose the new `HomeScreen.kt` with all elements on a `Box` layout.
27. Verify responsive positioning (avoid fixed px coordinates; use `BoxWithConstraints` or `LocalConfiguration`).
28. Run `./gradlew assembleDebug`.
29. Run `./gradlew testDebugUnitTest`.
30. Run `./gradlew lintDebug`.
31. Update `PROGRESS_TRACKER.md`, `PHASE_LOG.md`, `CHANGELOG.md`.

---

## Files to Inspect Before Resuming

- `docs/phases/PHASE-10-AI-CORE-VISUALIZATION.md`
- `docs/UI_UX_SPECIFICATION.md`
- `docs/PROGRESS_TRACKER.md`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarDisplay.kt`

## Commands to Run When Resuming

```bash
git status
git log --oneline -5
./gradlew --no-daemon testDebugUnitTest
cat docs/NEXT_STEPS.md
```
