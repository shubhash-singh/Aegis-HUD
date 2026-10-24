# UI_UX_SPECIFICATION.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

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

### Home Screen Layout (Portrait)

```
┌─────────────────────────────────┐
│  STATUS BAR (system)            │
├─────────────────────────────────┤
│  [SYSTEM LABEL]    [CLOCK]      │  <- TopBar zone
│  RAGNAR // AEGIS   HH:MM:SS    │
│  [DATE]            [STATUS]     │
├─────────────────────────────────┤
│                                 │
│         [RADAR / HUD CENTER]    │  <- Center HUD zone
│                                 │
│   [TELEMETRY PANELS LEFT]       │
│   Battery: 87%   Network: WiFi  │
│                                 │
├─────────────────────────────────┤
│  [QUICK LAUNCH ROW]             │  <- Quick launch zone
│  [App] [App] [App] [App] [App]  │
├─────────────────────────────────┤
│  [SEARCH BAR]  [DRAWER BUTTON]  │  <- Bottom nav zone
├─────────────────────────────────┤
│  NAVIGATION BAR (system)        │
└─────────────────────────────────┘
```

### HUD Panel Anatomy

```
┌─┬───────────────────────────────┬─┐
│ │  [SECTION HEADER]             │ │   <- Corner ticks
│ │                               │ │
│ │  [CONTENT]                    │ │
│ │                               │ │
└─┴───────────────────────────────┴─┘
```
Panels use 1dp borders, 2dp corner radius, with corner tick decorations (6dp lines extending from corners outward).

---

## 6. Component Design

### HUD Panel

- 1dp border in `aegis.border` (inactive) or `aegis.border.active` (active/highlighted).
- 2dp corner radius.
- Corner tick marks (4dp lines at each corner, offset outward).
- Section header in `aegis.type.sectionHeader`, uppercase, `aegis.cyan.dim`.
- Background `aegis.surface`.

### Clock Display

- Font: JetBrains Mono, `aegis.type.clock`.
- Color: `aegis.cyan`.
- Seconds in `aegis.cyan.dim` or smaller weight.
- No shadow effects; glow achieved with subtle tint bloom if performance permits.

### Status Indicator Dot

- 6dp filled circle.
- Color: `aegis.status.ok` / `aegis.status.warning` / `aegis.status.critical` / `aegis.status.offline`.
- Subtle pulse animation for `ok` state (optional, configurable).

### App Icon Grid

- Standard Android launcher icon size: 48dp.
- No background, no shadow — icons on transparent background.
- App name below in `aegis.type.appName`, truncated with ellipsis.
- Grid: 4-5 columns depending on screen width.

### Radar Graphic (Phase 6)

- Concentric circles in `aegis.border` (dim, 0.3 alpha).
- Rotating sweep sector using Compose Canvas + `drawArc`.
- Measurement ticks on outer ring.
- Center reticle crosshair in `aegis.cyan`.
- Animation speed: configurable (default 4 seconds per rotation).
- Pause: when in battery-saver mode or by user setting.

---

## 7. Animation Guidelines

| Animation | Type | Duration | Easing | Notes |
|---|---|---|---|---|
| Radar sweep | Infinite rotation | 4s per revolution | Linear | Configurable; pausable |
| Scan line | Vertical translate | 3s top to bottom | Linear, repeat | Optional overlay |
| Status dot pulse | Scale 1.0 → 1.3 → 1.0 | 2s | EaseInOut | Only for OK state |
| Screen transitions | Fade + slide | 200ms | FastOutSlowIn | Navigation transitions |
| App launch ripple | Scale + fade | 150ms | EaseOut | Feedback on app tap |
| Panel appear | Fade in | 300ms | EaseOut | Initial screen load |

**Rules:**
- Never animate more than 2 major elements simultaneously.
- All animations must be interruptible.
- Respect `WindowManager.LayoutParams.FLAG_DIM_BEHIND` and `PowerManager` battery-saver state.
- Provide a global "reduce motion" or "disable animations" setting.

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
