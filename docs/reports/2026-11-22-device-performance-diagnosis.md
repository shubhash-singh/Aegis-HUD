# AEGIS Device Performance Diagnosis

**Date:** 2026-11-22
**Device:** OnePlus CPH2401, Android 14 (API 34), 1080 × 2400, peak refresh rate 90 Hz
**Package / state:** `dev.ragnar.aegis`, PID 4056, top-resumed `MainActivity`
**Result:** **Fail — foreground CPU, frame pacing, thermal, and total memory targets require remediation before device acceptance.**

## Scope and Method

This diagnosis used read-only ADB commands while AEGIS was the active home launcher:

- `top` process and per-thread samples over 30 seconds.
- `dumpsys gfxinfo`, `meminfo`, `batterystats`, `battery`, `thermalservice`, and display settings.
- Source review of the active HUD rendering path.

The device was USB-powered during the capture. Therefore, the live thermal and CPU measurements are valid, but a new controlled battery-discharge rate was not measured in this run. BatteryStats values are historical values since the last charge/statistics window.

## Measured Findings

| Area | Evidence | Assessment |
|---|---|---|
| CPU | The five dominant AEGIS threads total about 249% CPU in a 5-second sample: app main 72.4%, `RenderThread` 73.4%, `hwuiTask0` 40.0%, `hwuiTask1` 40.6%, and Mali backend 23.2%. | **Critical** — roughly 2.5 CPU cores are continuously occupied on the home screen. |
| Frame pacing | `dumpsys gfxinfo`: 134,411 frames; 14,369 janky frames (10.69%); 50th/90th/95th/99th percentiles: 36/57/73/85 ms. | **Fail** — the 36 ms median misses both the 90 Hz 11.1 ms budget and the 60 Hz 16.7 ms target. |
| Render location | GPU percentiles are 7/7/8/10 ms (50th/90th/95th/99th). The main, RenderThread, and HWUI workers are hot. | **CPU-bound**, not GPU-bound. |
| Memory | Total PSS 160.97 MB; RSS 301.70 MB; native heap PSS 51.47 MB; graphics PSS 36.38 MB. No material swap for the app (136 KB). | **Needs reduction**, but not an active memory-leak or out-of-memory incident. |
| Historical battery attribution | BatteryStats identifies `dev.ragnar.aegis` as UID `u0a292`: 196 mAh estimated total, including 50.4 mAh CPU and 139 mAh screen; 37m 38s top state and 66m 24s foreground CPU time in the current statistics window. | **High impact** — the screen component is expected for a launcher, but CPU use is excessive. |
| Thermal state | CPU/GPU 64.9°C, battery 44.9°C, skin 52.0°C with severity 3. CPU/GPU hardware throttling starts at 85°C; skin severity may include charging and other system load. | **Immediate concern** — AEGIS materially adds heat, but this reading is not solely attributable to it. |

## Root Cause

The foreground neural HUD invalidates and redraws at display cadence. The recent visualization work layers several expensive operations into every frame:

1. Ten active `InfiniteTransition` channels keep the full-screen `Canvas` invalidating at the display rate in `NeuralAnimationState.kt`.
2. `AICoreVisualization.kt` draws both the new 3D mesh and the older complete 2D neural mesh in the same frame. Each mesh draws dense edges, many node circles, pulses, and glow passes.
3. `NeuralMeshDraw.kt` reprojects, depth-sorts, rebuilds paths, and emits the 3D edges/nodes/pulses every frame. The preallocated buffers correctly avoid heap churn but do not reduce CPU draw-command generation.
4. Animated rings and code-rain text add work to every full-screen frame. The static `home_bg` image is also composited behind this animated surface.

The existing `SLOW` setting only lengthens animation duration; it does **not** cap redraw cadence. Consequently it is not an effective CPU or battery-saving mode.

## Optimization Scope

### P0 — Required before further device acceptance

| Change | Expected impact | Verification |
|---|---|---|
| Remove or make optional the legacy 2D `drawBrainNeuralMesh` overlay; retain one neural mesh renderer. | Largest immediate reduction in draw commands and CPU. | Repeat 30-second `top` and `gfxinfo`; target under one core at normal mode. |
| Replace display-rate `InfiniteTransition` invalidation with one explicit animation clock capped by quality tier. Offer 30 FPS battery mode and 60 FPS maximum mode; never request 90 FPS by default. | Directly reduces CPU, HWUI work, heat, and battery consumption. | Confirm cadence in Perfetto/`gfxinfo`; no animation source invalidates above configured cap. |
| Enable an automatic static/low-power HUD when power save is on, battery is low, or thermal status is severe. | Prevents sustained heat and battery drain in adverse conditions. | Manually exercise power-save and thermal/battery test hooks. |
| Make the low-power profile the safe default until measurements pass. Keep the existing static/off setting available as an immediate user mitigation. | Stops the current default from exceeding device capacity. | Fresh install starts in the capped profile. |

### P1 — CPU and Frame-Pacing Improvements

| Change | Expected impact |
|---|---|
| Cache static ring/arc/mesh paths per size and color; split static and dynamic layers so static content is not rebuilt on animation ticks. | Less UI-thread display-list work. |
| Reduce dynamic primitives: fewer pulse edges, one glow pass per selected node, lower node density in battery mode, and no per-frame code rain outside high-quality mode. | Less RenderThread/HWUI load. |
| Avoid depth sorting when rotation is below a visual threshold or use a fixed/depth-bucket order. | Reduces per-frame CPU work in the 3D mesh. |
| Instrument named Compose trace sections around the neural renderer and render modes. | Makes regressions attributable to a concrete renderer operation. |

### P2 — RAM and Asset Improvements

| Change | Expected impact |
|---|---|
| Establish a launcher process budget using total PSS, native heap, and graphics memory—not JVM heap alone. | Makes the Phase 15 memory target meaningful on device. |
| Audit full-screen drawable dimensions and decoded memory; provide an appropriately sized WebP/AVIF background for 1080 × 2400 and avoid duplicate full-screen assets. | Reduces native/graphics memory and initial decode cost. |
| Use `dumpsys meminfo` after 30 minutes idle, repeated overlay open/close cycles, and rotation/process restart. | Distinguishes baseline footprint from a real leak. |

## Acceptance Gates for the Remediation

- Normal mode: median frame time <=16.7 ms, 95th percentile <=33.3 ms, janky frames <3%, and sustained AEGIS CPU <100% of one core on this device.
- Battery mode: <=30 FPS, sustained AEGIS CPU <50% of one core, with code rain and redundant pulse/glow layers disabled.
- Thermal: no severe skin thermal state during a 15-minute home-screen soak under otherwise comparable conditions.
- Memory: total PSS budget established and met after a 30-minute soak; no monotonic PSS increase across repeated overlay cycles.
- Battery: on battery, fixed brightness/network conditions, measure at least one hour and meet `PT-016-03` (<=1% battery per hour) or revise the target with measured evidence.

## Immediate User Mitigation

Until P0 lands, open Settings and select `OFF (STATIC)` under Radar Animation, or disable `RADAR DISPLAY`. Disconnecting USB/allowing the device to cool is advisable before further sustained testing because the current skin temperature is already in the severe range.

## Documentation Integrity Note

The authoritative `PROGRESS_TRACKER.md` and `NEXT_STEPS.md` are updated by this diagnosis. `README.md` and `PROJECT_PLAN.md` still contain older phase labels and should be normalized separately; neither should be used to override the tracker.
