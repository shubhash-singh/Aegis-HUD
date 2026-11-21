# UI_UX_SPECIFICATION.md — AEGIS HUD Launcher

**Version:** 0.2.0-redesign
**Last Updated:** 2026-11-21 (Phase 10 — Full-Screen AI Command Interface Redesign)

---

## 1. Design Philosophy

AEGIS must look and feel like a real technical engineering console, not a conventional launcher with neon colors on top. Every element should serve a functional or informational purpose. Decorative elements are permitted only when they reinforce the "live operational system" aesthetic without distracting from usability.

**Core Principles:**
- Dense but readable.
- Technical, not decorative.
- Alive but not chaotic.
- Dark by design, not by preference.
- No fake telemetry presented as real data.

---

## 2. Color Palette

### Base Colors

| Token | Hex | Usage |
|---|---|---|
| `aegis.background` | `#050A0E` | Primary background — near-black with very slight blue |
| `aegis.surface` | `#0A1520` | Panel surface — slightly lighter than background |
| `aegis.surface.elevated` | `#0F1E2E` | Elevated panels, overlays |
| `aegis.border` | `#1A3A5C` | Subtle structural borders |
| `aegis.border.active` | `#00C8FF` | Focused or highlighted border |
| `aegis.divider` | `#0D2035` | Fine divider lines |

### Accent Colors

| Token | Hex | Usage |
|---|---|---|
| `aegis.cyan` | `#00C8FF` | Primary accent — clocks, labels, highlights |
| `aegis.cyan.dim` | `#0090C0` | Dimmed cyan for secondary labels |
| `aegis.amber` | `#FFB830` | Secondary accent — warnings, alerts |
| `aegis.amber.dim` | `#C08020` | Dimmed amber |

### Status Colors

| Token | Hex | Usage |
|---|---|---|
| `aegis.status.ok` | `#00FF8C` | Healthy system state (battery OK, network OK) |
| `aegis.status.warning` | `#FF8C00` | Warning state (battery low, connectivity issue) |
| `aegis.status.critical` | `#FF2A2A` | Critical state (battery critical, error) |
| `aegis.status.offline` | `#4A6070` | Offline / unavailable |
| `aegis.status.unknown` | `#2A4050` | Data not yet received |

### Text Colors

| Token | Hex | Usage |
|---|---|---|
| `aegis.text.primary` | `#B0D8F0` | Body text and labels |
| `aegis.text.secondary` | `#607080` | Secondary labels, metadata |
| `aegis.text.accent` | `#00C8FF` | Highlighted or selected text |
| `aegis.text.warning` | `#FFB830` | Warning text |
| `aegis.text.muted` | `#304050` | Disabled or placeholder text |

---

## 3. Typography

### Typefaces

| Role | Typeface | Style | Usage |
|---|---|---|---|
| Technical mono | JetBrains Mono | Regular, Bold | Clock, telemetry values, hex codes |
| Technical mono | Source Code Pro | Regular | Fallback monospaced |
| UI labels | Inter | Medium, SemiBold | App names, labels, navigation |
| UI body | Inter | Regular | Descriptions, secondary text |

**Notes:**
- JetBrains Mono is included as a bundled font asset.
- Inter is included as a bundled font asset.
- No network font loading; all fonts are embedded in the APK.

### Type Scale (Compose TextStyle definitions)

| Style Token | Typeface | Size | Weight | Usage |
|---|---|---|---|---|
| `aegis.type.clock` | JetBrains Mono | 64sp | Bold | Main clock display |
| `aegis.type.clockSmall` | JetBrains Mono | 32sp | Regular | Secondary time elements |
| `aegis.type.telemetry` | JetBrains Mono | 14sp | Regular | Telemetry values |
| `aegis.type.telemetryLabel` | JetBrains Mono | 11sp | Regular | Telemetry labels |
| `aegis.type.appName` | Inter | 15sp | Medium | Application names |
| `aegis.type.body` | Inter | 14sp | Regular | General body text |
| `aegis.type.label` | Inter | 12sp | Medium | Small labels and captions |
| `aegis.type.sectionHeader` | Inter | 11sp | SemiBold | Section headers (uppercase) |

---

## 4. Spacing and Layout Tokens

| Token | Value | Usage |
|---|---|---|
| `spacing.xs` | 4dp | Tight padding inside components |
| `spacing.sm` | 8dp | Standard inner padding |
| `spacing.md` | 16dp | Standard section padding |
| `spacing.lg` | 24dp | Large section spacing |
| `spacing.xl` | 32dp | Major layout divisions |
| `spacing.xxl` | 48dp | Top-level screen margins |
| `panel.borderWidth` | 1dp | All HUD panel borders |
| `panel.cornerRadius` | 2dp | Minimal rounding — technical feel |
| `panel.tickLength` | 6dp | Corner tick mark length |

