# PHASE-07 — System & Battery Telemetry Dashboard

**Features:** F-006 (Battery Telemetry, P0) & F-008 (System Telemetry Dashboard, P1)
**Dependencies:** PHASE-02 (HUD Design System), PHASE-03 (HUD Home Screen), PHASE-06 (HUD Graphics)
**Status:** Implementation Complete — Build Verified (Device Validation Pending)
**Started:** 2026-11-21
**Completed:** 2026-11-21 (Build Verified)

---

## Objective

Deliver real-time battery and system telemetry to the AEGIS HUD dashboard:
1. Battery percentage, charging state, temperature, health, and plug type via `Intent.ACTION_BATTERY_CHANGED` sticky broadcast.
2. Network connectivity state, transport type (Wi-Fi, Cellular, Ethernet), meteredness, and offline indicator via `ConnectivityManager.NetworkCallback`.
3. Genuine system uptime tracking via `SystemClock.elapsedRealtime()`.
4. Strict enforcement of DEC-009: No fake telemetry. Missing or unavailable values display as "N/A" or "---".
5. No long-running background polling services; purely event-driven and lifecycle-aware.

---

## Sub-Phases

### F-006: Battery Telemetry (P0)

| ID | Title | Status |
|---|---|---|
| F-006-P1 | `Intent.ACTION_BATTERY_CHANGED` sticky broadcast integration | Implemented — Build Verified |
| F-006-P2 | Battery percentage display composable | Implemented — Build Verified |
| F-006-P3 | Charging state indicator (charging / full / discharging) | Implemented — Build Verified |
| F-006-P4 | Battery temperature display | Implemented — Build Verified |
| F-006-P5 | Unavailable value handling | Implemented — Build Verified |

### F-008: System Telemetry Dashboard (P1)

| ID | Title | Status |
|---|---|---|
| F-008-P1 | Network connectivity state (ConnectivityManager) | Implemented — Build Verified |
| F-008-P2 | Wi-Fi or mobile indicator | Implemented — Build Verified |
| F-008-P3 | Telemetry panel composable (combines battery + network) | Implemented — Build Verified |
| F-008-P4 | Data freshness timestamp | Implemented — Build Verified |
| F-008-P5 | Unavailable / permission denied handling | Implemented — Build Verified |

---

## Acceptance Criteria

- [x] Battery percentage updates without background polling service (sticky broadcast in `AndroidBatteryRepository`).
- [x] Charging state accurately reflects Charging, Full, or Discharging (`BatteryNormalizer.mapChargingStatus`).
- [x] Battery temperature displays real °C or "---" when unavailable (`BatteryNormalizer` tenths to °C conversion).
- [x] Network status reflects real connectivity (Wi-Fi, Cellular, Offline via `ConnectivityManager.NetworkCallback`).
- [x] Real system uptime displayed from `SystemClock.elapsedRealtime()`.
- [x] No fake telemetry presented as real data (DEC-009).
- [x] Unit tests pass for battery and network normalization (8 tests across `BatteryNormalizerTest` and `NetworkNormalizerTest`).

---

## Unit Tests

- [x] `UT-006-02`: Platform-neutral battery values map to charging state correctly.
- [x] `UT-006-03`: Temperature converts from tenths of degrees to degrees.
- [x] `UT-006-04`: Missing platform-neutral values produce safe defaults.
- [x] `UT-008-02`: Fake network source produces the correct offline `NetworkState`.

---

## Files Created

- `docs/phases/PHASE-07-TELEMETRY.md`
- `app/src/main/kotlin/dev/ragnar/aegis/domain/telemetry/BatteryState.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/domain/telemetry/NetworkState.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/BatteryNormalizer.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/BatteryRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/AndroidBatteryRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/NetworkNormalizer.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/NetworkRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/data/telemetry/AndroidNetworkRepository.kt`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/telemetry/TelemetryViewModel.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/data/telemetry/BatteryNormalizerTest.kt`
- `app/src/test/kotlin/dev/ragnar/aegis/data/telemetry/NetworkNormalizerTest.kt`

## Files Modified

- `app/src/main/AndroidManifest.xml`
- `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt`

---

## Validation Record

| Date | Command | Result | Notes |
|---|---|---|---|
| 2026-11-21 | `./gradlew --no-daemon assembleDebug` | BUILD SUCCESSFUL (34s) | APK generated successfully |
| 2026-11-21 | `./gradlew --no-daemon testDebugUnitTest` | BUILD SUCCESSFUL (21s) | 34 tests passed (8 new telemetry tests) |
| 2026-11-21 | `./gradlew --no-daemon lintDebug` | BUILD SUCCESSFUL (44s) | 0 errors |
