# PHASE-10 — Full-Screen AI Command Interface

**Feature:** F-003 (HUD Home Screen redesign), F-007 (AI Core Visualization), F-020 (Swipe-Up App Drawer Gesture)
**Dependencies:** PHASE-02, PHASE-03, PHASE-06, PHASE-07, PHASE-08, PHASE-09
**Status:** Planning Complete — Implementation Not Started
**Created:** 2026-11-21
**Design Reference:** `docs/UI_UX_SPECIFICATION.md` §5–§7

---

## Objective

Redesign the AEGIS home screen into a full-screen futuristic AI command interface.

Replace the existing radar composable with a holographic neural intelligence visualization (`AICoreVisualization`) that extends behind all floating information panels across the entire screen.

Remove all stacked card layouts, the bottom launcher dock, the home-screen search bar, and the APPS button. Replace with floating semi-transparent HUD panels and a swipe-up gesture drawer.

---

## Scope

### Remove (deprecated UI)

| Item | Location | Replacement |
|---|---|---|
| `RadarDisplay` composable | `ui/components/RadarDisplay.kt` | `AICoreVisualization` |
| Bottom launcher dock row | `HomeScreen.kt` | `AppDrawerGestureIndicator` |
| App shortcut row (5 icons) | `HomeScreen.kt` | Swipe-up drawer |
| Home-screen `HudSearchBar` | `HomeScreen.kt` | Lives only inside drawer |
| APPS / drawer open button | `HomeScreen.kt` | Swipe gesture |
| Stacked Telemetry card | `HomeScreen.kt` | `FloatingTelemetryPanel` |
| Stacked System Status card | `HomeScreen.kt` | `FloatingSystemStatusPanel` |

### Add (new composables)

| Composable | Package | Purpose |
|---|---|---|
| `NeuralGeometry.kt` | `ui/home/visualization/` | Static cached geometry (node positions, ring radii, edge list) |
| `NeuralAnimationState.kt` | `ui/home/visualization/` | `InfiniteTransition` animation state; isolated from panel data |
| `AICoreVisualization.kt` | `ui/home/visualization/` | Full-screen Canvas neural brain visualization |
| `FloatingCorePanel.kt` | `ui/home/panels/` | Top-left: AEGIS AT CORE + app version |
| `FloatingAiStatusPanel.kt` | `ui/home/panels/` | Top-right: AI STATUS state |
| `FloatingPerformancePanel.kt` | `ui/home/panels/` | Left side: CPU + RAM |
| `FloatingDevicePanel.kt` | `ui/home/panels/` | Right side: Temperature + Battery |
| `FloatingIntelligencePanel.kt` | `ui/home/panels/` | Lower-left: CONTEXT / VOICE / VISION / AUTOMATION |
| `FloatingTelemetryPanel.kt` | `ui/home/panels/` | Lower-right: Batt temp, uptime, voltage |
| `FloatingSystemStatusPanel.kt` | `ui/home/panels/` | Bottom: Network + storage |
| `AppDrawerGestureIndicator.kt` | `ui/drawer/` | Breathing chevrons + "SWIPE UP" text |

---

## Sub-Phases

| ID | Title | Status |
|---|---|---|
| PHASE-10-P1 | Remove deprecated home screen UI | Not Started |
| PHASE-10-P2 | Implement `NeuralGeometry` and `NeuralAnimationState` | Not Started |
| PHASE-10-P3 | Implement `AICoreVisualization` Canvas composable | Not Started |
| PHASE-10-P4 | Implement 7 floating information panels | Not Started |
| PHASE-10-P5 | Implement swipe-up drawer gesture + `AppDrawerGestureIndicator` | Not Started |
| PHASE-10-P6 | Integrate into `HomeScreen` with responsive layout | Not Started |
| PHASE-10-P7 | Build validation and unit tests | Not Started |

---

## AICoreVisualization — Visual Layer Specification

Rendered entirely via Compose `Canvas`. Layers (bottom to top):

1. **Background** — `#050A0E` fill (from parent)
2. **Concentric scanning rings** — 4–6 rings, expanding outward, fading with `EaseOut`
3. **Dynamic connection lines** — edges between neural nodes; alpha varies 0.3–0.8
4. **Data pulses** — bright dots traveling along connection edges
5. **Floating particles** — small glowing points, constant slow upward drift
6. **Rotating circular indicators** — inner ring (6s) + outer ring (10s), counter-rotating
7. **Vertical holographic projection beam** — central vertical light column, slow alpha pulse
8. **Circular energy platform** — flat ellipse at base of brain, slow rotation
9. **Neural brain cluster** — central interconnected node mass; slow pulse scale 1.0 → 1.05
10. **Horizontal waveforms** — subtle sine waves extending left/right from center

### Geometry Cache (`NeuralGeometry`)

Computed once from screen dimensions, stored as `remember`-cached data class:

```kotlin
data class NeuralGeometry(
    val centerX: Float,
    val centerY: Float,
    val brainRadius: Float,          // ~25% of min(width, height)
    val outerRingRadius: Float,      // ~45% of min(width, height)
    val nodes: List<NodePoint>,      // 20–30 nodes, positions relative to center
    val edges: List<Pair<Int,Int>>,  // node index pairs forming connections
    val ringRadii: List<Float>,      // concentric ring distances
    val platformRadiusX: Float,
    val platformRadiusY: Float,
)
```