---

## 5. Layout and Screen Structure

### Home Screen Layout — Full-Screen AI Command Interface (Portrait)

> [!IMPORTANT]
> The home screen is a **full-screen AI command interface**. The neural visualization fills the entire screen behind all floating panels. There is no stacked card layout. No separate telemetry card row. No app shortcut row or search bar on the home screen.

```
┌────────────────────────────────────────┐
│  STATUS BAR (system — transparent)     │
├────────────────────────────────────────┤
│  RAGNAR // AEGIS    DATE    HH:MM:SS   │  ← Header (clock prominent)
│  [ONLINE ●]                            │
├────────────────────────────────────────┤
│ ┌──────────┐              ┌──────────┐ │
│ │AEGIS AT  │              │AI STATUS │ │  ← Floating panels (top L/R)
│ │  CORE    │              │  IDLE    │ │
│ └──────────┘              └──────────┘ │
│                                        │
│ ┌──────────┐  ╔══════════╗ ┌────────┐ │
│ │  SYSTEM  │  ║          ║ │DEVICE  │ │
│ │  PERF    │  ║  NEURAL  ║ │STATUS  │ │  ← Full-screen neural viz
│ │  CPU ▓▓▓ │  ║   BRAIN  ║ │TEMP    │ │     with floating L/R panels
│ │  RAM ▓▓░ │  ║          ║ │BATT    │ │
│ └──────────┘  ╚══════════╝ └────────┘ │
│                                        │
│ ┌──────────┐              ┌──────────┐ │
│ │INTELLIGE-│              │TELEMETRY │ │  ← Floating panels (lower L/R)
│ │  NCE     │              │BATT TEMP │ │
│ │CONTEXT✓  │              │UPTIME    │ │
│ │VOICE ✓   │              │VOLTAGE   │ │
│ └──────────┘              └──────────┘ │
│                                        │
│ ┌──────────────────────────────────┐   │
│ │  SYSTEM STATUS — NETWORK / STOR  │   │  ← Floating bottom panel
│ └──────────────────────────────────┘   │
│                                        │
│         ∧∧  SWIPE UP                   │  ← App drawer indicator
│        TO OPEN APP DRAWER              │
├────────────────────────────────────────┤
│  NAVIGATION BAR (system — transparent) │
└────────────────────────────────────────┘
```

### Full-Screen Neural Visualization Layers (bottom → top)

```
Layer 0: Near-black background (#050A0E)
Layer 1: Concentric scanning rings (Canvas)
Layer 2: Dynamic connection lines + data pulse animations
Layer 3: Floating particles
Layer 4: Rotating circular indicators
Layer 5: Vertical holographic projection beam
Layer 6: Circular energy platform (base)
Layer 7: Central holographic neural brain (dominant element)
Layer 8: Subtle flowing waveforms (horizontal)
Layer 9: Floating HUD information panels (semi-transparent overlay)
Layer 10: Header bar (clock, branding)
Layer 11: App drawer gesture indicator (bottom)
```

### Floating HUD Panel Anatomy

All information panels are floating holographic overlays — no opaque card backgrounds.

```
┌─┬───────────────────────────┬─┐
│ │  ▸ SECTION HEADER         │ │   ← 1dp cyan border, corner ticks
│ │                           │ │   ← Semi-transparent dark fill
│ │  KEY    VALUE             │ │   ← Monospaced compact content
│ │  KEY    ▓▓▓░░  VALUE      │ │   ← Mini progress bars
│ │                           │ │
└─┴───────────────────────────┴─┘
      ↑ subtle background blur
```

Panel rules:
- Background: `#050A0E` at 70–80% opacity (not fully opaque).
- Border: 1dp `aegis.border.active` (`#00C8FF`) cyan glow.
- Corner tick decorations: 6dp lines extending outward from each corner.
- Typography: JetBrains Mono, 11–13sp for labels, 13–15sp for values.
- No Material `Card` or `Surface` elevation shadows.
- `Modifier.blur()` used sparingly (only if device GPU permits, opt-out available).

---

## 6. Component Design

### Neural AI Core Visualization (`AICoreVisualization`)

The dominant visual element. Renders via Compose `Canvas`. Extends across the full screen behind all floating panels.

**Visual Elements:**

| Element | Description | Animation |
|---|---|---|
| Central holographic neural brain | Layered geometric node clusters, glowing | Slow pulse, rotation |
| Interconnected glowing nodes | Scattered across Canvas | Idle drift, connection activation |
| Dynamic connection lines | Lines between nodes with data packet travel | Packet travel along path |
| Data pulses | Bright dots racing along connection lines | Repeating along each edge |
| Floating particles | Tiny glowing points drifting upward | Constant gentle float |
| Concentric scanning rings | 4–6 large rings centered on brain | Slow expand + fade |
| Rotating circular indicators | Inner and outer rotating arc indicators | Continuous counter-rotation |
| Vertical holographic projection beam | Vertical column of light through brain center | Slow vertical pulse |
| Circular energy platform | Flat ring at base of brain | Slow rotation |
| Horizontal waveforms | Subtle sine waves extending left/right | Animated phase shift |

