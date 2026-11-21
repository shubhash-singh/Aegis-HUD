# PHASE-13 — Context-Aware HUD

**Feature:** F-013 (Context-Aware HUD)  
**Dependencies:** F-002 (HUD Design System), F-006 (Battery Telemetry), F-008 (System Telemetry), F-010 (Customization & DataStore)  
**Status:** Implementation Complete — Build Verified (Device Validation Pending)  
**Started:** 2026-11-22  
**Completed:** 2026-11-22 (Build Verified)  

---

## Objective

Automatically adapt the HUD display based on configurable, user-defined rules and device context (time of day, battery level, power-save status, network connectivity):
1. **Zero background services:** Evaluation is entirely reactive and computed on-device when the launcher is visible.
2. **Zero location data / zero restricted permissions:** Only uses existing public system broadcasts and clock state.
3. **Dynamic HUD adaptations:**
   - Override accent colors (e.g. Amber for Night Ops, Matrix Green for Morning, Tactical Crimson for Critical Battery).
   - Override animation speed / power-saver throttling (e.g. disable animations on critical battery).
   - Display cyber-tactical alert banners (e.g. COMM LINK OFFLINE or LOW POWER DEFENSE).
4. **Persistent user configuration:** Master toggle and individual protocol toggles stored in DataStore Preferences.
5. **Configurable in HUD Settings:** User can inspect current protocol statuses (`ACTIVE` vs `STANDBY`) and toggle each rule.

---

## Sub-Phases

| ID | Title | Scope | Status |
|---|---|---|---|
| **F-013-P1** | Rule Model Design | Define `ContextTrigger`, `ContextAction`, `ContextRule`, `ContextSnapshot`, and `ContextEvaluationResult`. | Implemented — Build Verified |
| **F-013-P2** | Rule Evaluation Engine | Pure Kotlin `ContextRuleEvaluator` sorting by priority with deterministic resolution; no background service or leaks. | Implemented — Build Verified |
| **F-013-P3** | Time-Based Rules | Time-of-day protocols (Night Ops: 22:00-06:00 Amber, Morning: 06:00-12:00 Green) with midnight hour wrap-around. | Implemented — Build Verified |
| **F-013-P4** | Battery-Level Rules | Critical battery threshold (≤15% Crimson HUD + animations off) and OS power-saver mode detection. | Implemented — Build Verified |
| **F-013-P5** | Network-State Rules | Network disconnected detection triggering tactical offline alert banner. | Implemented — Build Verified |
| **F-013-P6** | Settings UI Configuration | `SettingsSheet` section for Context-Aware HUD with master toggle and per-protocol toggles showing live `ACTIVE`/`STANDBY` state. | Implemented — Build Verified |
| **F-013-P7** | DataStore Persistence | DataStore Preferences storage for `contextAwareHudEnabled` and `enabledContextRules: Set<String>`. | Implemented — Build Verified |

---

## Architecture & Data Flow

```
[System Time / Clock] + [Battery Telemetry] + [Network Telemetry] + [PowerManager]
                             │
                             ▼
                    ContextSnapshot
                             │
                             ▼
                    ContextRuleEvaluator ◄── UserPreferences (enabled rules + master toggle)
                             │
                             ▼
                  ContextEvaluationResult
                             │
               ┌─────────────┴─────────────┐
               ▼                           ▼
      Dynamic HUD Overrides         Tactical HUD Alert Banner
      (effectiveAccent, speed)      (displayed on HomeScreen)
```

### 1. Domain Models (`dev.ragnar.aegis.domain.context`)

- `ContextTrigger`:
  - `TimeWindow(startHour: Int, endHour: Int)`
  - `BatteryLevelBelow(thresholdPercent: Int)`
  - `PowerSaveActive`
  - `NetworkDisconnected`
- `ContextAction`:
  - `SetAccent(accent: AccentColor)`
  - `SetAnimationSpeed(speed: AnimationSpeed)`
  - `ShowAlert(alertText: String)`
- `ContextRule`:
  - `id: String`, `name: String`, `description: String`, `trigger: ContextTrigger`, `actions: List<ContextAction>`, `priority: Int`
- `ContextSnapshot`:
  - `hourOfDay: Int`, `minute: Int`, `batteryPercent: Int`, `isPowerSaveMode: Boolean`, `isNetworkConnected: Boolean`
- `ContextEvaluationResult`:
  - `activeRuleIds: Set<String>`, `effectiveAccent: AccentColor?`, `effectiveAnimationSpeed: AnimationSpeed?`, `activeAlerts: List<String>`

### 2. Predefined Standard Protocols

1. **`rule_low_battery` (Priority 100):**
   - Trigger: Battery ≤ 15%
   - Actions: Tactical Crimson accent, animations disabled, alert banner `"CRITICAL BATTERY: CRIMSON DEFENSE ENGAGED"`.
2. **`rule_power_save` (Priority 80):**
   - Trigger: OS Power Save mode is active
   - Actions: Slow animation speed, alert banner `"SYSTEM IN POWER SAVE MODE"`.
3. **`rule_network_offline` (Priority 50):**
   - Trigger: Network disconnected
   - Actions: Alert banner `"COMM LINK OFFLINE — LOCAL CACHE MODE"`.
4. **`rule_night_ops` (Priority 40):**
   - Trigger: Time between 22:00 and 06:00
   - Actions: Alert Amber accent.
5. **`rule_morning_brief` (Priority 30):**
   - Trigger: Time between 06:00 and 12:00
   - Actions: Matrix Green accent.

---

## Acceptance Criteria

- [x] `ContextRuleEvaluator` evaluates mock time, battery, power save, and network inputs correctly without side effects.
- [x] Time-window evaluation correctly handles hour ranges wrapping around midnight (e.g. 22:00 to 06:00).
- [x] No rule configured or master toggle disabled produces default HUD state without alterations.
- [x] Context rules persist across launcher restarts via DataStore Preferences.
- [x] When an alert rule fires, a cyber-tactical alert banner appears on the home screen.
- [x] Zero background service, zero location permissions, zero battery drain while device is idle.
- [x] `./gradlew testDebugUnitTest` passes (11 new unit tests in `ContextRuleEvaluatorTest`, 98 total passing).
- [x] `./gradlew assembleDebug` passes.
- [x] `./gradlew lintDebug` passes with 0 errors.