### Animation State Isolation (`NeuralAnimationState`)

All animation state must be derived from `rememberInfiniteTransition()` inside this class. The class is instantiated once and passed to `AICoreVisualization`. Floating panel data updates must **not** trigger `AICoreVisualization` recomposition.

```kotlin
// Key rule: AICoreVisualization accepts only stable parameters
@Composable
fun AICoreVisualization(
    modifier: Modifier = Modifier,
    animationSpeedMultiplier: Float = 1f,
    accentColor: Color,
    // NO data state params here — panels are siblings, not children
)
```

---

## Floating Panel Layout — Responsive Positioning

Use `BoxWithConstraints` to derive available screen space. Position panels using fractional offsets:

| Panel | Horizontal | Vertical |
|---|---|---|
| `FloatingCorePanel` | 2% from left | 12% from top (below header) |
| `FloatingAiStatusPanel` | 2% from right (end-aligned) | 12% from top |
| `FloatingPerformancePanel` | 2% from left | 38% from top (vertically centered) |
| `FloatingDevicePanel` | 2% from right (end-aligned) | 38% from top |
| `FloatingIntelligencePanel` | 2% from left | 68% from top |
| `FloatingTelemetryPanel` | 2% from right (end-aligned) | 68% from top |
| `FloatingSystemStatusPanel` | Centered, 10% margins each side | 85% from top |

> [!WARNING]
> Never use hardcoded `dp` offsets derived from a specific device's pixel dimensions. Always compute from `maxWidth` / `maxHeight` available in `BoxWithConstraints`.

---

## Swipe-Up App Drawer Gesture

### Detection

Use `Modifier.pointerInput` with `detectVerticalDragGestures` on the `HomeScreen` root. Open drawer when cumulative upward drag exceeds `48.dp`.

### Animation

```kotlin
AnimatedVisibility(
    visible = drawerOpen,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
) {
    AppDrawerScreen(...)
}
```

### System Navigation Conflict

Apply `WindowInsets.navigationBars` padding to the gesture detection area to avoid conflicting with the system navigation gesture zone (bottom ~32dp on most devices with gesture navigation).

---

## Acceptance Criteria

- [ ] Neural visualization fills the full screen behind all panels.
- [ ] All 7 floating panels display correct live data.
- [ ] No stacked card layout exists below the neural visualization.
- [ ] No app shortcut row, search bar, or APPS button on the home screen.
- [ ] Swipe-up gesture opens the app drawer with animation.
- [ ] Swipe-down or back gesture dismisses the drawer.
- [ ] `AppDrawerGestureIndicator` shows with breathing animation.
- [ ] All existing app-discovery and search functionality still works from within the drawer.
- [ ] Responsive layout: panels do not overlap on 360dp, 390dp, 412dp, or 480dp width screens.
- [ ] Animations pause when battery-saver mode is active.
- [ ] `./gradlew assembleDebug` — BUILD SUCCESSFUL.
- [ ] `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL, all prior tests plus new tests pass.
- [ ] `./gradlew lintDebug` — 0 errors.

---

## Unit Tests

| ID | Test | Coverage |
|---|---|---|
| UT-010-01 | `NeuralGeometry` nodes do not exceed canvas bounds | `NeuralGeometry` |
| UT-010-02 | `NeuralGeometry` edges reference valid node indices | `NeuralGeometry` |
| UT-010-03 | `FloatingSystemStatusPanel` formats speeds correctly (kb/s, Mb/s) | Formatter |
| UT-010-04 | `FloatingTelemetryPanel` formats uptime correctly (seconds → HH:MM:SS) | Formatter |
| UT-010-05 | `FloatingIntelligencePanel` reflects available/unavailable states correctly | ViewModel mapping |
| UT-010-06 | Drawer gesture state: upward drag > threshold → open; downward drag → close | Gesture state logic |

---

## Files to Create / Modify

### New Files

- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/NeuralGeometry.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/NeuralAnimationState.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/visualization/AICoreVisualization.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingCorePanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingAiStatusPanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingPerformancePanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingDevicePanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingIntelligencePanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingTelemetryPanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingSystemStatusPanel.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/drawer/AppDrawerGestureIndicator.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/ui/home/visualization/NeuralGeometryTest.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/ui/home/panels/FloatingPanelFormatterTest.kt`
- `docs/phases/PHASE-10-AI-CORE-VISUALIZATION.md` ← this file

### Modified Files

- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt` — Full redesign
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeViewModel.kt` — Add AI status state + intelligence availability state
- `app/src/main/kotlin/dev/ragnar/aegis/telemetry/system/SystemInfoRepository.kt` — Add CPU, RAM, storage telemetry

### Archived (do not delete — kept for reference)

- `app/src/main/kotlin/dev/ragnar/aegis/ui/components/RadarDisplay.kt` — Superseded by `AICoreVisualization`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| — | — | — | Pending implementation |