**Performance rules:**
- Static geometry (ring positions, node positions) must be computed once and cached — never per frame.
- Animation state lives in `InfiniteTransition` / `Animatable` — never recomputed in `drawWithContent`.
- `AICoreVisualization` composable must be isolated with stable parameters to prevent unnecessary recomposition when floating panel data updates.
- Pause or throttle all animations when `PowerManager.isPowerSaveMode` is `true`.
- No excessive `BlurMaskFilter` on every draw call — batch or avoid per-frame blur.

**Composable signature contract:**
```kotlin
@Composable
fun AICoreVisualization(
    modifier: Modifier = Modifier,
    animationSpeedMultiplier: Float = 1f, // 0f = paused
    accentColor: Color = AegisColors.cyan,
)
```

---

### Floating Information Panels

All six panel composables share these style properties:

| Property | Value |
|---|---|
| Background | `aegis.background` (#050A0E) @ 75% alpha |
| Border | 1dp `aegis.border.active` (#00C8FF) |
| Corner radius | 2dp |
| Corner ticks | 6dp outward lines at all 4 corners |
| Font | JetBrains Mono |
| Label size | 11sp, `aegis.text.secondary` |
| Value size | 13sp, `aegis.text.primary` or `aegis.cyan` |
| Padding | 8dp inner (`spacing.sm`) |
| Blur | Optional; must respect user "reduce effects" setting |

#### `FloatingCorePanel` — Top-left

Displays:
- `AEGIS AT CORE` label
- Application version string

#### `FloatingAiStatusPanel` — Top-right

Displays:
- `AI STATUS` label
- State: `IDLE` / `PROCESSING` / `ACTIVE`
- Color-coded: `aegis.text.secondary` for IDLE, `aegis.cyan` for PROCESSING, `aegis.status.ok` for ACTIVE
- Reflects actual `HomeViewModel` state

#### `FloatingPerformancePanel` — Left side

Displays:
- `SYSTEM PERFORMANCE` label
- CPU utilization % + small progress bar
- RAM utilization % + small progress bar

Data source: `SystemTelemetryRepository` (CPU via `/proc/stat`; RAM via `ActivityManager.MemoryInfo`)

#### `FloatingDevicePanel` — Right side

Displays:
- `DEVICE STATUS` label
- Temperature (°C) from `BatteryRepository`
- Battery percentage + charging state indicator

#### `FloatingIntelligencePanel` — Lower-left

Displays:
- `INTELLIGENCE` label
- `CONTEXT` — availability indicator
- `VOICE` — availability indicator
- `VISION` — availability indicator
- `AUTOMATION` — availability indicator

Indicators use `aegis.status.ok` (green) for available, `aegis.status.offline` (dim) for unavailable.

#### `FloatingTelemetryPanel` — Lower-right

Displays:
- `TELEMETRY` label
- Battery temperature (°C)
- Device uptime (formatted HH:MM:SS or Xd Xh Xm)
- Battery voltage (mV)

> [!IMPORTANT]
> This replaces the old separate Telemetry card. No separate card below the visualization.

#### `FloatingSystemStatusPanel` — Bottom (above nav gesture)

Displays:
- `SYSTEM STATUS` label
- Network connectivity: Wi-Fi / Mobile / Offline
- Network info: SSID (Wi-Fi) or carrier name (Mobile)
- Download speed (↓ kb/s or Mb/s)
- Upload speed (↑ kb/s or Mb/s)
- Storage: used / total formatted

> [!IMPORTANT]
> This replaces the old separate System Status card. It floats above the system navigation bar.

---

### App Drawer Gesture Indicator (`AppDrawerGestureIndicator`)

Replaces all previous bottom launcher rows, search bars, and APPS buttons.

Displays at the bottom of the home screen:
- Two upward-pointing cyan chevrons (`∧∧`)
- Text: `SWIPE UP`
- Text: `TO OPEN APP DRAWER`
- Subtle "breathing" alpha animation (1.0 → 0.5 → 1.0, 2–3s period)

Tapping the indicator also triggers the drawer open.

---

### App Drawer (Swipe-Up Gesture)

- Triggered by upward swipe from bottom of screen OR tap on `AppDrawerGestureIndicator`.
- Dismissed by swipe down or back gesture.
- Animates from bottom of screen upward (`AnimatedVisibility` with `slideInVertically` + `fadeIn`).
- Full-screen dark overlay with app grid + search bar inside the drawer.
- Must not conflict with system navigation gesture zone (apply `WindowInsets.navigationBars` padding).

---

### Header

Retained from current implementation:
- Android status bar (transparent, edge-to-edge)
- `RAGNAR // AEGIS` branding
- Current date
- Large digital clock (JetBrains Mono, `aegis.type.clock`, `aegis.cyan`)
- Online indicator dot

The clock must remain prominent — it is the primary time reference — but must not block the neural brain visualization center. Position in upper portion of screen.

---

### Removed Elements

> [!WARNING]
> The following UI elements are **removed** in this redesign. Do not re-implement them.

| Removed Element | Reason |
|---|---|
| App shortcut row (5 icons) | Replaced by swipe-up drawer |
| Assistant / Browser / Calculator / Calendar / Camera shortcuts | Replaced by swipe-up drawer |
| Search bar on home screen | Lives only inside drawer |
| APPS button / drawer open button | Replaced by gesture + indicator |
| Persistent bottom launcher dock | Replaced by gesture indicator |
| Separate stacked Telemetry card | Integrated into `FloatingTelemetryPanel` |
| Separate stacked System Status card | Integrated into `FloatingSystemStatusPanel` |
| Radar widget (isolated card) | Replaced by full-screen `AICoreVisualization` |

---

## 7. Animation Guidelines

| Animation | Type | Duration | Easing | Notes |
|---|---|---|---|---|
| Neural brain pulse | Scale 1.0 → 1.05 → 1.0 | 3s | EaseInOut | Continuous; pausable |
| Node activation | Brightness flash | 400ms | EaseOut | Triggered per node |
| Data pulse travel | Path animation | 1.5–3s per edge | Linear | Repeating per connection |
| Particle float | Y-axis drift + fade | 4–8s | Linear | Staggered; pooled |
| Scanning rings | Scale + fade out | 4s | EaseOut | Repeating concentric |
| Rotating indicators | Infinite rotation | 6s / 10s (inner/outer) | Linear | Counter-rotating |
| Projection beam | Alpha pulse | 2s | EaseInOut | Continuous |
| Energy platform | Rotation | 12s | Linear | Continuous |
| Waveform phase | Phase shift | 4s cycle | Linear | Continuous |
| Status dot pulse | Scale 1.0 → 1.3 → 1.0 | 2s | EaseInOut | Only for OK state |
| Screen transitions | Fade + slide | 200ms | FastOutSlowIn | Navigation transitions |
| App launch ripple | Scale + fade | 150ms | EaseOut | Feedback on app tap |
| Panel appear | Fade in | 300ms | EaseOut | Initial screen load |
| Drawer open | SlideInVertically + fade | 350ms | FastOutSlowIn | Swipe-up drawer |
| Drawer gesture indicator | Alpha breathe | 2.5s | EaseInOut | Continuous on home screen |

**Rules:**
- Never animate more than 3 major elements simultaneously using heavy Canvas draw calls per frame.
- All animations must be interruptible and pausable via user setting or battery-saver mode.
- Pause or reduce nonessential animation when `PowerManager.isPowerSaveMode` is true.
- Provide a global "reduce motion" / "disable animations" setting.
- Static geometry (node positions, ring radii) is computed once and cached — never on each frame.

---

## 8. System Insets and Navigation

- Always apply `WindowInsets.systemBars` padding to screen edges.
- Content must not be occluded by status bar, navigation bar, or system gestures.
- Use `Modifier.windowInsetsPadding(WindowInsets.systemBars)` on root screen composables.
- Full-screen edge-to-edge mode enabled via `enableEdgeToEdge()` in Activity `onCreate`.

---

## 9. Accessibility

- All interactive elements have a `contentDescription`.
- Minimum touch target: 48dp × 48dp.
- Text colors meet WCAG AA contrast on dark backgrounds.
- Clock and telemetry values are readable at system large font sizes (up to 130% scale).
- Animations can be globally disabled via settings.
- No color-only information encoding (status must also use text or icon, not only color).

---

## 10. Design Anti-Patterns to Avoid

- No plain Material card with a neon border as the entire "HUD" design.
- No excessive glow or blur that tanks performance.
- No animated elements that cannot be disabled.
- No simulated fake data labeled as real sensor readings.
- No design that only works at one screen density or aspect ratio.
- No heavy PNG assets where vector or Canvas drawing suffices.
- **No isolated radar widget inside a card** — the visualization fills the whole screen.
- **No stacked card layout** for telemetry and system status below the visualization.
- **No persistent app shortcut row on the home screen** — use swipe-up drawer only.
- **No fixed screen-coordinate positioning** — all panel placement must be responsive to screen dimensions.
- **No excessive object allocation inside `Canvas` draw lambdas** — cache `Paint`, `Path`, and geometry objects.
- **No blur (`BlurMaskFilter`) applied every frame** — batch or conditionally disable.
